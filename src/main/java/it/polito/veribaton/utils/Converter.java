package it.polito.veribaton.utils;

import it.polito.veribaton.model.*;
import org.openbaton.catalogue.mano.descriptor.*;
import org.openbaton.catalogue.nfvo.ConfigurationParameter;
import org.openbaton.exceptions.BadFormatException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Converter is a utility class for object conversion from Verifoo format to Openbaton and back
 */
public class Converter {

    /**
     * ETSIToVerifo converts a NSD into a Verifoo NFV
     *
     * @param nsd the nsd in ETSI format
     * @return a verifoo NFV instance
     * @throws BadFormatException when conversion is not possible
     */
    public static NFV ETSIToVerifo(NetworkServiceDescriptor nsd) throws BadFormatException {

        //init all objects to be used
        NFV nfv = new NFV();
        nfv.setGraphs(new Graphs());
        nfv.setPropertyDefinition(new PropertyDefinition());
        nfv.setConstraints(new Constraints());
        nfv.getConstraints().setNodeConstraints(new NodeConstraints());
        nfv.getConstraints().setLinkConstraints(new LinkConstraints());
        Graph graph = new Graph();
        graph.setId(0L);
        //create a map of network names and a set of nodes connected to the networks
        HashMap<String, Set<String>> networks = new HashMap<String, Set<String>>();
        //create a map of nodes and a set of neighbouring nodes
        HashMap<String, Set<String>> adjMatrix = new HashMap<String, Set<String>>();

        //creates networks
        for (VirtualLinkDescriptor vl : nsd.getVld()) {
            networks.put(vl.getName(), new HashSet<>());
        }

        //iterate over nfv VNFS
        for (VirtualNetworkFunctionDescriptor vnfd : nsd.getVnfd()) {
            //create a node and its entry in the adjacency matrix
            Node vnf = new Node();
            adjMatrix.put(vnfd.getName(), new HashSet<>());
            // set node name
            vnf.setName(vnfd.getName());
            //set functional type based on the VNF enum (can throw BadFormatException)
            vnf.setFunctionalType(FunctionalTypes.valueOf(vnfd.getType()));
            //add this VNF to each network it is connected to
            for (InternalVirtualLink vl : vnfd.getVirtual_link()) {
                if (networks.get(vl.getName()) != null) {
                    networks.get(vl.getName()).add(vnfd.getName());
                } else {
                    //throw exception when link name is not defined
                    throw new BadFormatException("Virtual link " + vl.getName() + " not defined in vld section");
                }
            }

            //create configuration and name it after nsd, or else 'unnamed'
            Configuration cfg = new Configuration();
            org.openbaton.catalogue.nfvo.Configuration vnfdConf = vnfd.getConfigurations();
            if (vnfdConf != null) {
                if (vnfdConf.getName() != null) {
                    cfg.setName(vnfdConf.getName());
                } else {
                    cfg.setName("unnamed");
                }
            } else {
                cfg.setName("unnamed");
            }

            //set VNF configuration
            if (vnfdConf != null) {
                if (vnfdConf.getConfigurationParameters() != null) {

                    //iterate over config parameters
                    for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
                        //if param name is 'optional', set node optionality
                        if (vnfdConfP.getConfKey().equals("optional")) {
                            if (vnfdConfP.getValue().equalsIgnoreCase("true")) {
                                NodeConstraints.NodeMetrics optional = new NodeConstraints.NodeMetrics();
                                optional.setOptional(true);
                                optional.setNode(vnf.getName());
                                nfv.getConstraints().getNodeConstraints().getNodeMetrics().add(optional);
                            }
                            if (vnfdConfP.getValue().equalsIgnoreCase("false")) {
                                NodeConstraints.NodeMetrics optional = new NodeConstraints.NodeMetrics();
                                optional.setOptional(false);
                                optional.setNode(vnf.getName());
                                nfv.getConstraints().getNodeConstraints().getNodeMetrics().add(optional);
                            }
                        }
                        //if param name is 'canReach' set reachability property for the node
                        if (vnfdConfP.getConfKey().equals("canReach")) {
                            Property p = new Property();
                            p.setGraph(graph.getId());
                            p.setName(PName.REACHABILITY_PROPERTY);
                            p.setSrc(vnf.getName());
                            p.setDst(vnfdConfP.getValue());
                            nfv.getPropertyDefinition().getProperty().add(p);
                        }

                        //if param name is 'cannotReach' set isolation property for the node
                        if (vnfdConfP.getConfKey().equals("cannotReach")) {
                            Property p = new Property();
                            p.setGraph(graph.getId());
                            p.setName(PName.ISOLATION_PROPERTY);
                            p.setSrc(vnf.getName());
                            p.setDst(vnfdConfP.getValue());
                            nfv.getPropertyDefinition().getProperty().add(p);
                        }
                    }
                }
            }

            // create config based on functional type
            switch (vnf.getFunctionalType()) {
                case ANTISPAM:
                    //set antispam sources based on parameters beginning with 'source'
                    cfg.setAntispam(new Antispam());
                    if (vnfdConf != null) {
                        if (vnfdConf.getConfigurationParameters() != null) {
                            for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
                                if (vnfdConfP.getConfKey().startsWith("source")) {
                                    cfg.getAntispam().getSource().add(vnfdConfP.getValue());
                                }
                            }
                        }
                    }
                    break;
                case DPI:
                    //set dpi not llowed strings based on parameters beginning with 'notAllowed'
                    cfg.setDpi(new Dpi());
                    if (vnfdConf != null) {
                        if (vnfdConf.getConfigurationParameters() != null) {
                            for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
                                if (vnfdConfP.getConfKey().startsWith("notAllowed")) {
                                    cfg.getDpi().getNotAllowed().add(vnfdConfP.getValue());
                                }
                            }
                        }
                    }
                    break;
                case NAT:
                    //set nat sources based on parameters beginning with 'source'
                    cfg.setNat(new Nat());
                    if (vnfdConf != null) {
                        if (vnfdConf.getConfigurationParameters() != null) {
                            for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
                                if (vnfdConfP.getConfKey().startsWith("source")) {
                                    cfg.getNat().getSource().add(vnfdConfP.getValue());
                                }
                            }
                        }
                    }
                    break;
                case CACHE:
                    cfg.setCache(new Cache());
                    break;
                case ENDHOST:
                    cfg.setEndhost(new Endhost());
                    break;
                case VPNEXIT:
                    cfg.setVpnexit(new Vpnexit());
                    break;
                case ENDPOINT:
                    cfg.setEndpoint(new Endpoint());
                    break;
                case FIREWALL:
                    // firewall configurations
                    cfg.setFirewall(new Firewall());
                    Optional<String> def = getParamValue(vnfdConf, "defaultAction");
                    if (def.isPresent()) {
                        if (def.get().equalsIgnoreCase("allow")) {
                            cfg.getFirewall().setDefaultAction(ActionTypes.ALLOW);
                        }

                        if (def.get().equalsIgnoreCase("deny")) {
                            cfg.getFirewall().setDefaultAction(ActionTypes.DENY);
                        }
                    }
                    /*if (vnfdConf != null) {
                        if (vnfdConf.getConfigurationParameters() != null) {
                            for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
                                if (vnfdConfP.getConfKey().equals("defaultAction")) {
                                    if (vnfdConfP.getValue().equalsIgnoreCase("allow")) {
                                        cfg.getFirewall().setDefaultAction(ActionTypes.ALLOW);
                                    }
                                    if (vnfdConfP.getValue().equalsIgnoreCase("deny")) {
                                        cfg.getFirewall().setDefaultAction(ActionTypes.DENY);
                                    }
                                }
                            }
                        }
                    }*/

                    break;
                case VPNACCESS:
                    cfg.setVpnaccess(new Vpnaccess());
                    break;
                case WEBCLIENT:
                    // webclient configurations
                    cfg.setWebclient(new Webclient());
                    Optional<String> nameWS = getParamValue(vnfdConf, "nameWebServer");
                    nameWS.ifPresent(s -> cfg.getWebclient().setNameWebServer(s));

  /*                  if (vnfdConf != null) {
                        if (vnfdConf.getConfigurationParameters() != null) {
                            for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
                                if (vnfdConfP.getConfKey().equals("nameWebServer")) {
                                    cfg.getWebclient().setNameWebServer(vnfdConfP.getValue());

                                }
                            }
                        }
                    }*/

                    break;
                case WEBSERVER:
                    cfg.setWebserver(new Webserver());
                    cfg.getWebserver().setName(vnfd.getName());
                    break;
                case MAILCLIENT:
                    cfg.setMailclient(new Mailclient());
                    break;
                case MAILSERVER:
                    cfg.setMailserver(new Mailserver());
                    break;
                case FIELDMODIFIER:
                    cfg.setFieldmodifier(new Fieldmodifier());
                    break;
                case FORWARDER:
                    cfg.setForwarder(new Forwarder());
                    break;
                default:
                    // the type is not present in the list
                    throw new BadFormatException("VNF type not supported");

            }

            vnf.setConfiguration(cfg);
            graph.getNode().add(vnf);
        }

        //set adj matrix
        // iterate over entry sets in the network map
        for (Map.Entry<String, Set<String>> belongingTo : networks.entrySet()) {
            // iterate over each vnf in the list of nodes belonging to a specified network
            for (String vnf : belongingTo.getValue()) {
                // add to adjacency matrix to the key corresponding to current vnf all nodes in the same network that are not the current vnf
                adjMatrix.get(vnf).addAll(belongingTo.getValue().stream().filter(t -> !t.equals(vnf)).collect(Collectors.toSet()));
            }
        }

        // iterate over nodes and set as neighbours all entries of the adjacency matrix
        for (Node n : graph.getNode()) {
            Set<String> nodeNeighbors = adjMatrix.get(n.getName());
            for (String neighborName : nodeNeighbors) {
                Neighbour nb = new Neighbour();
                nb.setName(neighborName);
                n.getNeighbour().add(nb);
            }
        }

        nfv.getGraphs().getGraph().add(graph);
        nfv.setParsingString("");

        return nfv;
    }

    /**
     * VerifooToETSI converts a NFV object to ETSI Openbaton format
     *
     * @param nsd the original nsd to be modified using information taken from the nfv
     * @param nfv Verifoo NFV deployment response
     * @return a modified nsd instance
     */
    public static NetworkServiceDescriptor VerifooToETSI(NetworkServiceDescriptor nsd, NFV nfv) {
        //iterate over optional nodes
        for (NodeConstraints.NodeMetrics nm : nfv.getConstraints().getNodeConstraints().getNodeMetrics()) {
            if (nm.isOptional()) {
                //if an optional node is not present in the list of nodes returned from verifoo then remove it from the nsd
                if (nfv.getGraphs().getGraph().get(0).getNode().stream().noneMatch(t -> t.getName().equals(nm.getNode()))) {
                    //find the specified node and remove it in etsi model
                    nsd.getVnfd().removeIf(t -> t.getName().equals(nm.getNode()));
                }
            }
        }

        //remove networks and add a single vlink
        nsd.setVld(new HashSet<>());
        VirtualLinkDescriptor vld = new VirtualLinkDescriptor();
        vld.setName("vlink");
        nsd.getVld().add(vld);
        for (Node node : nfv.getGraphs().getGraph().get(0).getNode()) {
            VirtualNetworkFunctionDescriptor currentVnfd = nsd.getVnfd().stream().filter(t -> t.getName().equals(node.getName())).findFirst().get();
            //empty virtual link
            currentVnfd.setVirtual_link(new HashSet<>());
            //empty connection points
            currentVnfd.getVdu().forEach(t -> t.getVnfc().forEach(l -> l.setConnection_point(new HashSet<>())));

            //create internal vl and add connection point to vdu
            InternalVirtualLink vl = new InternalVirtualLink();
            vl.setName("vlink");
            currentVnfd.getVirtual_link().add(vl);
            VNFDConnectionPoint cp = new VNFDConnectionPoint();
            cp.setVirtual_link_reference("vlink");
            currentVnfd.getVdu().forEach(t -> t.getVnfc().forEach(l -> l.getConnection_point().add(cp)));

            //eventually create configuration objects
            if (currentVnfd.getConfigurations() == null) {
                currentVnfd.setConfigurations(new org.openbaton.catalogue.nfvo.Configuration());
            }
            if (currentVnfd.getConfigurations().getConfigurationParameters() == null) {
                currentVnfd.getConfigurations().setConfigurationParameters(new HashSet<>());
            }

            //if firewall, configure it
            switch (node.getFunctionalType()) {

                case FIREWALL:
                    Firewall fw = node.getConfiguration().getFirewall();
                    //check if firewall is configured and import configuration into NSD
                    if (fw != null) {
                        if (fw.getDefaultAction() != null) {
                            // if defaultAction is present in NFV and not present in ETSI, set it
                            if (!getParamValue(currentVnfd.getConfigurations(), "defaultAction").isPresent()) {
                                ConfigurationParameter defAction = new ConfigurationParameter();
                                defAction.setConfKey("defaultAction");
                                if (fw.getDefaultAction().equals(ActionTypes.ALLOW)) {
                                    defAction.setValue("allow");
                                }
                                if (fw.getDefaultAction().equals(ActionTypes.DENY)) {
                                    defAction.setValue("deny");
                                }

                                currentVnfd.getConfigurations().getConfigurationParameters().add(defAction);
                            }
                        }
                        //autoconfig value is not imported if fw config is not empty
                        if (!getParamValue(currentVnfd.getConfigurations(), "allow").isPresent()) {
                            if (!getParamValue(currentVnfd.getConfigurations(), "deny").isPresent()) {
                                //build allow and deny strings as {src1},{dst1};{src2},{dst2}
                                StringBuilder allow = new StringBuilder();
                                StringBuilder deny = new StringBuilder();
                                for (Elements e : fw.getElements()) {
                                    if (e.getAction().equals(ActionTypes.ALLOW)) {
                                        if (allow.length() > 0) allow.append(";");
                                        allow.append(e.getSource()).append(",").append(e.getDestination());
                                    }
                                    if (e.getAction().equals(ActionTypes.DENY)) {
                                        if (deny.length() > 0) deny.append(";");
                                        deny.append(e.getSource()).append(",").append(e.getDestination());
                                    }
                                }
                                //add configuration parameters
                                ConfigurationParameter allowPar = new ConfigurationParameter(), denyPar = new ConfigurationParameter();
                                allowPar.setConfKey("allow");
                                allowPar.setValue(allow.toString());
                                denyPar.setConfKey("deny");
                                denyPar.setValue(deny.toString());

                                currentVnfd.getConfigurations().getConfigurationParameters().add(allowPar);
                                currentVnfd.getConfigurations().getConfigurationParameters().add(denyPar);
                            }
                        }
                    }

                    break;
                case ENDHOST:
                    break;
                case ENDPOINT:
                    break;
                case ANTISPAM:
                    break;
                case CACHE:
                    break;
                case DPI:
                    break;
                case MAILCLIENT:
                    break;
                case MAILSERVER:
                    break;
                case NAT:
                    break;
                case VPNACCESS:
                    break;
                case VPNEXIT:
                    break;
                case WEBCLIENT:
                    break;
                case WEBSERVER:
                    break;
                case FIELDMODIFIER:
                    break;
                case FORWARDER:
                    break;
            }
        }

        return nsd;
    }


    /**
     * getParamValue is a convenience method returning an Optional containing the value of the configuration parameter specified
     *
     * @param cfg  the Openbaton configuration object
     * @param name the name of the config parameter
     * @return the parameter value or an empty optional if it cannot be retrieved
     */
    private static Optional<String> getParamValue(org.openbaton.catalogue.nfvo.Configuration cfg, String name) {
        if (cfg == null) return Optional.empty();
        if (cfg.getConfigurationParameters() == null) return Optional.empty();
        Optional<ConfigurationParameter> param = cfg.getConfigurationParameters().stream().filter(t -> t.getConfKey().equals(name)).findFirst();
        return param.map(ConfigurationParameter::getValue);
    }
}


