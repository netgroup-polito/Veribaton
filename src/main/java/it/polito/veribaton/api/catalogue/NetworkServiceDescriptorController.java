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

import io.swagger.annotations.ApiOperation;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
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

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/ns-descriptors")
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
            value = "Adding a Network Service Descriptor",
            notes = "POST request with Network Service Descriptor as JSON content of the request body")
    public NetworkServiceDescriptor create(
            @RequestBody @Valid NetworkServiceDescriptor networkServiceDescriptor,
            @RequestHeader(value = "project-id") String projectId) {
        NetworkServiceDescriptor nsd;
        log.trace("Just Received: " + networkServiceDescriptor);
        NFVORequestor requestor =
                null;
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


            requestor.getNetworkServiceDescriptorAgent().create(networkServiceDescriptor);
        } catch (SDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * This operation is used to remove a disabled Network Service Descriptor
     *
     * @param id of Network Service Descriptor
     */
    @ApiOperation(
            value = "Removing a Network Service Descriptor",
            notes = "DELETE request where the id in the url belongs to the NSD to delete")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") String id, @RequestHeader(value = "project-id") String projectId) {
        NFVORequestor requestor =
                null;
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
            value = "Removing multiple Network Service Descriptors",
            notes = "Delete Request takes a list of Network Service Descriptor ids")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void multipleDelete(
            @RequestBody @Valid List<String> ids, @RequestHeader(value = "project-id") String projectId) {

        NFVORequestor requestor =
                null;
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
        NFVORequestor requestor =
                null;
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

            return null;//requestor.getNetworkServiceDescriptorAgent().findAll();

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
        NFVORequestor requestor =
                null;
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

        NetworkServiceDescriptor nsd = null;//networkServiceDescriptorManagement.query(id, projectId);
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
        NFVORequestor requestor =
                null;
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

            return requestor.getNetworkServiceDescriptorAgent().update(networkServiceDescriptor, id);

        } catch (SDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //networkServiceDescriptorManagement.update(networkServiceDescriptor, projectId);
    }
}
