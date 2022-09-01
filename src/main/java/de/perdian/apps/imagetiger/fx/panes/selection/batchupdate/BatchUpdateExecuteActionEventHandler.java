/*
 * Copyright 2022-2022 Christian Seifert
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
package de.perdian.apps.imagetiger.fx.panes.selection.batchupdate;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateItem;
import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateJob;
import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateSettings;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

class BatchUpdateExecuteActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(BatchUpdateExecuteActionEventHandler.class);

    private List<BatchUpdateItem> items = null;
    private BatchUpdateSettings settings = null;
    private JobExecutor jobExecutor = null;

    BatchUpdateExecuteActionEventHandler(List<BatchUpdateItem> items, BatchUpdateSettings settings, JobExecutor jobExecutor) {
        this.setItems(items);
        this.setSettings(settings);
        this.setJobExecutor(jobExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
        log.debug("Preparing property evaluation from settings: {}", this.getSettings());
        this.getJobExecutor().executeJob(new BatchUpdateJob(this.getItems(), this.getSettings()));
    }

    private List<BatchUpdateItem> getItems() {
        return this.items;
    }
    private void setItems(List<BatchUpdateItem> items) {
        this.items = items;
    }

    private BatchUpdateSettings getSettings() {
        return this.settings;
    }
    private void setSettings(BatchUpdateSettings settings) {
        this.settings = settings;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

}
