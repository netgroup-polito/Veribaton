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

package org.openbaton.catalogue.mano.common;

import org.openbaton.catalogue.util.BaseEntity;

import java.util.Set;

/**
 * Created by lto on 05/02/15.
 *
 * <p>Based on ETSI GS NFV-MAN 001 V1.1.1 (2014-12)
 *
 * <p>The VLD describes the basic topology of the connectivity (e.g. E-LAN, E-Line, E-Tree) between
 * one or more VNFs connected to this VL and other required parameters (e.g. bandwidth and QoS
 * class). The VLD connection parameters are expected to have similar attributes to those used on
 * the ports on VNFs in ETSI GS NFV-SWA 001 [i.8]. Therefore a set of VLs in a Network Service can
 * be mapped to a Network Connectivity Topology (NCT) as defined in ETSI GS NFV-SWA 001 [i.8].
 */

public abstract class AbstractVirtualLink extends BaseEntity {

    /**
     * extId of the network to attach
     */
    protected String extId;
    /**
     * Name referenced by VNFCs
     */
    protected String name;
    /**
     * Throughput of the link (e.g. bandwidth of E-Line, root bandwidth of E-Tree, and aggregate
     * capacity of E-LAN)
     */
    private String root_requirement;
    /**
     * Throughput of leaf connections to the link (for E-Tree and E-LAN branches)
     */
    private String leaf_requirement;
    /**
     * QoS options available on the VL, e.g. latency, jitter, etc.
     */
    private Set<String> qos;
    /**
     * Test access facilities available on the VL (e.g. none, passive monitoring, or active
     * (intrusive) loopbacks at endpoints TODO think of using Enum instead of String
     */
    private Set<String> test_access;
    /**
     * Connectivity types, e.g. E-Line, E-LAN, or E-Tree. TODO: think of using Enum instead of String
     */
    private String connectivity_type;
    private String cidr;

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getRoot_requirement() {
        return root_requirement;
    }

    public void setRoot_requirement(String root_requirement) {
        this.root_requirement = root_requirement;
    }

    public String getLeaf_requirement() {
        return leaf_requirement;
    }

    public void setLeaf_requirement(String leaf_requirement) {
        this.leaf_requirement = leaf_requirement;
    }

    public Set<String> getQos() {
        return qos;
    }

    public void setQos(Set<String> qos) {
        this.qos = qos;
    }

    public Set<String> getTest_access() {
        return test_access;
    }

    public void setTest_access(Set<String> test_access) {
        this.test_access = test_access;
    }

    public String getConnectivity_type() {
        return connectivity_type;
    }

    public void setConnectivity_type(String connectivity_type) {
        this.connectivity_type = connectivity_type;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AbstractVirtualLink{"
                + "extId='"
                + extId
                + '\''
                + ", root_requirement='"
                + root_requirement
                + '\''
                + ", leaf_requirement='"
                + leaf_requirement
                + '\''
                + ", qos="
                + qos
                + ", test_access="
                + test_access
                + ", connectivity_type='"
                + connectivity_type
                + '\''
                + ", name='"
                + name
                + '\''
                + ", cidr='"
                + cidr
                + '\''
                + "} "
                + super.toString();
    }
}
