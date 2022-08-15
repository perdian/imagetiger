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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

public class JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(JobExecutor.class);
    private List<JobListener> listeners = new CopyOnWriteArrayList<>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private AtomicLong jobCounter = new AtomicLong();
    private JobContextImpl currentJobContext = null;
    private BooleanProperty busy = null;

    public JobExecutor() {
        BooleanProperty busy = new SimpleBooleanProperty();
        this.addListener(new UpdateBusyWhileJobRunningJobListener(busy));
        this.setBusy(busy);
    }

    /**
     * Executes the given job with a separate thread. All registered listeners
     * will be notified upon the start of the job and changes within the jobs
     * state
     *
     * @param job
     *     the job to be executed
     */
    public synchronized Future<?> executeJob(Job job) {

        // If we already have a job running, we need to make sure that it gets
        // cancelled first
        this.cancelCurrentJob();

        JobContextImpl jobContext = new JobContextImpl(job, this.getJobCounter(), this.getListeners());
        this.setCurrentJobContext(jobContext);

        log.trace("Executing job: {}", job);
        return this.getExecutor().submit(() -> {

            this.getListeners().forEach(listener -> listener.jobStarted(job));
            try {
                job.execute(jobContext);
            } finally {

                this.getListeners().forEach(listener -> listener.jobCompleted(job, !jobContext.isActive()));

                synchronized (this) {
                    if (this.getCurrentJobContext() == jobContext) {
                        this.setCurrentJobContext(null);
                    }
                }

            }

        });

    }

    public synchronized void cancelCurrentJob() {
        Optional.ofNullable(this.getCurrentJobContext()).ifPresent(jobContext -> jobContext.setCancelled(true));
    }

    private static class UpdateBusyWhileJobRunningJobListener implements JobListener {

        private Property<Boolean> busy = null;

        private UpdateBusyWhileJobRunningJobListener(Property<Boolean> busy) {
            this.setBusy(busy);
        }

        @Override
        public void jobStarted(Job job) {
            Platform.runLater(() -> this.getBusy().setValue(Boolean.TRUE));
        }

        @Override
        public void jobCompleted(Job job, boolean otherJobsActive) {
            if (!otherJobsActive) {
                Platform.runLater(() -> this.getBusy().setValue(Boolean.FALSE));
            }
        }

        private Property<Boolean> getBusy() {
            return this.busy;
        }
        private void setBusy(Property<Boolean> busy) {
            this.busy = busy;
        }

    }

    public BooleanProperty getBusy() {
        return this.busy;
    }
    private void setBusy(BooleanProperty busy) {
        this.busy = busy;
    }

    public void addListener(JobListener listener) {
        this.getListeners().add(listener);
    }
    List<JobListener> getListeners() {
        return this.listeners;
    }
    void setListeners(List<JobListener> listeners) {
        this.listeners = listeners;
    }

    ExecutorService getExecutor() {
        return this.executor;
    }
    void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    AtomicLong getJobCounter() {
        return this.jobCounter;
    }
    void setJobCounter(AtomicLong jobCounter) {
        this.jobCounter = jobCounter;
    }

    JobContextImpl getCurrentJobContext() {
        return this.currentJobContext;
    }
    void setCurrentJobContext(JobContextImpl currentJobContext) {
        this.currentJobContext = currentJobContext;
    }

}
