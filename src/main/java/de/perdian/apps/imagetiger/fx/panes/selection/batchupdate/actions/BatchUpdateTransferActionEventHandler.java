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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateItem;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class BatchUpdateTransferActionEventHandler implements EventHandler<ActionEvent> {

    private List<BatchUpdateItem> items = null;
    private JobExecutor jobExecutor = null;
    private ObjectProperty<EventHandler<ActionEvent>> onTransferProperty = null;

    public BatchUpdateTransferActionEventHandler(List<BatchUpdateItem> items, JobExecutor jobExecutor, ObjectProperty<EventHandler<ActionEvent>> onTransferProperty) {
        this.setItems(items);
        this.setJobExecutor(jobExecutor);
        this.setOnTransferProperty(onTransferProperty);
    }

    @Override
    public void handle(ActionEvent event) {
        this.getJobExecutor().executeJob(jobContext -> {
            List<BatchUpdateItem> dirtyItems = this.getItems().stream().filter(item -> item.getDirty().getValue()).collect(Collectors.toList());
            if (!dirtyItems.isEmpty()) {
                jobContext.updateProgress("Saving " + dirtyItems.size() + " dirty items", 0, dirtyItems.size());
                for (int i=0; i < dirtyItems.size() && !jobContext.isCancelled(); i++) {
                    BatchUpdateItem dirtyItem = dirtyItems.get(i);
                    jobContext.updateProgress("Saving dirty item: " + dirtyItem.getFileName().getOriginalValue().getValue(), i, dirtyItems.size());
                    this.saveItem(dirtyItem);
                }
            }
            this.getOnTransferProperty().getValue().handle(event);
        });
    }

    private void saveItem(BatchUpdateItem item) {

        ImageFile imageFile = item.getImageFile();

        String newFileDateValue = item.getFileDateLocalString().getNewValue().getValue();
        if (StringUtils.isNotEmpty(newFileDateValue)) {
            imageFile.getFileDateLocalString().getNewValue().setValue(newFileDateValue);
        }
        String newFileDateZone = item.getFileDateLocalZone().getNewValue().getValue();
        if (StringUtils.isNotEmpty(newFileDateZone)) {
            imageFile.getFileDateLocalZone().getNewValue().setValue(newFileDateZone);
        }

        String newFileNameWithoutExtension = item.getFileNameWithoutExtension().getNewValue().getValue();
        if (StringUtils.isNotEmpty(newFileNameWithoutExtension)) {
            imageFile.getFileNameWithoutExtension().getNewValue().setValue(newFileNameWithoutExtension);
        }
        String newFileExtension = item.getFileExtension().getNewValue().getValue();
        if (StringUtils.isNotEmpty(newFileExtension)) {
            imageFile.getFileExtension().getNewValue().setValue(newFileExtension);
        }

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

    private ObjectProperty<EventHandler<ActionEvent>> getOnTransferProperty() {
        return this.onTransferProperty;
    }
    private void setOnTransferProperty(ObjectProperty<EventHandler<ActionEvent>> onTransferProperty) {
        this.onTransferProperty = onTransferProperty;
    }

}
