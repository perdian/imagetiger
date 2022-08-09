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

import de.perdian.apps.imagetiger.model.ImageFile;
import de.perdian.apps.imagetiger.model.support.ChangeTrackingProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

class BatchUpdateItem {

    private ImageFile imageFile = null;
    private ChangeTrackingProperty<String> fileName = null;
    private ChangeTrackingProperty<String> fileNameWithoutExtension = null;
    private ChangeTrackingProperty<String> fileExtension = null;
    private ChangeTrackingProperty<String> fileDateLocalString = null;
    private ChangeTrackingProperty<String> fileDateLocalZone = null;
    private ReadOnlyBooleanProperty dirty = null;

    BatchUpdateItem(ImageFile imageFile) {
        this.setImageFile(imageFile);

        ChangeTrackingProperty<String> fileName = new ChangeTrackingProperty<>(imageFile.getFileName().getOriginalValue().getValue());
        ChangeTrackingProperty<String> fileNameWithoutExtension = new ChangeTrackingProperty<>(imageFile.getFileNameWithoutExtension().getOriginalValue().getValue());
        ChangeTrackingProperty<String> fileExtension = new ChangeTrackingProperty<>(imageFile.getFileExtension().getOriginalValue().getValue());
        ChangeTrackingProperty<String> fileDateLocalString = new ChangeTrackingProperty<>(imageFile.getFileDateLocalString().getOriginalValue().getValue());
        ChangeTrackingProperty<String> fileDateLocalZone = new ChangeTrackingProperty<>(imageFile.getFileDateLocalZone().getOriginalValue().getValue());

        BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
        List<ChangeTrackingProperty<?>> dirtyCheckingProperties = List.of(fileName, fileNameWithoutExtension, fileExtension, fileDateLocalString, fileDateLocalZone);
        dirtyCheckingProperties.forEach(property -> property.getDirty().addListener((o, oldValue, newValue) -> {
            dirtyProperty.setValue(dirtyCheckingProperties.stream().map(p -> p.getDirty().getValue()).findAny().orElse(false));
        }));

        this.setFileName(fileName);
        this.setFileNameWithoutExtension(fileNameWithoutExtension);
        this.setFileExtension(fileExtension);
        this.setFileDateLocalString(fileDateLocalString);
        this.setFileDateLocalZone(fileDateLocalZone);
        this.setDirty(dirtyProperty);

    }

    ImageFile getImageFile() {
        return this.imageFile;
    }
    private void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    ChangeTrackingProperty<String> getFileName() {
        return this.fileName;
    }
    private void setFileName(ChangeTrackingProperty<String> fileName) {
        this.fileName = fileName;
    }

    ChangeTrackingProperty<String> getFileNameWithoutExtension() {
        return this.fileNameWithoutExtension;
    }
    private void setFileNameWithoutExtension(ChangeTrackingProperty<String> fileNameWithoutExtension) {
        this.fileNameWithoutExtension = fileNameWithoutExtension;
    }

    ChangeTrackingProperty<String> getFileExtension() {
        return this.fileExtension;
    }
    private void setFileExtension(ChangeTrackingProperty<String> fileExtension) {
        this.fileExtension = fileExtension;
    }

    ChangeTrackingProperty<String> getFileDateLocalString() {
        return this.fileDateLocalString;
    }
    private void setFileDateLocalString(ChangeTrackingProperty<String> fileDateLocalString) {
        this.fileDateLocalString = fileDateLocalString;
    }

    ChangeTrackingProperty<String> getFileDateLocalZone() {
        return this.fileDateLocalZone;
    }
    private void setFileDateLocalZone(ChangeTrackingProperty<String> fileDateLocalZone) {
        this.fileDateLocalZone = fileDateLocalZone;
    }

    ReadOnlyBooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(ReadOnlyBooleanProperty dirty) {
        this.dirty = dirty;
    }

}
