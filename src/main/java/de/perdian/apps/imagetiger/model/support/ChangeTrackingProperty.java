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
package de.perdian.apps.imagetiger.model.support;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;

public class ChangeTrackingProperty<T> {

    private ObjectProperty<T> originalValueInternal = null;
    private ObjectProperty<T> newValueInternal = null;
    private ObservableBooleanValue dirty = null;

    public ChangeTrackingProperty(T initialValue) {
        ObjectProperty<T> originalValue = new SimpleObjectProperty<>(initialValue);
        ObjectProperty<T> newValue = new SimpleObjectProperty<>(initialValue);
        this.setDirty(Bindings.equal(originalValue, newValue).not());
        this.setOriginalValueInternal(originalValue);
        this.setNewValueInternal(newValue);
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("original", this.getOriginalValue().getValue());
        toStringBuilder.append("new", this.getNewValue().getValue());
        return toStringBuilder.toString();
    }

    public void resetValue(T value) {
        Platform.runLater(() -> {
            this.getOriginalValueInternal().setValue(value);
            this.getNewValueInternal().setValue(value);
        });
    }

    public ReadOnlyObjectProperty<T> getOriginalValue() {
        return this.getOriginalValueInternal();
    }
    private ObjectProperty<T> getOriginalValueInternal() {
        return this.originalValueInternal;
    }
    private void setOriginalValueInternal(ObjectProperty<T> originalValueInternal) {
        this.originalValueInternal = originalValueInternal;
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

}
