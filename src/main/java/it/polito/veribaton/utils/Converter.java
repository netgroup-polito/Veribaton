package it.polito.veribaton.utils;

import it.polito.veribaton.model.*;
import org.openbaton.catalogue.mano.descriptor.InternalVirtualLink;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.mano.descriptor.VirtualLinkDescriptor;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
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

                    /*
                    NodeConstraints.NodeMetrics optional = new NodeConstraints.NodeMetrics();
                    optional.setOptional(true);
                    optional.setNode(vnf.getName());
                    nfv.getConstraints().getNodeConstraints().getNodeMetrics().add(optional);
                    */
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
                    /*
                    Host hc = new Host();
                    hc.setName("host-"+vnf.getName());
                    hc.setType(TypeOfHost.CLIENT);
                    hc.setFixedEndpoint(vnf.getName());
                    hc.setCores(2);
                    hc.setCpu(2);
                    hc.setMemory(2);
                    hc.setDiskStorage(2);
                    nfv.getHosts().getHost().add(hc);

                    Connection clientConn = new Connection();
                    clientConn.setAvgLatency(1);
                    clientConn.setSourceHost(hc.getName());
                    clientConn.setDestHost(middlebox.getName());
                    nfv.getConnections().getConnection().add(clientConn);
                    */
                    break;
                case WEBSERVER:
                    cfg.setWebserver(new Webserver());
                    cfg.getWebserver().setName(vnfd.getName());
                    /*
                    Host hs = new Host();
                    hs.setName("host-"+vnf.getName());
                    hs.setType(TypeOfHost.SERVER);
                    hs.setFixedEndpoint(vnf.getName());
                    hs.setCores(2);
                    hs.setCpu(2);
                    hs.setMemory(2);
                    hs.setDiskStorage(2);
                    nfv.getHosts().getHost().add(hs);

                    Connection serverConn = new Connection();
                    serverConn.setAvgLatency(1);
                    serverConn.setSourceHost(middlebox.getName());
                    serverConn.setDestHost(hs.getName());
                    nfv.getConnections().getConnection().add(serverConn);
                    */
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

                default:
                    throw new BadFormatException("VNF type not supported");

            }

            vnf.setConfiguration(cfg);
            graph.getNode().add(vnf);
            /*
            if (endhosts.size() > 1) {
                for (String srcendhost : endhosts) {
                    for (String dstendhost : endhosts) {
                        if (dstendhost != srcendhost) {
                            Property p = new Property();
                            p.setGraph(graph.getId());
                            p.setName(PName.REACHABILITY_PROPERTY);
                            p.setSrc(srcendhost);
                            p.setDst(dstendhost);
                        }
                    }
                }
            }
            */


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
}


