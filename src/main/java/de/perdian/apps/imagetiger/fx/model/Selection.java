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
package de.perdian.apps.imagetiger.fx.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.imagetiger.fx.support.jobs.JobContext;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import de.perdian.apps.imagetiger.model.ImageFile;
import de.perdian.apps.imagetiger.model.ImageFileParser;
import de.perdian.apps.imagetiger.model.impl.DefaultImageFileParser;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Selection {

    private static final Logger log = LoggerFactory.getLogger(Selection.class);

    private BooleanProperty busy = null;
    private BooleanProperty dirty = null;
    private ObjectProperty<File> selectedDirectory = null;
    private ObjectProperty<ImageFile> primaryImageFile = null;
    private ObservableList<ImageFile> availableImageFiles = null;
    private ObservableList<ImageFile> selectedImageFiles = null;
    private ObservableList<ImageFile> dirtyImageFiles = null;
    private JobExecutor jobExecutor = null;
    private ImageFileParser imageFileParser = new DefaultImageFileParser();

    public Selection(BooleanProperty busyProperty, JobExecutor jobExecutor) {

        ObservableList<ImageFile> dirtyImageFiles = FXCollections.observableArrayList();
        ObservableList<ImageFile> availableImageFiles = FXCollections.observableArrayList();
        availableImageFiles.addListener((ListChangeListener.Change<? extends ImageFile> change) -> {
            while (change.next()) {
                dirtyImageFiles.removeAll(change.getRemoved());
                for (ImageFile newImageFile : change.getAddedSubList()) {
                    newImageFile.getDirty().addListener((o, oldValue, newValue) -> {
                        if (newValue) {
                            dirtyImageFiles.add(newImageFile);
                        } else {
                            dirtyImageFiles.remove(newImageFile);
                        }
                    });
                }
            }
        });

        this.setBusy(busyProperty);
        this.setDirty(new SimpleBooleanProperty());
        this.setSelectedDirectory(new SimpleObjectProperty<>());
        this.setAvailableImageFiles(availableImageFiles);
        this.setDirtyImageFiles(dirtyImageFiles);;
        this.setSelectedImageFiles(FXCollections.observableArrayList());
        this.setPrimaryImageFile(new SimpleObjectProperty<>());
        this.setJobExecutor(jobExecutor);

    }

    public void updateSelectedDirectory(File newDirectory) {
        this.getJobExecutor().executeJob(context -> {
            List<ImageFile> imageFiles = this.parseImageFiles(newDirectory, context);
            synchronized (this) {
                if (!context.isCancelled()) {
                    this.primaryImageFile.setValue(null);
                    this.selectedDirectory.setValue(newDirectory);
                    this.selectedImageFiles.clear();
                    this.dirtyImageFiles.clear();
                    this.availableImageFiles.setAll(imageFiles);
                }
            }
        });
    }

    public void updatePrimaryImageFile(ImageFile newPrimaryImageFile) {
        if (this.availableImageFiles.contains(newPrimaryImageFile)) {
            this.primaryImageFile.setValue(newPrimaryImageFile);
        } else {
            this.primaryImageFile.setValue(null);
        }
    }

    public void saveDirtyFiles() {
        List<ImageFile> dirtyImageFiles = new ArrayList<>(this.getDirtyImageFiles());
        if (!dirtyImageFiles.isEmpty()) {
            this.getJobExecutor().executeJob(jobContext -> {
                synchronized (this) {
                    log.info("Saving {} dirty files", dirtyImageFiles.size());
                    jobContext.updateProgress("Saving " + dirtyImageFiles.size() + " dity files", 0, dirtyImageFiles.size());
                    for (int i=0; i < dirtyImageFiles.size() && !jobContext.isCancelled(); i++) {
                        ImageFile dirtyFile = dirtyImageFiles.get(i);
                        jobContext.updateProgress("Saving dirty file: " + dirtyFile.getFileName().getOriginalValue().getValue(), i + 1, dirtyImageFiles.size());
                        try {
                            dirtyFile.updateOsFile();
                        } catch (Exception e) {
                            log.error("Cannot save dirty file: " + dirtyFile.getFileName().getOriginalValue().getValue(), e);
                        }
                    }
                }
            });
        }
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

    public ReadOnlyBooleanProperty getBusy() {
        return this.busy;
    }
    private void setBusy(BooleanProperty busy) {
        this.busy = busy;
    }

    public ReadOnlyBooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(BooleanProperty dirty) {
        this.dirty = dirty;
    }

    public ReadOnlyObjectProperty<File> getSelectedDirectory() {
        return this.selectedDirectory;
    }
    private void setSelectedDirectory(ObjectProperty<File> selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    public ReadOnlyObjectProperty<ImageFile> getPrimaryImageFile() {
        return this.primaryImageFile;
    }
    private void setPrimaryImageFile(ObjectProperty<ImageFile> primaryImageFile) {
        this.primaryImageFile = primaryImageFile;
    }

    public ObservableList<ImageFile> getAvailableImageFiles() {
        return this.availableImageFiles;
    }
    private void setAvailableImageFiles(ObservableList<ImageFile> availableImageFiles) {
        this.availableImageFiles = availableImageFiles;
    }

    public ObservableList<ImageFile> getSelectedImageFiles() {
        return this.selectedImageFiles;
    }
    private void setSelectedImageFiles(ObservableList<ImageFile> selectedImageFiles) {
        this.selectedImageFiles = selectedImageFiles;
    }

    public ObservableList<ImageFile> getDirtyImageFiles() {
        return this.dirtyImageFiles;
    }
    private void setDirtyImageFiles(ObservableList<ImageFile> dirtyImageFiles) {
        this.dirtyImageFiles = dirtyImageFiles;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    ImageFileParser getImageFileParser() {
        return this.imageFileParser;
    }
    void setImageFileParser(ImageFileParser imageFileParser) {
        this.imageFileParser = imageFileParser;
    }

}
