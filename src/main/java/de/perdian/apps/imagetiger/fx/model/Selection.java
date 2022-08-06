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

import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Selection {

    private BooleanProperty busy = new SimpleBooleanProperty(false);
    private BooleanProperty dirty = new SimpleBooleanProperty(false);
    private ObjectProperty<File> selectedDirectory = null;
    private ObservableList<ImageFile> availableImageFiles = null;
    private ObservableList<ImageFile> selectedImageFiles = null;
    private ObservableList<ImageFile> dirtyImageFiles = null;
    private ObjectProperty<ImageFile> primaryImageFile = null;

    public Selection() {

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

        this.setBusy(new SimpleBooleanProperty());
        this.setDirty(new SimpleBooleanProperty());
        this.setSelectedDirectory(new SimpleObjectProperty<>());
        this.setAvailableImageFiles(availableImageFiles);
        this.setDirtyImageFiles(dirtyImageFiles);;
        this.setSelectedImageFiles(FXCollections.observableArrayList());
        this.setPrimaryImageFile(new SimpleObjectProperty<>());

    }

    public BooleanProperty getBusy() {
        return this.busy;
    }
    private void setBusy(BooleanProperty busy) {
        this.busy = busy;
    }

    public BooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(BooleanProperty dirty) {
        this.dirty = dirty;
    }

    public ObjectProperty<File> getSelectedDirectory() {
        return this.selectedDirectory;
    }
    private void setSelectedDirectory(ObjectProperty<File> selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
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
    public void setDirtyImageFiles(ObservableList<ImageFile> dirtyImageFiles) {
        this.dirtyImageFiles = dirtyImageFiles;
    }

    public ObjectProperty<ImageFile> getPrimaryImageFile() {
        return this.primaryImageFile;
    }
    public void setPrimaryImageFile(ObjectProperty<ImageFile> primaryImageFile) {
        this.primaryImageFile = primaryImageFile;
    }

}
