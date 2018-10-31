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

package org.openbaton.catalogue.util;

import java.io.Serializable;
import java.util.Map;

public class BaseEntity implements Serializable {
    private String id;

    private Integer hbVersion = 0;
    private String projectId;
    private Boolean shared = false;
    private Map<String, String> metadata;

    public BaseEntity() {
    }

    public void ensureId() {
        id = IdGenerator.createUUID();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getHbVersion() {
        return hbVersion;
    }

    public void setHbVersion(Integer hbVersion) {
        this.hbVersion = hbVersion;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "BaseEntity{"
                + "id='"
                + id
                + '\''
                + ", version="
                + hbVersion
                + ", projectId='"
                + projectId
                + '\''
                + ", shared="
                + shared
                + ", metadata="
                + metadata
                + '}';
    }

    public Boolean isShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
