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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ImageDataProperty<T> {

    private ObjectProperty<T> savedValue = null;
    private ObjectProperty<T> newValue = null;
    private BooleanBinding dirty = null;

    public ImageDataProperty(T initialValue) {
        ObjectProperty<T> savedValue = new SimpleObjectProperty<>(initialValue);
        ObjectProperty<T> newValue = new SimpleObjectProperty<>(initialValue);
        this.setDirty(Bindings.equal(savedValue, newValue).not());
        this.setSavedValue(savedValue);
        this.setNewValue(newValue);
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("saved", this.getSavedValue().getValue());
        toStringBuilder.append("new", this.getNewValue().getValue());
        return toStringBuilder.toString();
    }

    public ObjectProperty<T> getSavedValue() {
        return this.savedValue;
    }
    private void setSavedValue(ObjectProperty<T> savedValue) {
        this.savedValue = savedValue;
    }

    public ObjectProperty<T> getNewValue() {
        return this.newValue;
    }
    private void setNewValue(ObjectProperty<T> newValue) {
        this.newValue = newValue;
    }

    public BooleanBinding getDirty() {
        return this.dirty;
    }
    private void setDirty(BooleanBinding dirty) {
        this.dirty = dirty;
    }

}
