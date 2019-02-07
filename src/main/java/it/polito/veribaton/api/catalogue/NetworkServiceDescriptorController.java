/*
 * Copyright (c) 2015-2018 Open Baton (http://openbaton.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polito.veribaton.api.catalogue;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.polito.veribaton.model.*;
import it.polito.veribaton.utils.Converter;
import it.polito.veribaton.utils.LogWriter;
import org.openbaton.catalogue.mano.descriptor.*;
import org.openbaton.exceptions.BadFormatException;
import org.openbaton.exceptions.BadRequestException;
import org.openbaton.exceptions.NotFoundException;
import org.openbaton.sdk.NFVORequestor;
import org.openbaton.sdk.NfvoRequestorBuilder;
import org.openbaton.sdk.api.exception.SDKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/ns-descriptors")
@Api(tags = "Network Service Descriptors")
public class NetworkServiceDescriptorController {

    @Value("${openbaton.host}")
    private String nfvHost;
    @Value("${openbaton.port}")
    private Integer nfvPort;
    @Value("${openbaton.username}")
    private String nfvUser;
    @Value("${openbaton.password}")
    private String nfvPassword;
    @Value("${openbaton.ssl}")
    private Boolean nfvSslEnabled;
    @Value("${verifoo.scheme}")
    private String verifooScheme;
    @Value("${verifoo.host}")
    private String verifooHost;
    @Value("${verifoo.port}")
    private Integer verifooPort;
    @Value("${verifoo.baseUri}")
    private String verifooBaseUri;
    @Value("${verifoo.deploymentUri}")
    private String verifooDeploymentUri;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * This operation allows submitting and validating a Network Service Descriptor (NSD), including
     * any related VNFFGD and VLD.
     *
     * @param networkServiceDescriptor : the Network Service Descriptor to be created
     * @return networkServiceDescriptor: the Network Service Descriptor filled with id and values from
     * core
     */
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
            value = "Add a Network Service Descriptor",
            notes = "POST request with Network Service Descriptor as JSON content of the request body")
    public NetworkServiceDescriptor create(
            @RequestBody @Valid NetworkServiceDescriptor networkServiceDescriptor,
            HttpServletResponse response,
            @RequestHeader(value = "project-id") String projectId) throws BadRequestException {
        log.trace("Just Received: " + networkServiceDescriptor);

        try {

            NFV nfv = Converter.ETSIToVerifo(networkServiceDescriptor, 0L);

            log.info("Converted input JSON to verifoo format");
            LogWriter.logXml(nfv, "log/nfv.xml");

            String uri = verifooScheme + "://" + verifooHost + ":" + verifooPort + verifooBaseUri + verifooDeploymentUri;
            RestTemplate restTemplate = new RestTemplate();

            log.info("Contacting verifoo...");
            NFV result = restTemplate.postForObject(uri, nfv, NFV.class);
            log.info("Verifoo response received");

            LogWriter.logXml(result, "log/nfvResp.xml");

            HashSet<String> connections = new HashSet<String>();

              /*  for (VirtualNetworkFunctionDescriptor vnfd : networkServiceDescriptor.getVnfd()) {
                    if (!result.getGraphs().getGraph().get(0).getNode().stream().filter(t -> t.getName().equals(vnfd.getName())).findFirst().isPresent()) {
                        networkServiceDescriptor.getVnfd().remove(vnfd);
                    }
                }*/
              /*
            for (NodeConstraints.NodeMetrics nm : nfv.getConstraints().getNodeConstraints().getNodeMetrics()) {
                if (nm.isOptional()) {
                    Host middlebox = nfv.getHosts().getHost().stream().filter(t -> t.getName().equals("middlebox")).findFirst().get();
                    //if an optional node is not present in the list of nodes deployed on the middlebox then remove it
                    if (!middlebox.getNodeRef().stream().filter(t -> t.getNode().equals(nm.getNode())).findFirst().isPresent()) {
                        //find the specified node and remove it in etsi model
                        networkServiceDescriptor.getVnfd().removeIf(t -> t.getName().equals(nm.getNode()));

                        //find and remove it in verifoo definition
                        result.getGraphs().getGraph().get(0).getNode().removeIf(t -> t.getName().equals(nm.getNode()));

                        //find and remove it from node neighbours
                        for (Node n : result.getGraphs().getGraph().get(0).getNode()) {
                            n.getNeighbour().removeIf(t -> t.getName().equals(nm.getNode()));
                        }
                    }
                }
            }
            */

            LogWriter.logXml(result, "log/nfvRespModified.xml");

            for (Node node : result.getGraphs().getGraph().get(0).getNode()) {
                VirtualNetworkFunctionDescriptor currentVnfd = networkServiceDescriptor.getVnfd().stream().filter(t -> t.getName().equals(node.getName())).findFirst().get();
                //empty virtual link
                currentVnfd.setVirtual_link(new HashSet<>());
                //empty connection points
                currentVnfd.getVdu().forEach(t -> t.getVnfc().forEach(l -> l.setConnection_point(new HashSet<>())));

                for (Neighbour nodeNeighbour : node.getNeighbour()) {
                    //add in alphabetical order to connections
                    String vlink = node.getName().compareToIgnoreCase(nodeNeighbour.getName()) > 0 ? nodeNeighbour.getName() + node.getName() : node.getName() + nodeNeighbour.getName();
                    connections.add(vlink);
                    InternalVirtualLink vl = new InternalVirtualLink();
                    vl.setName(vlink);
                    currentVnfd.getVirtual_link().add(vl);
                    VNFDConnectionPoint cp = new VNFDConnectionPoint();
                    cp.setVirtual_link_reference(vlink);
                    currentVnfd.getVdu().forEach(t -> t.getVnfc().forEach(l -> l.getConnection_point().add(cp)));
                }
            }

            networkServiceDescriptor.setVld(new HashSet<>());
            for (String link : connections) {
                VirtualLinkDescriptor vld = new VirtualLinkDescriptor();
                vld.setName(link);
                networkServiceDescriptor.getVld().add(vld);
            }

            LogWriter.logJson(networkServiceDescriptor, "log/nsd.json");

            log.info("Contacting Openbaton...");
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            NetworkServiceDescriptor creationResponse = requestor.getNetworkServiceDescriptorAgent().create(networkServiceDescriptor);
            log.info("Openbaton response received");
            return requestor.getNetworkServiceDescriptorAgent().findById(creationResponse.getId());
            //networkServiceDescriptor.setId(creationResponse.getId());
            //return creationResponse;
            //response.setHeader("X-Header", "TEST");
        } catch (HttpClientErrorException restex) {
            switch (restex.getStatusCode()) {
                case NOT_FOUND:
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, restex.getResponseBodyAsString());

                case BAD_REQUEST:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, restex.getResponseBodyAsString());

                default:
                    throw new ServerErrorException(restex.getStatusCode() + " " + restex.getResponseBodyAsString(), restex);
            }

        } catch (HttpServerErrorException restex) {
            throw new ServerErrorException(restex.getStatusCode() + " " + restex.getResponseBodyAsString(), restex);
        } catch (ResourceAccessException ioexc) {
            throw new ServerErrorException(ioexc.getMessage(), ioexc);
        } catch (SDKException nfvoex) {
            throw new ServerErrorException("Unable to perform operation on NFVO", nfvoex);
        } catch (BadFormatException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * This operation is used to remove a disabled Network Service Descriptor
     *
     * @param id of Network Service Descriptor
     */
    @ApiOperation(
            value = "Remove a Network Service Descriptor",
            notes = "DELETE request where the id in the url belongs to the NSD to delete")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") String id, @RequestHeader(value = "project-id") String projectId) {
        try {
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();


            requestor.getNetworkServiceDescriptorAgent().delete(id);
        } catch (SDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a list Network Service Descriptor from the NSDs Repository
     *
     * @param ids: the list of the ids
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @RequestMapping(
            value = "/multipledelete",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Remove multiple Network Service Descriptors",
            notes = "Delete Request takes a list of Network Service Descriptor ids")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void multipleDelete(
            @RequestBody @Valid List<String> ids, @RequestHeader(value = "project-id") String projectId) {

        try {
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();


            for (String id : ids) requestor.getNetworkServiceDescriptorAgent().delete(id);
        } catch (SDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * This operation returns the list of Network Service Descriptor (NSD)
     *
     * @return List<NetworkServiceDescriptor>: the list of Network Service Descriptor stored
     */
    @ApiOperation(
            value = "Get all NSDs from a project",
            notes =
                    "Returns all Network Service Descriptors onboarded in the project with the specified id")
    @RequestMapping(method = RequestMethod.GET)
    public List<NetworkServiceDescriptor> findAll(
            @RequestHeader(value = "project-id") String projectId) {
        try {
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            return requestor.getNetworkServiceDescriptorAgent().findAll();

        } catch (SDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * This operation returns the Network Service Descriptor (NSD) selected by id
     *
     * @param id of Network Service Descriptor
     * @return NetworkServiceDescriptor: the Network Service Descriptor selected @
     */
    @ApiOperation(
            value = "Get Network Service Descriptor by id",
            notes = "Returns the Network Service Descriptor with the id in the URL")
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public NetworkServiceDescriptor findById(
            @PathVariable("id") String id, @RequestHeader(value = "project-id") String projectId)
            throws NotFoundException {
        NFVORequestor requestor;
        try {
            requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

        } catch (SDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        NetworkServiceDescriptor nsd = null;
        try {
            nsd = requestor.getNetworkServiceDescriptorAgent().findById(id);
        } catch (SDKException e) {
            e.printStackTrace();
        }
        if (nsd == null)
            throw new NotFoundException("Did not find a Network Service Descriptor with ID " + id);
        return nsd;
    }

    /**
     * This operation updates the Network Service Descriptor (NSD)
     *
     * @param networkServiceDescriptor : the Network Service Descriptor to be updated
     * @param id                       : the id of Network Service Descriptor
     * @return networkServiceDescriptor: the Network Service Descriptor updated
     */
    @ApiOperation(
            value = "Update a Network Service Descriptor",
            notes =
                    "Takes a Network Service Descriptor and updates the Descriptor with the id provided in the URL with the Descriptor from the request body")
    @RequestMapping(
            value = "{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NetworkServiceDescriptor update(
            @RequestBody @Valid NetworkServiceDescriptor networkServiceDescriptor,
            @PathVariable("id") String id,
            @RequestHeader(value = "project-id") String projectId) {

        try {
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            return requestor.getNetworkServiceDescriptorAgent().update(networkServiceDescriptor, id);

        } catch (SDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
