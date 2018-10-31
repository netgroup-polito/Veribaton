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

import org.openbaton.catalogue.util.BaseEntity;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Based on ETSI GS NFV-MAN 001 V1.1.1 (2014-12)
 */
public class VNFComponent extends BaseEntity {

    /**
     * Describes network connectivity between a VNFC instance (based on this VDU) and an internal
     * Virtual Link.
     */
    @NotNull
    protected Set<VNFDConnectionPoint> connection_point;

    public VNFComponent() {
        this.connection_point = new HashSet<>();
    }

    @Override
    public String toString() {
        return "VNFComponent{" + "connection_point=" + connection_point + "} " + super.toString();
    }

    public Set<VNFDConnectionPoint> getConnection_point() {
        return connection_point;
    }

    public void setConnection_point(Set<VNFDConnectionPoint> connection_point) {
        this.connection_point = connection_point;
    }
}
