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

package org.openbaton.catalogue.mano.descriptor;

import org.openbaton.catalogue.mano.common.*;
import org.openbaton.catalogue.nfvo.Configuration;
import org.openbaton.catalogue.nfvo.RequiresParameters;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.TypeConstraintException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VirtualNetworkFunctionDescriptor extends NFVEntityDescriptor {
    /**
     * Version of the VNF Descriptor.
     */
    // private String descriptor_version;
    private Set<LifecycleEvent> lifecycle_event;

    private Configuration configurations;
    /**
     * This describes a set of elements related to a particular VDU
     */
    @NotNull
    @Size(min = 1)
    private Set<VirtualDeploymentUnit> vdu;
    /**
     * Represents the type of network connectivity mandated by the VNF vendor between two or more
     * Connection Point
     */
    private Set<InternalVirtualLink> virtual_link;
    /**
     * Describe dependencies between VDUs. Defined in terms of source and target VDU, i.e. target VDU
     * "depends on" source VDU. In other words sources VDU shall exists before target VDU can be
     * initiated/deployed.
     */
    private Set<VDUDependency> vdu_dependency;
    /**
     * Represents the assurance parameter(s) and its requirement for each deployment flavour of the
     * VNF being described, see clause 6.3.1.5.
     */

    private Set<VNFDeploymentFlavour> deployment_flavour;
    /**
     * The VNF package may contain a file that lists all files in the package. This can be useful for
     * auditing purposes or for enabling some security features on the package. TODO consider having a
     * stream of a pointer to a file
     */
    private String manifest_file;
    /**
     * The manifest file may be created to contain a digest of each file that it lists as part of the
     * package. This digest information can form the basis of a security mechanism to ensure the
     * contents of the package meet certain security related properties. If the manifest file contains
     * digests of the files in the package, then the manifest file should also note the particular
     * hash algorithm used to enable suitable verification mechanisms. Examples of suitable hash
     * algorithms include, but are not limited to SHA-256, SHA-384, SHA-512, and SHA-3. In conjunction
     * with an appropriate security signing mechanism, which may include having a security certificate
     * as part of the VNF package, the digest information can be used to help ensure the contents of
     * the VNF package have not been tampered with.
     */
    private Set<Security> manifest_file_security;

    private String type;

    private String endpoint;
    private String vnfPackageLocation;

    private Map<String, RequiresParameters> requires;

    private Set<String> provides;

    private Boolean cyclicDependency = false;

    private String createdAt;

    private String updatedAt;

    // NFVO Version
    private String nfvo_version;

    public VirtualNetworkFunctionDescriptor() {
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNfvo_version() {
        return nfvo_version;
    }

    public void setNfvo_version(String nfvo_version) {
        this.nfvo_version = nfvo_version;
    }

    public Configuration getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Configuration configurations) {
        this.configurations = configurations;
    }

    public Boolean isCyclicDependency() {
        return cyclicDependency;
    }

    public void setCyclicDependency(Boolean cyclicDependency) {
        this.cyclicDependency = cyclicDependency;
    }

    @Override
    public String toString() {
        return "VirtualNetworkFunctionDescriptor{"
                + "lifecycle_event="
                + lifecycle_event
                + ", configurations="
                + configurations
                + ", vdu="
                + vdu
                + ", virtual_link="
                + virtual_link
                + ", vdu_dependency="
                + vdu_dependency
                + ", deployment_flavour="
                + deployment_flavour
                + ", manifest_file='"
                + manifest_file
                + '\''
                + ", manifest_file_security="
                + manifest_file_security
                + ", type='"
                + type
                + '\''
                + ", endpoint='"
                + endpoint
                + '\''
                + ", vnfPackageLocation='"
                + vnfPackageLocation
                + '\''
                + ", requires="
                + requires
                + ", provides="
                + provides
                + ", cyclicDependency="
                + cyclicDependency
                + ", createdAt='"
                + createdAt
                + '\''
                + ", updatedAt='"
                + updatedAt
                + '\''
                + ", nfvo_version='"
                + nfvo_version
                + '\''
                + "} "
                + super.toString();
    }

    public Map<String, RequiresParameters> getRequires() {
        return requires;
    }

    public void setRequires(Map<String, RequiresParameters> requires) {
        this.requires = requires;
    }

    public Set<String> getProvides() {
        return provides;
    }

    public void setProvides(Set<String> provides) {
        this.provides = provides;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean hasCyclicDependency() {
        return cyclicDependency;
    }

    @Override
    public Set<ConnectionPoint> getConnection_point() {
        return connection_point;
    }

    @Override
    public void setConnection_point(Set<ConnectionPoint> connection_point) {
        for (ConnectionPoint cp : connection_point)
            if (!(cp instanceof VNFDConnectionPoint))
                throw new TypeConstraintException(
                        "Connection Point "
                                + cp.getId()
                                + " field must be an instance of "
                                + ConnectionPoint.class.getCanonicalName());
        this.connection_point = connection_point;
    }

    public Set<VNFDConnectionPoint> getVNFDConnection_point() {
        Set<VNFDConnectionPoint> res = new HashSet<>();
        for (ConnectionPoint cp : connection_point) res.add((VNFDConnectionPoint) cp);
        return res;
    }

    public Set<VirtualDeploymentUnit> getVdu() {
        return vdu;
    }

    public void setVdu(Set<VirtualDeploymentUnit> vdu) {
        this.vdu = vdu;
    }

    public Set<InternalVirtualLink> getVirtual_link() {
        return virtual_link;
    }

    public void setVirtual_link(Set<InternalVirtualLink> virtual_link) {
        this.virtual_link = virtual_link;
    }

    public Set<VDUDependency> getVdu_dependency() {
        return vdu_dependency;
    }

    public void setVdu_dependency(Set<VDUDependency> vdu_dependency) {
        this.vdu_dependency = vdu_dependency;
    }

    public Set<VNFDeploymentFlavour> getDeployment_flavour() {
        return deployment_flavour;
    }

    public void setDeployment_flavour(Set<VNFDeploymentFlavour> deployment_flavour) {
        this.deployment_flavour = deployment_flavour;
    }

    public String getManifest_file() {
        return manifest_file;
    }

    public void setManifest_file(String manifest_file) {
        this.manifest_file = manifest_file;
    }

    public Set<Security> getManifest_file_security() {
        return manifest_file_security;
    }

    public void setManifest_file_security(Set<Security> manifest_file_security) {
        this.manifest_file_security = manifest_file_security;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVnfPackageLocation() {
        return vnfPackageLocation;
    }

    public void setVnfPackageLocation(String vnfPackageLocation) {
        this.vnfPackageLocation = vnfPackageLocation;
    }

    public Set<LifecycleEvent> getLifecycle_event() {
        return lifecycle_event;
    }

    public void setLifecycle_event(Set<LifecycleEvent> lifecycle_event) {
        this.lifecycle_event = lifecycle_event;
    }
}
