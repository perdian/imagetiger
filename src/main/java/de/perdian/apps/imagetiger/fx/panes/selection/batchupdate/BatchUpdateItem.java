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

import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

class BatchUpdateItem {

    private ImageFile imageFile = null;
    private ReadOnlyStringProperty originalFileNameProperty = null;
    private StringProperty newFileNameProperty = null;
    private ReadOnlyStringProperty originalFileExtensionProperty = null;
    private StringProperty newFileExtensionProperty = null;
    private ReadOnlyStringProperty originalFileDateLocalStringProperty = null;
    private StringProperty newFileDateLocalStringProperty = null;
    private ReadOnlyStringProperty originalFileDateLocalZoneProperty = null;
    private StringProperty newFileDateLocalZoneProperty = null;

    BatchUpdateItem(ImageFile imageFile) {
        this.setImageFile(imageFile);
    }

    ImageFile getImageFile() {
        return this.imageFile;
    }
    private void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    ReadOnlyStringProperty getOriginalFileNameProperty() {
        return this.originalFileNameProperty;
    }
    private void setOriginalFileNameProperty(ReadOnlyStringProperty originalFileNameProperty) {
        this.originalFileNameProperty = originalFileNameProperty;
    }

    StringProperty getNewFileNameProperty() {
        return this.newFileNameProperty;
    }
    private void setNewFileNameProperty(StringProperty newFileNameProperty) {
        this.newFileNameProperty = newFileNameProperty;
    }

    ReadOnlyStringProperty getOriginalFileExtensionProperty() {
        return this.originalFileExtensionProperty;
    }
    private void setOriginalFileExtensionProperty(ReadOnlyStringProperty originalFileExtensionProperty) {
        this.originalFileExtensionProperty = originalFileExtensionProperty;
    }

    StringProperty getNewFileExtensionProperty() {
        return this.newFileExtensionProperty;
    }
    private void setNewFileExtensionProperty(StringProperty newFileExtensionProperty) {
        this.newFileExtensionProperty = newFileExtensionProperty;
    }

    ReadOnlyStringProperty getOriginalFileDateLocalStringProperty() {
        return this.originalFileDateLocalStringProperty;
    }
    private void setOriginalFileDateLocalStringProperty(ReadOnlyStringProperty originalFileDateLocalStringProperty) {
        this.originalFileDateLocalStringProperty = originalFileDateLocalStringProperty;
    }

    StringProperty getNewFileDateLocalStringProperty() {
        return this.newFileDateLocalStringProperty;
    }
    private void setNewFileDateLocalStringProperty(StringProperty newFileDateLocalStringProperty) {
        this.newFileDateLocalStringProperty = newFileDateLocalStringProperty;
    }

    ReadOnlyStringProperty getOriginalFileDateLocalZoneProperty() {
        return this.originalFileDateLocalZoneProperty;
    }
    private void setOriginalFileDateLocalZoneProperty(ReadOnlyStringProperty originalFileDateLocalZoneProperty) {
        this.originalFileDateLocalZoneProperty = originalFileDateLocalZoneProperty;
    }

    StringProperty getNewFileDateLocalZoneProperty() {
        return this.newFileDateLocalZoneProperty;
    }
    private void setNewFileDateLocalZoneProperty(StringProperty newFileDateLocalZoneProperty) {
        this.newFileDateLocalZoneProperty = newFileDateLocalZoneProperty;
    }

}
