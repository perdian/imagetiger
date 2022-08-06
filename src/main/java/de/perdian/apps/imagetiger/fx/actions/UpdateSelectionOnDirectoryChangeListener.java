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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.fx.support.jobs.JobContext;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import de.perdian.apps.imagetiger.model.ImageFile;
import de.perdian.apps.imagetiger.model.ImageFileParser;
import de.perdian.apps.imagetiger.model.impl.DefaultImageFileParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class UpdateSelectionOnDirectoryChangeListener implements ChangeListener<File> {

    private static final Logger log = LoggerFactory.getLogger(UpdateSelectionOnDirectoryChangeListener.class);

    private Selection selection = null;
    private JobExecutor jobExecutor = null;
    private ImageFileParser imageFileParser = new DefaultImageFileParser();

    public UpdateSelectionOnDirectoryChangeListener(Selection selection, JobExecutor jobExecutor) {
        this.setSelection(selection);
        this.setJobExecutor(jobExecutor);
    }

    @Override
    public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
        this.getJobExecutor().executeJob(context -> {
            List<ImageFile> imageFiles = this.parseImageFiles(newValue, context);
            synchronized (this.getSelection()) {
                if (!context.isCancelled()) {
                    this.getSelection().getSelectedDirectory().setValue(newValue);
                    this.getSelection().getSelectedImageFiles().clear();
                    this.getSelection().getAvailableImageFiles().setAll(imageFiles);
                }
            }
        });
    }

    private List<ImageFile> parseImageFiles(File directory, JobContext jobContext) {

        List<File> potentialImageFiles = Arrays.stream(directory.listFiles())
            .filter(file -> file.isFile())
            .filter(file -> !file.isHidden())
            .filter(file -> this.getImageFileParser().isPotentialImageFile(file))
            .sorted((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()))
            .toList();

        if (potentialImageFiles.isEmpty()) {
            return Collections.emptyList();
        } else {
            jobContext.updateProgress("Processed " + potentialImageFiles.size() + " image files", 0, potentialImageFiles.size());
            List<ImageFile> imageFiles = new ArrayList<>(potentialImageFiles.size());
            for (int i=0; i < potentialImageFiles.size() && !jobContext.isCancelled(); i++) {
                File potentialImageFile = potentialImageFiles.get(i);
                jobContext.updateProgress("Processing image file: " + potentialImageFile.getName(), i, potentialImageFiles.size());
                try {
                    ImageFile imageFile = this.getImageFileParser().parseFile(potentialImageFile);
                    if (imageFile != null) {
                        imageFiles.add(imageFile);
                    }
                } catch (IOException e) {
                    log.warn("Cannot process image file at: " + potentialImageFile.getAbsolutePath(), e);
                }
            }
            jobContext.updateProgress("Analyzed " + imageFiles.size() + " image files", potentialImageFiles.size(), potentialImageFiles.size());
            return imageFiles;
        }

    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    public ImageFileParser getImageFileParser() {
        return this.imageFileParser;
    }
    public void setImageFileParser(ImageFileParser imageFileParser) {
        this.imageFileParser = imageFileParser;
    }

}
