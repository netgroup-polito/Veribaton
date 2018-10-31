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

import org.openbaton.catalogue.mano.common.AbstractVirtualLink;

import java.util.Set;

/**
 * Created by lto on 06/02/15.
 *
 * <p>Based on ETSI GS NFV-MAN 001 V1.1.1 (2014-12)
 */
public class InternalVirtualLink extends AbstractVirtualLink {

    /**
     * References to Connection Points (vnfd:vdu:vnfc:connection_point:id,vnfd:connection_point:id),
     * e.g. of type E-Line, E-Tree, or E-LAN.
     */
    private Set<String> connection_points_references;

    public InternalVirtualLink() {
    }

    public Set<String> getConnection_points_references() {
        return connection_points_references;
    }

    public void setConnection_points_references(Set<String> connection_points_references) {
        this.connection_points_references = connection_points_references;
    }

    @Override
    public String toString() {
        return "InternalVirtualLink{"
                + "connection_points_references="
                + connection_points_references
                + "} "
                + super.toString();
    }
}
