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
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.perdian.apps.imagetiger.fx.ImageTigerPreferences;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;

public class BatchUpdateSettings {

    private StringProperty originalFileNamePattern = null;
    private StringProperty newFileName = null;
    private StringProperty newFileExtension = null;
    private StringProperty newFileDateLocalString = null;
    private StringProperty newFileDateLocalZone = null;
    private ObservableBooleanValue ready = null;
    private List<ChangeListener<String>> changeListeners = null;

    public BatchUpdateSettings(ImageTigerPreferences preferences) {
        this();

        this.getOriginalFileNamePattern().bindBidirectional(preferences.createProperty("BatchUpdateSettings.originalFileNamePattern", null));
        this.getNewFileName().bindBidirectional(preferences.createProperty("BatchUpdateSettings.newFileName", null));
        this.getNewFileExtension().bindBidirectional(preferences.createProperty("BatchUpdateSettings.newFileExtension", "#{lowercase(file.extension)}"));
        this.getNewFileDateLocalString().bindBidirectional(preferences.createProperty("BatchUpdateSettings.newFileDateLocalString", null));
        this.getNewFileDateLocalZone().bindBidirectional(preferences.createProperty("BatchUpdateSettings.newFileDateLocalZone", null));
    }

    public BatchUpdateSettings() {

        List<ChangeListener<String>> changeListeners = new CopyOnWriteArrayList<>();
        ChangeListener<String> changeListener = (o, oldValue, newValue) -> {
            for (ChangeListener<String> changeListenerDelegee : changeListeners) {
                changeListenerDelegee.changed(o, oldValue, newValue);
            }
        };
        this.setChangeListeners(changeListeners);

        StringProperty originalFileNamePattern = new SimpleStringProperty();
        originalFileNamePattern.addListener(changeListener);
        this.setOriginalFileNamePattern(originalFileNamePattern);

        StringProperty newFileName = new SimpleStringProperty();
        newFileName.addListener(changeListener);
        this.setNewFileName(newFileName);

        StringProperty newFileExtension = new SimpleStringProperty();
        newFileExtension.addListener(changeListener);
        this.setNewFileExtension(newFileExtension);

        StringProperty newFileDateLocalString = new SimpleStringProperty();
        newFileDateLocalString.addListener(changeListener);
        this.setNewFileDateLocalString(newFileDateLocalString);

        StringProperty newFileDateLocalZone = new SimpleStringProperty();
        newFileDateLocalZone.addListener(changeListener);
        this.setNewFileDateLocalZone(newFileDateLocalZone);

        ObservableBooleanValue newFileNameReady = Bindings.or(newFileName.isNotEmpty(), newFileExtension.isNotEmpty());
        ObservableBooleanValue newFileDateReady = newFileDateLocalString.isNotEmpty();
        ObservableBooleanValue ready = Bindings.or(newFileNameReady, newFileDateReady);
        this.setReady(ready);

    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("originalFileNamePattern", this.getOriginalFileNamePattern().getValue());
        toStringBuilder.append("newFileName", this.getNewFileName().getValue());
        toStringBuilder.append("newFileExtension", this.getNewFileExtension().getValue());
        toStringBuilder.append("newFileDateLocalString", this.getNewFileDateLocalString().getValue());
        toStringBuilder.append("newFileDateLocalZone", this.getNewFileDateLocalZone().getValue());
        return toStringBuilder.toString();
    }

    public StringProperty getOriginalFileNamePattern() {
        return this.originalFileNamePattern;
    }
    private void setOriginalFileNamePattern(StringProperty originalFileNamePattern) {
        this.originalFileNamePattern = originalFileNamePattern;
    }

    public StringProperty getNewFileName() {
        return this.newFileName;
    }
    private void setNewFileName(StringProperty newFileName) {
        this.newFileName = newFileName;
    }

    public StringProperty getNewFileExtension() {
        return this.newFileExtension;
    }
    private void setNewFileExtension(StringProperty newFileExtension) {
        this.newFileExtension = newFileExtension;
    }

    public StringProperty getNewFileDateLocalString() {
        return this.newFileDateLocalString;
    }
    private void setNewFileDateLocalString(StringProperty newFileDateLocalString) {
        this.newFileDateLocalString = newFileDateLocalString;
    }

    public StringProperty getNewFileDateLocalZone() {
        return this.newFileDateLocalZone;
    }
    private void setNewFileDateLocalZone(StringProperty newFileDateLocalZone) {
        this.newFileDateLocalZone = newFileDateLocalZone;
    }

    public ObservableBooleanValue getReady() {
        return this.ready;
    }
    private void setReady(ObservableBooleanValue ready) {
        this.ready = ready;
    }

    public void addChangeListener(ChangeListener<String> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    private List<ChangeListener<String>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<String>> changeListeners) {
        this.changeListeners = changeListeners;
    }

}
