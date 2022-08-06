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
package de.perdian.apps.imagetiger.fx.panes.selection.tags;

import java.util.Objects;
import java.util.function.Function;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageDataProperty;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class TagsPaneControlFactory {

    private Selection selection = null;

    TagsPaneControlFactory(Selection selection) {
        this.setSelection(selection);
    }

    Label createLabel(String text) {
        Label label = new Label(text);
        return label;
    }

    TextField createTextField(Function<ImageFile, ImageDataProperty<String>> propertyFunction) {
        TextField textField = this.createTextFieldBasics(propertyFunction);
        textField.disableProperty().bind(this.getSelection().getPrimaryImageFile().isNull());
        return textField;
    }

    private TextField createTextFieldBasics(Function<ImageFile, ImageDataProperty<String>> propertyFunction) {
        StringProperty controlProperty = this.bindImageDataProperty(propertyFunction, new SimpleStringProperty());

        TextField textField = new TextField();
        textField.setOnKeyPressed(event -> this.handleOnKeyPressed(event));
        textField.textProperty().bindBidirectional(controlProperty);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        return textField;

    }

    private <T extends Property<U>, U> T bindImageDataProperty(Function<ImageFile, ImageDataProperty<U>> imageDataPropertyFunction, T fxProperty) {

        ChangeListener<U> updateFxPropertyChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(fxProperty.getValue(), newValue)) {
                fxProperty.setValue(newValue);
            }
        };
        this.getSelection().getPrimaryImageFile().addListener((o, oldPrimaryImageFile, newPrimaryImageFile) -> {
            if (newPrimaryImageFile != null) {
                Property<U> newPrimaryImageFileProperty = imageDataPropertyFunction.apply(newPrimaryImageFile).getNewValue();
                fxProperty.setValue(newPrimaryImageFileProperty.getValue());
                newPrimaryImageFileProperty.addListener(updateFxPropertyChangeListener);
            } else {
                fxProperty.setValue(null);
            }
            if (oldPrimaryImageFile != null) {
                Property<U> oldPrimaryImageFileProperty = imageDataPropertyFunction.apply(oldPrimaryImageFile).getNewValue();
                oldPrimaryImageFileProperty.removeListener(updateFxPropertyChangeListener);
            }
        });
        fxProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && this.getSelection().getPrimaryImageFile().getValue() != null) {
                Property<U> primaryImageFileProperty = imageDataPropertyFunction.apply(this.getSelection().getPrimaryImageFile().getValue()).getNewValue();
                if (!Objects.equals(newValue, primaryImageFileProperty.getValue())) {
                    primaryImageFileProperty.setValue(newValue);
                }
            }
        });

        return fxProperty;

    }

    private void handleOnKeyPressed(KeyEvent event) {
        if (KeyCode.PAGE_UP.equals(event.getCode())) {
            int currentImageFileIndex = this.getSelection().getAvailableImageFiles().indexOf(this.getSelection().getPrimaryImageFile().getValue());
            int newImageFileIndex = (currentImageFileIndex - 1) % this.getSelection().getAvailableImageFiles().size();
            if (newImageFileIndex < 0) {
                newImageFileIndex = this.getSelection().getAvailableImageFiles().size() + newImageFileIndex;
            }
            ImageFile newPrimaryImageFile = this.getSelection().getAvailableImageFiles().get(newImageFileIndex);
            this.getSelection().getPrimaryImageFile().setValue(newPrimaryImageFile);
        } else if (KeyCode.PAGE_DOWN.equals(event.getCode())) {
            int currentImageFileIndex = this.getSelection().getAvailableImageFiles().indexOf(this.getSelection().getPrimaryImageFile().getValue());
            int newImageFileIndex = (currentImageFileIndex + 1) % this.getSelection().getAvailableImageFiles().size();
            ImageFile newPrimaryImageFile = this.getSelection().getAvailableImageFiles().get(newImageFileIndex);
            this.getSelection().getPrimaryImageFile().setValue(newPrimaryImageFile);
        }
    }


    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

}
