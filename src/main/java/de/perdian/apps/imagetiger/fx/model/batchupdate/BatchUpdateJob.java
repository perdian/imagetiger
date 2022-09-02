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
package de.perdian.apps.imagetiger.fx.model.batchupdate;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.imagetiger.fx.support.jobs.Job;
import de.perdian.apps.imagetiger.fx.support.jobs.JobContext;

public class BatchUpdateJob implements Job {

    private List<BatchUpdateItem> items = null;
    private BatchUpdateSettings settings = null;

    public BatchUpdateJob(List<BatchUpdateItem> items, BatchUpdateSettings settings) {
        this.setItems(items);
        this.setSettings(settings);
    }

    @Override
    public void execute(JobContext jobContext) {
        BatchUpdateContext updateContext = new BatchUpdateContext(this.getItems(), this.getSettings());
        for (int i=0; i < this.getItems().size() && !jobContext.isCancelled(); i++) {

            BatchUpdateItem item = this.getItems().get(i);
            jobContext.updateProgress("Updating file: " + item.getFileName().getOriginalValue().getValue(), i, this.getItems().size());

            BatchUpdateItemContext itemContext = updateContext.createItemContext(item);
            String newFileName = this.getSettings().getNewFileName().getValue();
            String newFileExtension = this.getSettings().getNewFileExtension().getValue();
            if (StringUtils.isNotEmpty(newFileName)) {
                item.getFileNameWithoutExtension().getNewValue().setValue(itemContext.evaluate(newFileName));
            }
            if (StringUtils.isNotEmpty(newFileExtension)) {
                item.getFileExtension().getNewValue().setValue(itemContext.evaluate(newFileExtension));
            }

            String newFileDateLocalString = this.getSettings().getNewFileDateLocalString().getValue();
            String newFileDateLocalZone = this.getSettings().getNewFileDateLocalZone().getValue();
            if (StringUtils.isNotEmpty(newFileDateLocalString)) {
                String newFileDateLocalStringValue = itemContext.evaluate(newFileDateLocalString);
                if (StringUtils.isNotEmpty(newFileDateLocalStringValue)) {
                    item.getFileDateLocalString().getNewValue().setValue(newFileDateLocalStringValue);
                }
            }
            if (StringUtils.isNotEmpty(newFileDateLocalZone)) {
                String newFileDateLocalZoneValue = itemContext.evaluate(newFileDateLocalZone);
                if (StringUtils.isNotEmpty(newFileDateLocalZoneValue)) {
                    item.getFileDateLocalZone().getNewValue().setValue(newFileDateLocalZoneValue);
                }
            }

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

}
