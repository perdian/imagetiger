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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateItem;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class BatchUpdateSaveActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(BatchUpdateSaveActionEventHandler.class);

    private List<BatchUpdateItem> items = null;
    private JobExecutor jobExecutor = null;

    public BatchUpdateSaveActionEventHandler(List<BatchUpdateItem> items, JobExecutor jobExecutor) {
        this.setItems(items);
        this.setJobExecutor(jobExecutor);
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
        });

    }

    private void saveItem(BatchUpdateItem item) {
        try {

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

            imageFile.updateOsFile();

            item.resetAllValues();

        } catch (Exception e) {
            log.error("Cannot update image file at: {}", item.getImageFile().getFileName().getOriginalValue().getValue(), e);
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

}
