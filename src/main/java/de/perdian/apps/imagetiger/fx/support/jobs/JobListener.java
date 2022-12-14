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
 * Listener that will be notified upon changes within the execution of a job
 *
 * @author Christian Seifert
 */

public interface JobListener {

    default void jobStarted(Job job) {
    }

    default void jobProgress(Job job, String progressMessage, Integer progressStep, Integer totalProgressSteps) {
    }

    default void jobCompleted(Job job, boolean otherJobsActive) {
    }

}
