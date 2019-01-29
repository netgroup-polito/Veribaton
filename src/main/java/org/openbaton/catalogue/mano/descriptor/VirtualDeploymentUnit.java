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

import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.util.BaseEntity;

import javax.validation.constraints.Min;
import java.util.Set;

// import javax.persistence.CascadeType;

/**
 * Based on ETSI GS NFV-MAN 001 V1.1.1 (2014-12)
 */
public class VirtualDeploymentUnit extends BaseEntity {
    /**
     * A unique identifier of this VDU within the scope of the VNFD, including version functional *
     * description and other identification information. This will be used to refer to VDU when
     * defining relationships between them.
     */
    private String name;
    /**
     * This provides a reference to a VM image NOTE: A cardinality of zero allows for creating empty
     * virtualisation containers as per (ETSI GS NFV-SWA 001 [i.8]).
     */
    private Set<String> vm_image;
    /**
     * Reference to the VDU (vnfd:vdu:id) used to instantiate this element.
     */
    private String parent_vdu;
    /**
     * Describe the required computation resources characteristics (e.g. processing power, number of
     * virtual CPUs, etc.), including Key Quality Indicators (KQIs) for performance and
     * reliability/availability.
     */
    private String computation_requirement;
    /**
     * This represents the virtual memory needed for the VDU.
     */
    private String virtual_memory_resource_element;
    /**
     * This represents the requirements in terms of the virtual network bandwidth needed for the VDU.
     */
    private String virtual_network_bandwidth_resource;
    /**
     * Defines VNF component functional scripts/workflows for specific lifecycle events(e.g.
     * initialization, termination, graceful shutdown, scaling out/in).
     */

    private Set<LifecycleEvent> lifecycle_event;
    /**
     * Placeholder for other constraints.
     */
    private String vdu_constraint;

    /**
     * Defines minimum and maximum number of instances which can be created to support scale out/in.
     */
    @Min(1)
    private Integer scale_in_out;

    /**
     * Contains information that is distinct for each VNFC created based on this VDU.
     */
    private Set<VNFComponent> vnfc;

    private Set<VNFCInstance> vnfc_instance;
    /**
     * Monitoring parameter, which can be tracked for a VNFC based on this VDU. Examples include:
     * memory-consumption, CPU-utilisation, bandwidth-consumption, VNFC downtime, etc.
     */
    private Set<String> monitoring_parameter;

    private String hostname;

    private Set<String> vimInstanceName;

    public VirtualDeploymentUnit() {
    }

    public String getParent_vdu() {
        return parent_vdu;
    }

    public void setParent_vdu(String parent_vdu) {
        this.parent_vdu = parent_vdu;
    }

    public Set<VNFCInstance> getVnfc_instance() {
        return vnfc_instance;
    }

    public void setVnfc_instance(Set<VNFCInstance> vnfc_instance) {
        this.vnfc_instance = vnfc_instance;
    }

    public Set<String> getVm_image() {
        return vm_image;
    }

    public void setVm_image(Set<String> vm_image) {
        this.vm_image = vm_image;
    }

    public String getComputation_requirement() {
        return computation_requirement;
    }

    public void setComputation_requirement(String computation_requirement) {
        this.computation_requirement = computation_requirement;
    }

    public String getVirtual_memory_resource_element() {
        return virtual_memory_resource_element;
    }

    public void setVirtual_memory_resource_element(String virtual_memory_resource_element) {
        this.virtual_memory_resource_element = virtual_memory_resource_element;
    }

    public String getVirtual_network_bandwidth_resource() {
        return virtual_network_bandwidth_resource;
    }

    public void setVirtual_network_bandwidth_resource(String virtual_network_bandwidth_resource) {
        this.virtual_network_bandwidth_resource = virtual_network_bandwidth_resource;
    }

    public Set<LifecycleEvent> getLifecycle_event() {
        return lifecycle_event;
    }

    public void setLifecycle_event(Set<LifecycleEvent> lifecycle_event) {
        this.lifecycle_event = lifecycle_event;
    }

    public String getVdu_constraint() {
        return vdu_constraint;
    }

    public void setVdu_constraint(String vdu_constraint) {
        this.vdu_constraint = vdu_constraint;
    }

    public Integer getScale_in_out() {
        return scale_in_out;
    }

    public void setScale_in_out(Integer scale_in_out) {
        this.scale_in_out = scale_in_out;
    }

    public Set<VNFComponent> getVnfc() {
        return vnfc;
    }

    public void setVnfc(Set<VNFComponent> vnfc) {
        this.vnfc = vnfc;
    }

    public Set<String> getMonitoring_parameter() {
        return monitoring_parameter;
    }

    public void setMonitoring_parameter(Set<String> monitoring_parameter) {
        this.monitoring_parameter = monitoring_parameter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getVimInstanceName() {
        return vimInstanceName;
    }

    public void setVimInstanceName(Set<String> vimInstanceName) {
        this.vimInstanceName = vimInstanceName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "VirtualDeploymentUnit{"
                + "name='"
                + name
                + '\''
                + ", vm_image="
                + vm_image
                + ", parent_vdu='"
                + parent_vdu
                + '\''
                + ", computation_requirement='"
                + computation_requirement
                + '\''
                + ", virtual_memory_resource_element='"
                + virtual_memory_resource_element
                + '\''
                + ", virtual_network_bandwidth_resource='"
                + virtual_network_bandwidth_resource
                + '\''
                + ", lifecycle_event="
                + lifecycle_event
                + ", vdu_constraint='"
                + vdu_constraint
                + '\''
                + ", scale_in_out="
                + scale_in_out
                + ", vnfc="
                + vnfc
                + ", vnfc_instance="
                + vnfc_instance
                + ", monitoring_parameter="
                + monitoring_parameter
                + ", hostname='"
                + hostname
                + '\''
                + ", vimInstanceName="
                + vimInstanceName
                + "} "
                + super.toString();
    }
}