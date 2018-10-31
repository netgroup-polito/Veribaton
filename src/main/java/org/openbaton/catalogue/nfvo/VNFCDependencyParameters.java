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

package org.openbaton.catalogue.nfvo;

import org.openbaton.catalogue.util.BaseEntity;

import java.util.Map;

public class VNFCDependencyParameters extends BaseEntity {
    private String vnfcId;

    private Map<String, DependencyParameters> parameters;

    public String getVnfcId() {
        return vnfcId;
    }

    public void setVnfcId(String vnfcId) {
        this.vnfcId = vnfcId;
    }

    public Map<String, DependencyParameters> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, DependencyParameters> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "VNFCDependencyParameters{"
                + "vnfcId='"
                + vnfcId
                + '\''
                + ", parameters="
                + parameters
                + "} "
                + super.toString();
    }
}
