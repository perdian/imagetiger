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
package de.perdian.apps.imagetiger.fx.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class SaveChangedFilesActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(SaveChangedFilesActionEventHandler.class);

    private List<ImageFile> dirtyImageFiles = null;
    private JobExecutor jobExecutor = null;

    public SaveChangedFilesActionEventHandler(List<ImageFile> dirtyImageFiles, JobExecutor jobExecutor) {
        this.setDirtyImageFiles(dirtyImageFiles);
        this.setJobExecutor(jobExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
        List<ImageFile> dirtyImageFiles = this.getDirtyImageFiles();
        if (!dirtyImageFiles.isEmpty()) {
            log.info("Saving {} changes files", dirtyImageFiles.size());
            this.getJobExecutor().executeJob(jobContext -> {
                jobContext.updateProgress("Saving " + dirtyImageFiles.size() + " changed files", 0, dirtyImageFiles.size());
                for (int i=0; i < dirtyImageFiles.size() && !jobContext.isCancelled(); i++) {
                    ImageFile dirtyFile = dirtyImageFiles.get(i);
                    jobContext.updateProgress("Saving dirty file: " + dirtyFile.getFileName().getSavedValue().getValue(), i + 1, dirtyImageFiles.size());
                    try {
                        dirtyFile.updateOsFile();
                    } catch (Exception e) {
                        log.error("Cannot save dirty file: " + dirtyFile.getFileName().getSavedValue().getValue(), e);
                    }
                }
            });
        }

    }

    private List<ImageFile> getDirtyImageFiles() {
        return this.dirtyImageFiles;
    }
    private void setDirtyImageFiles(List<ImageFile> dirtyImageFiles) {
        this.dirtyImageFiles = dirtyImageFiles;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

}
