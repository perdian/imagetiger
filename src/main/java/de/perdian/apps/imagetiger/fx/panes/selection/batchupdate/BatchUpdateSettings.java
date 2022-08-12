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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;

class BatchUpdateSettings {

    private StringProperty originalFileNamePattern = null;
    private StringProperty newFileName = null;
    private StringProperty newFileExtension = null;
    private StringProperty newFileDateLocalString = null;
    private StringProperty newFileDateLocalZone = null;
    private ObservableBooleanValue ready = null;

    BatchUpdateSettings() {

        StringProperty originalFileNamePattern = new SimpleStringProperty();
        StringProperty newFileName = new SimpleStringProperty();
        StringProperty newFileExtension = new SimpleStringProperty();
        StringProperty newFileDateLocalString = new SimpleStringProperty();
        StringProperty newFileDateLocalZone = new SimpleStringProperty();

        this.setOriginalFileNamePattern(originalFileNamePattern);
        this.setNewFileName(newFileName);
        this.setNewFileExtension(newFileExtension);
        this.setNewFileDateLocalString(newFileDateLocalString);
        this.setNewFileDateLocalZone(newFileDateLocalZone);

        ObservableBooleanValue newFileNameReady = newFileName.isNotEmpty();
        ObservableBooleanValue newFileDateReady = newFileDateLocalString.isNotEmpty();
        ObservableBooleanValue ready = Bindings.or(newFileNameReady, newFileDateReady);
        this.setReady(ready);

    }

    StringProperty getOriginalFileNamePattern() {
        return this.originalFileNamePattern;
    }
    private void setOriginalFileNamePattern(StringProperty originalFileNamePattern) {
        this.originalFileNamePattern = originalFileNamePattern;
    }

    StringProperty getNewFileName() {
        return this.newFileName;
    }
    private void setNewFileName(StringProperty newFileName) {
        this.newFileName = newFileName;
    }

    StringProperty getNewFileExtension() {
        return this.newFileExtension;
    }
    private void setNewFileExtension(StringProperty newFileExtension) {
        this.newFileExtension = newFileExtension;
    }

    StringProperty getNewFileDateLocalString() {
        return this.newFileDateLocalString;
    }
    private void setNewFileDateLocalString(StringProperty newFileDateLocalString) {
        this.newFileDateLocalString = newFileDateLocalString;
    }

    StringProperty getNewFileDateLocalZone() {
        return this.newFileDateLocalZone;
    }
    private void setNewFileDateLocalZone(StringProperty newFileDateLocalZone) {
        this.newFileDateLocalZone = newFileDateLocalZone;
    }

    ObservableBooleanValue getReady() {
        return this.ready;
    }
    private void setReady(ObservableBooleanValue ready) {
        this.ready = ready;
    }

}
