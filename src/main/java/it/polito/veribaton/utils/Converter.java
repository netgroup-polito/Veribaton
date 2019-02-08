package it.polito.veribaton.utils;

import it.polito.veribaton.model.*;
import org.openbaton.catalogue.mano.descriptor.*;
import org.openbaton.catalogue.nfvo.ConfigurationParameter;
import org.openbaton.exceptions.BadFormatException;

import java.util.*;
import java.util.stream.Collectors;

public class Converter {

    public static NFV ETSIToVerifo(NetworkServiceDescriptor nsd, Long id) throws BadFormatException {

        NFV nfv = new NFV();
        nfv.setGraphs(new Graphs());
        //nfv.setHosts(new Hosts());
        //nfv.setConnections(new Connections());
        nfv.setPropertyDefinition(new PropertyDefinition());
        nfv.setConstraints(new Constraints());
        nfv.getConstraints().setNodeConstraints(new NodeConstraints());
        nfv.getConstraints().setLinkConstraints(new LinkConstraints());
        Graph graph = new Graph();
        graph.setId(id);
        HashMap<String, Set<String>> networks = new HashMap<String, Set<String>>();
        HashMap<String, Set<String>> adjMatrix = new HashMap<String, Set<String>>();
        HashSet<String> endhosts = new HashSet<String>();


        //create networks
        for (VirtualLinkDescriptor vl : nsd.getVld()) {
            networks.put(vl.getName(), new HashSet<>());
        }

        //create sink host where all vnfs will go
        /*
        Host middlebox = new Host();
        middlebox.setName("middlebox");
        middlebox.setType(TypeOfHost.MIDDLEBOX);
        middlebox.setCores(1000);
        middlebox.setCpu(1000);
        middlebox.setMemory(1000);
        middlebox.setDiskStorage(1000);
        middlebox.getSupportedVNF().addAll(getMiddleboxSupportedVNFs());
        nfv.getHosts().getHost().add(middlebox);
        */
        for (VirtualNetworkFunctionDescriptor vnfd : nsd.getVnfd()) {
            Node vnf = new Node();
            adjMatrix.put(vnfd.getName(), new HashSet<>());
            //vnf.setId((long) vnfd.getName().hashCode());
            vnf.setName(vnfd.getName());
            vnf.setFunctionalType(FunctionalTypes.valueOf(vnfd.getType()));
            for (InternalVirtualLink vl : vnfd.getVirtual_link()) {
                if (networks.get(vl.getName()) != null) {
                    networks.get(vl.getName()).add(vnfd.getName());
                } else {
                    throw new BadFormatException("Virtual link " + vl.getName() + " not defined in vld section");
                }
            }

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

            if (vnfdConf != null) {
                if (vnfdConf.getConfigurationParameters() != null) {
                    for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
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
                        if (vnfdConfP.getConfKey().equals("canReach")) {
                            Property p = new Property();
                            p.setGraph(graph.getId());
                            p.setName(PName.REACHABILITY_PROPERTY);
                            p.setSrc(vnf.getName());
                            p.setDst(vnfdConfP.getValue());
                            nfv.getPropertyDefinition().getProperty().add(p);
                        }
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

            switch (vnf.getFunctionalType()) {
                case ANTISPAM:
                    cfg.setAntispam(new Antispam());
                    break;
                case DPI:
                    cfg.setDpi(new Dpi());
                    break;
                case NAT:
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
                    //endhosts.add(vnf.getName());
                    break;
                case VPNEXIT:
                    cfg.setVpnexit(new Vpnexit());
                    break;
                case ENDPOINT:
                    cfg.setEndpoint(new Endpoint());
                    break;
                case FIREWALL:
                    cfg.setFirewall(new Firewall());
                    if (vnfdConf != null) {
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
                    }

                    break;
                case VPNACCESS:
                    cfg.setVpnaccess(new Vpnaccess());
                    break;
                case WEBCLIENT:
                    cfg.setWebclient(new Webclient());
                    if (vnfdConf != null) {
                        if (vnfdConf.getConfigurationParameters() != null) {
                            for (ConfigurationParameter vnfdConfP : vnfdConf.getConfigurationParameters()) {
                                if (vnfdConfP.getConfKey().equals("nameWebServer")) {
                                    cfg.getWebclient().setNameWebServer(vnfdConfP.getValue());

                                }
                            }
                        }
                    }

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
                    throw new BadFormatException("VNF type not supported");

            }

            vnf.setConfiguration(cfg);
            graph.getNode().add(vnf);


        }

        //set adj matrix
        for (Map.Entry<String, Set<String>> belongingTo : networks.entrySet()) {
            for (String vnf : belongingTo.getValue()) {
                adjMatrix.get(vnf).addAll(belongingTo.getValue().stream().filter(t -> !t.equals(vnf)).collect(Collectors.toSet()));
            }
        }

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

    public static NetworkServiceDescriptor VerifooToETSI(NetworkServiceDescriptor nsd, NFV nfv) throws BadFormatException {
        //iterate over optional nodes
        for (NodeConstraints.NodeMetrics nm : nfv.getConstraints().getNodeConstraints().getNodeMetrics()) {
            if (nm.isOptional()) {
                //if an optional node is not present in the list of nodes returned from verifoo then remove it from the nsd
                if (!nfv.getGraphs().getGraph().get(0).getNode().stream().filter(t -> t.getName().equals(nm.getNode())).findFirst().isPresent()) {
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
                            if (!getParamValue(currentVnfd.getConfigurations(), "defaultAction").isPresent()) {
                                ConfigurationParameter defAction = new ConfigurationParameter();
                                defAction.setConfKey("defaultAction");
                                if (fw.getDefaultAction().equals(ActionTypes.ALLOW)){
                                    defAction.setValue("allow");
                                }
                                if (fw.getDefaultAction().equals(ActionTypes.DENY)){
                                    defAction.setValue("deny");
                                }

                                currentVnfd.getConfigurations().getConfigurationParameters().add(defAction);
                            }
                        }
                        //autoconfig is not imported if fw config is not empty
                        if (!getParamValue(currentVnfd.getConfigurations(), "allow").isPresent()) {
                            if (!getParamValue(currentVnfd.getConfigurations(), "deny").isPresent()) {
                                //build allow and deny strings as {src1},{dst1};{src2},{dst2}
                                String allow = "", deny = "";
                                for (Elements e : fw.getElements()) {
                                    if (e.getAction().equals(ActionTypes.ALLOW)) {
                                        if (!allow.isEmpty()) allow += ";";
                                        allow += e.getSource()+","+e.getDestination();
                                    }
                                    if (e.getAction().equals(ActionTypes.DENY)) {
                                        if (!deny.isEmpty()) deny += ";";
                                        deny += e.getSource()+","+e.getDestination();
                                    }
                                }
                                //add configuration parameters
                                ConfigurationParameter allowPar = new ConfigurationParameter(), denyPar  = new ConfigurationParameter();
                                allowPar.setConfKey("allow");
                                allowPar.setValue(allow);
                                denyPar.setConfKey("deny");
                                denyPar.setValue(deny);

                                currentVnfd.getConfigurations().getConfigurationParameters().add(allowPar);
                                currentVnfd.getConfigurations().getConfigurationParameters().add(denyPar);
                            }
                        }
                    }

                    break;
            }
        }



        return nsd;
    }

    private static List<SupportedVNFType> getMiddleboxSupportedVNFs() {
        List<SupportedVNFType> list = new LinkedList<>();
        SupportedVNFType antispam = new SupportedVNFType();
        antispam.setFunctionalType(FunctionalTypes.ANTISPAM);
        list.add(antispam);

        SupportedVNFType cache = new SupportedVNFType();
        cache.setFunctionalType(FunctionalTypes.CACHE);
        list.add(cache);

        SupportedVNFType dpi = new SupportedVNFType();
        dpi.setFunctionalType(FunctionalTypes.DPI);
        list.add(dpi);

        SupportedVNFType fieldmodifier = new SupportedVNFType();
        fieldmodifier.setFunctionalType(FunctionalTypes.FIELDMODIFIER);
        list.add(fieldmodifier);

        SupportedVNFType fw = new SupportedVNFType();
        fw.setFunctionalType(FunctionalTypes.FIREWALL);
        list.add(fw);

        SupportedVNFType nat = new SupportedVNFType();
        nat.setFunctionalType(FunctionalTypes.NAT);
        list.add(nat);

        return list;
    }

    private static Optional<String> getParamValue(org.openbaton.catalogue.nfvo.Configuration cfg, String name) {
        if (cfg == null) return Optional.empty();
        if (cfg.getConfigurationParameters() == null) return Optional.empty();
        Optional<ConfigurationParameter> param = cfg.getConfigurationParameters().stream().filter(t->t.getConfKey().equals("name")).findFirst();
        if (param.isPresent()) {
            return Optional.of(param.get().getValue());
        } else return Optional.empty();
    }
}


