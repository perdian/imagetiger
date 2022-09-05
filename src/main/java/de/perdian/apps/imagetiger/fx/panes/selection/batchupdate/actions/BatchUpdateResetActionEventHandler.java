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
package de.perdian.apps.imagetiger.fx.panes.selection.batchupdate.actions;

import java.util.List;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateItem;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class BatchUpdateResetActionEventHandler implements EventHandler<ActionEvent> {

    private List<BatchUpdateItem> items = null;
    private JobExecutor jobExecutor = null;

    public BatchUpdateResetActionEventHandler(List<BatchUpdateItem> items, JobExecutor jobExecutor) {
        this.setItems(items);
        this.setJobExecutor(jobExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
        this.getJobExecutor().executeJob(jobContext -> {
            this.getItems().forEach(item -> item.resetAllValues());
        });
    }

    private List<BatchUpdateItem> getItems() {
        return this.items;
    }
    private void setItems(List<BatchUpdateItem> items) {
        this.items = items;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

}
