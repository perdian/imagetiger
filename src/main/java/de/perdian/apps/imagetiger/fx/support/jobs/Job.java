/*
 * Copyright 2014-2022 Christian Seifert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.imagetiger.fx.support.jobs;

/**
 * Represents any kind of job that has to be executed throughout the GUI. It will be run within a separate thread and
 * therefore any implementation should make sure that GUI relevant operations are performed in the GUI thread by
 * using the {@code Platform.runLater} method.
 *
 * @author Christian Seifert
 */

public interface Job {

    /**
     * @param context
     *     the context through which the job can communicate with the outer world
     */
    void execute(JobContext context);

}
