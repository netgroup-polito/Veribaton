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

package org.openbaton.exceptions;

/**
 * Created by lto on 15/01/16.
 */
public class CyclicDependenciesException extends Exception {
    public CyclicDependenciesException(Throwable cause) {
        super(cause);
    }

    public CyclicDependenciesException(String message) {
        super(message);
    }

    public CyclicDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }
}