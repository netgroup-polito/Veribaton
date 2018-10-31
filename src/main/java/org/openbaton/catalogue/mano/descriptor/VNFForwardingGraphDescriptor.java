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

import org.openbaton.catalogue.mano.common.ConnectionPoint;
import org.openbaton.catalogue.mano.common.CostituentVNF;
import org.openbaton.catalogue.mano.common.Security;
import org.openbaton.catalogue.util.BaseEntity;

import java.util.Set;

/**
 * Created by lto on 05/02/15.
 *
 * <p>Based on ETSI GS NFV-MAN 001 V1.1.1 (2014-12)
 */
public class VNFForwardingGraphDescriptor extends BaseEntity {

    /**
     * Specify the vendor generating this VNFFG
     */
    private String vendor;
    /**
     * Specify the identifier (e.g. name), version, and description of service this VNFFG is
     * describing
     */
    private String version;
    /**
     * Count of the external endpoints (connection_point elements) included in this VNFFG, to form an
     * index
     */
    private int number_of_endpoints;
    /**
     * Count of the VLs (dependent_virtual_link elements) used by this VNFFG, to form an index
     */
    private int number_of_virtual_links;
    /**
     * Reference to a VLD (vld:id) used to instantiate this Forwarding Graph
     */
    private Set<VirtualLinkDescriptor> dependent_virtual_link;
    /**
     * This element describes a Network Forwarding Path within the VNFFG
     */
    private Set<NetworkForwardingPath> network_forwarding_path;
    /**
     * Reference to Connection Points (nsd/vnfd/pnfd:connection_point:id) forming the VNFFG including
     * Connection Points attached to PNFs
     */
    private Set<ConnectionPoint> connection_point;
    /**
     * Version of this VNFFGD
     */
    private String descriptor_version;
    /**
     * Reference to a VNFD (nsd:deployment_flavours:constituent_vnf:id) used to instantiate this VNF
     * Forwarding Graph
     */
    private Set<CostituentVNF> constituent_vnfs;
    /**
     * This is a signature of vnffgd to prevent tampering. The particular hash algorithm used to
     * compute the signature, together with the corresponding cryptographic certificate to validate
     * the signature should also be included
     */
    private Security vnffgd_security;

    public VNFForwardingGraphDescriptor() {
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getNumber_of_endpoints() {
        return number_of_endpoints;
    }

    public void setNumber_of_endpoints(int number_of_endpoints) {
        this.number_of_endpoints = number_of_endpoints;
    }

    public int getNumber_of_virtual_links() {
        return number_of_virtual_links;
    }

    public void setNumber_of_virtual_links(int number_of_virtual_links) {
        this.number_of_virtual_links = number_of_virtual_links;
    }

    public Set<VirtualLinkDescriptor> getDependent_virtual_link() {
        return dependent_virtual_link;
    }

    public void setDependent_virtual_link(Set<VirtualLinkDescriptor> dependent_virtual_link) {
        this.dependent_virtual_link = dependent_virtual_link;
    }

    public Set<NetworkForwardingPath> getNetwork_forwarding_path() {
        return network_forwarding_path;
    }

    public void setNetwork_forwarding_path(Set<NetworkForwardingPath> network_forwarding_path) {
        this.network_forwarding_path = network_forwarding_path;
    }

    public Set<ConnectionPoint> getConnection_point() {
        return connection_point;
    }

    public void setConnection_point(Set<ConnectionPoint> connection_point) {
        this.connection_point = connection_point;
    }

    public String getDescriptor_version() {
        return descriptor_version;
    }

    public void setDescriptor_version(String descriptor_version) {
        this.descriptor_version = descriptor_version;
    }

    public Set<CostituentVNF> getConstituent_vnfs() {
        return constituent_vnfs;
    }

    public void setConstituent_vnfs(Set<CostituentVNF> constituent_vnfs) {
        this.constituent_vnfs = constituent_vnfs;
    }

    public Security getVnffgd_security() {
        return vnffgd_security;
    }

    public void setVnffgd_security(Security vnffgd_security) {
        this.vnffgd_security = vnffgd_security;
    }

    @Override
    public String toString() {
        return "VNFForwardingGraphDescriptor{"
                + "vendor='"
                + vendor
                + '\''
                + ", version='"
                + version
                + '\''
                + ", number_of_endpoints="
                + number_of_endpoints
                + ", number_of_virtual_links="
                + number_of_virtual_links
                + ", dependent_virtual_link="
                + dependent_virtual_link
                + ", network_forwarding_path="
                + network_forwarding_path
                + ", connection_point="
                + connection_point
                + ", descriptor_version='"
                + descriptor_version
                + '\''
                + ", constituent_vnfs="
                + constituent_vnfs
                + ", vnffgd_security="
                + vnffgd_security
                + "} "
                + super.toString();
    }
}
