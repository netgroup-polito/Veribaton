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

public class ConnectionPoint extends BaseEntity {

    /**
     * This may be for example a virtual port, a virtual NIC address, a physical port, a physical NIC
     * address or the endpoint of an IP VPN enabling network connectivity. TODO think about what type
     * must be
     */
    protected String type;

    public ConnectionPoint() {
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ConnectionPoint{" + "type='" + type + '\'' + "} " + super.toString();
    }
}
