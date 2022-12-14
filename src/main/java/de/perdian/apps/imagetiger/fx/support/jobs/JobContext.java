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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface JobContext {

    public static final JobContext NULL_CONTEXT = new JobContext() {

        private static final Logger log = LoggerFactory.getLogger(JobContext.class);

        @Override
        public void updateProgress(String message, Integer step, Integer totalSteps) {
            this.updateProgress(message);
        }

        @Override
        public void updateProgress(String message) {
            if (StringUtils.isNotEmpty(message)) {
                log.info(message);
            }
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public Throwable getError() {
            return null;
        }

    };

    /**
     * Sends a progress information to the system telling everyone about the
     * current state of a job
     *
     * @param message
     *     the message to be shown to the user
     */
    public void updateProgress(String message);

    /**
     * Sends a progress information to the system telling everyone about the
     * current state of a job
     *
     * @param message
     *     the message to be shown to the user
     * @param step
     *     the current step
     * @param totalSteps
     *     the total number of steps that this job is going to process
     */
    public void updateProgress(String message, Integer step, Integer totalSteps);

    /**
     * Checks if the current job is active, meaning whether it has overall
     * control over the GUI and is the main job whose progress is being
     * displayed
     */
    public boolean isActive();

    /**
     * Checks if the job has been cancelled
     */
    public boolean isCancelled();

    /**
     * Gets an error that occured during the job execution
     */
    public Throwable getError();

}
