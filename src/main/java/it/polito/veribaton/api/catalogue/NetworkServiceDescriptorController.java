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
import it.polito.veribaton.exceptions.InvalidGraphException;
import it.polito.veribaton.exceptions.UnsatisfiedPropertyException;
import it.polito.veribaton.model.NFV;
import it.polito.veribaton.model.Property;
import it.polito.veribaton.utils.Converter;
import it.polito.veribaton.utils.LogWriter;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
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
import java.util.List;

/**
 * NetworkServiceDescriptorController is the class handling requests related to Netowrk Service lifecycle.
 * All requests receive the header 'project' as a reference to the Openbaton top hierarchy element
 */
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
            //convert the input received in openbaton form to verifoo XML
            NFV nfv = Converter.ETSIToVerifo(networkServiceDescriptor);

            log.info("Converted input JSON to verifoo format");
            LogWriter.logXml(nfv, "log/nfv.xml");

            //build verifoo deployment URL
            String uri = verifooScheme + "://" + verifooHost + ":" + verifooPort + verifooBaseUri + verifooDeploymentUri;
            RestTemplate restTemplate = new RestTemplate();

            log.info("Contacting verifoo...");
            //post NFV object to verifoo deployment service endpoint
            NFV result = restTemplate.postForObject(uri, nfv, NFV.class);
            log.info("Verifoo response received");

            //upon response, if any property is not satisfied throw exception
            if (result.getPropertyDefinition() != null) {
                for (Property p : result.getPropertyDefinition().getProperty()) {
                    if (!p.isIsSat()) {
                        throw new UnsatisfiedPropertyException(p.getName().value());
                    }
                }
            }

            LogWriter.logXml(result, "log/nfvResp.xml");

            //convert back verifoo format into openbaton for catalog upload
            NetworkServiceDescriptor finalNSD = Converter.VerifooToETSI(networkServiceDescriptor, result);

            LogWriter.logJson(finalNSD, "log/nsd.json");

            log.info("Contacting Openbaton...");
            //create openbaton client
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            //upload NSD in openbaton catalog
            NetworkServiceDescriptor creationResponse = requestor.getNetworkServiceDescriptorAgent().create(finalNSD);
            log.info("Openbaton response received");

            //return the newly created descriptor
            return requestor.getNetworkServiceDescriptorAgent().findById(creationResponse.getId());

        }
        //handle verifoo http client errors
        catch (HttpClientErrorException restex) {
            if (restex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, restex.getResponseBodyAsString());
                //otherwise throu a server error
            }
            throw new ServerErrorException(restex.getStatusCode() + " " + restex.getResponseBodyAsString(), restex);
        }
        // handle connection issues for http client such as conn refused
        catch (HttpServerErrorException restex) {
            throw new ServerErrorException(restex.getStatusCode() + " " + restex.getResponseBodyAsString(), restex);
        }
        // handle io exceptions for resource access
        catch (ResourceAccessException ioexc) {
            throw new ServerErrorException(ioexc.getMessage(), ioexc);
        }
        // handle errors coming from Openbaton connection
        catch (SDKException nfvoex) {
            throw new ServerErrorException("Unable to perform operation on NFVO", nfvoex);
        }
        // catch openbaton format errors
        catch (BadFormatException e) {
            throw new BadRequestException(e.getMessage());
        }
        // handle invalid graph properties
        catch (UnsatisfiedPropertyException e) {
            throw new BadRequestException(new InvalidGraphException(e));
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
            // open Openbaton connection
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            //request a delete on specified ID
            requestor.getNetworkServiceDescriptorAgent().delete(id);
        }
        // catch Openbaton errors
        catch (SDKException e) {
            throw new ServerErrorException("Unable to perform operation on NFVO", e);
        }
    }

    /**
     * Removes a list Network Service Descriptor from the NSDs Repository
     *
     * @param ids: the list of the ids
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
            // open Openbaton connection
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            //request a delete on specified IDs
            for (String id : ids) requestor.getNetworkServiceDescriptorAgent().delete(id);
        }
        // catch Openbaton errors
        catch (SDKException e) {
            throw new ServerErrorException("Unable to perform operation on NFVO", e);
        }
    }

    /**
     * This operation returns the list of Network Service Descriptors (NSD)
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
            // open Openbaton connection from client
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            //get all NSDs and return them
            return requestor.getNetworkServiceDescriptorAgent().findAll();

        }
        // catch Openbaton errors
        catch (SDKException e) {
            throw new ServerErrorException("Unable to perform operation on NFVO", e);
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
            // open Openbaton connection from client
            requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

        }
        //catch connection problems
        catch (SDKException e) {
            throw new ServerErrorException("Unable to perform operation on NFVO", e);
        }

        NetworkServiceDescriptor nsd = null;
        try {
            //find specified id
            nsd = requestor.getNetworkServiceDescriptorAgent().findById(id);
        } catch (SDKException e) {
            e.printStackTrace();
        }
        //if nothing is found return 404
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
            //open connection to Openbaton
            NFVORequestor requestor = NfvoRequestorBuilder.create()
                    .nfvoIp(nfvHost)
                    .nfvoPort(nfvPort)
                    .username(nfvUser)
                    .password(nfvPassword)
                    .projectName(projectId)
                    .sslEnabled(nfvSslEnabled)
                    .version("1")
                    .build();

            //request NSD update
            return requestor.getNetworkServiceDescriptorAgent().update(networkServiceDescriptor, id);

        }
        // catch Openbaton errors
        catch (SDKException e) {
            throw new ServerErrorException("Unable to perform operation on NFVO", e);
        }
    }
}
