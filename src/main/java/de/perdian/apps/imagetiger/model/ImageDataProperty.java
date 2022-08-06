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
package de.perdian.apps.imagetiger.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;

public class ImageDataProperty<T> {

    private ObjectProperty<T> savedValueInternal = null;
    private ObjectProperty<T> newValueInternal = null;
    private ObservableBooleanValue dirty = null;
    private boolean readOnly = false;

    public ImageDataProperty(T initialValue, boolean readOnly) {
        ObjectProperty<T> savedValue = new SimpleObjectProperty<>(initialValue);
        ObjectProperty<T> newValue = new SimpleObjectProperty<>(initialValue);
        this.setDirty(Bindings.equal(savedValue, newValue).not());
        this.setSavedValueInternal(savedValue);
        this.setNewValueInternal(newValue);
        this.setReadOnly(readOnly);
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("saved", this.getSavedValue().getValue());
        toStringBuilder.append("new", this.getNewValue().getValue());
        return toStringBuilder.toString();
    }

    public void resetValue(T value) {
        Platform.runLater(() -> {
            this.getSavedValueInternal().setValue(value);
            this.getNewValueInternal().setValue(value);
        });
    }

    public ReadOnlyObjectProperty<T> getSavedValue() {
        return this.getSavedValueInternal();
    }
    private ObjectProperty<T> getSavedValueInternal() {
        return this.savedValueInternal;
    }
    private void setSavedValueInternal(ObjectProperty<T> savedValueInternal) {
        this.savedValueInternal = savedValueInternal;
    }

    public ObjectProperty<T> getNewValue() {
        return this.getNewValueInternal();
    }
    private ObjectProperty<T> getNewValueInternal() {
        return this.newValueInternal;
    }
    private void setNewValueInternal(ObjectProperty<T> newValueInternal) {
        this.newValueInternal = newValueInternal;
    }

    public ObservableBooleanValue getDirty() {
        return this.dirty;
    }
    private void setDirty(ObservableBooleanValue dirty) {
        this.dirty = dirty;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }
    private void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}
