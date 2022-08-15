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

import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

class BatchUpdateExecuteActionEventHandler implements EventHandler<ActionEvent> {

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
        this.getJobExecutor().executeJob(jobContext -> {
            List<BatchUpdateItem> items = this.getItems();
            for (int i=0; i < items.size() && !jobContext.isCancelled(); i++) {
                BatchUpdateItem item = items.get(i);
                jobContext.updateProgress("Updating file: " + item.getFileName().getOriginalValue().getValue(), i, items.size());
                this.updateItem(item);
            }
        });
    }

    private void updateItem(BatchUpdateItem item) {
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            // Ignore here
        }
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
