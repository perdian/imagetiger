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
package de.perdian.apps.imagetiger.fx.panes.selection.files;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

class FileThumbnailPane extends GridPane {

    FileThumbnailPane(Selection selection, ImageFile imageFile, IntegerProperty widthAndHeightProperty, Executor thumnailsScalingExecutor) {

        Label imageLabel = new Label();
        imageLabel.setAlignment(Pos.CENTER);
        imageLabel.prefWidthProperty().bind(widthAndHeightProperty);
        imageLabel.prefHeightProperty().bind(widthAndHeightProperty);
        imageLabel.setBorder(Border.stroke(Color.rgb(224, 224, 224)));
        imageLabel.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                selection.getPrimaryImageFile().setValue(imageFile);
            }
        });
        this.updateImage(imageFile, imageLabel, widthAndHeightProperty, widthAndHeightProperty.intValue(), thumnailsScalingExecutor);

        ToggleButton selectedButton = new ToggleButton("Selected");
        selectedButton.setGraphic(new FontIcon(MaterialDesignC.CHECKBOX_BLANK_OUTLINE));
        selectedButton.selectedProperty().addListener((o, oldValue, newValue) -> Platform.runLater(() -> selectedButton.setGraphic(new FontIcon(newValue ? MaterialDesignC.CHECKBOX_MARKED : MaterialDesignC.CHECKBOX_BLANK_OUTLINE))));
        selectedButton.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                selection.getPrimaryImageFile().setValue(imageFile);
            }
        });
        GridPane.setHalignment(selectedButton, HPos.CENTER);
        GridPane.setHgrow(selectedButton, Priority.ALWAYS);

        selectedButton.setSelected(selection.getSelectedImageFiles().contains(imageFile));
        selectedButton.selectedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue && !selection.getSelectedImageFiles().contains(imageFile)) {
                selection.getSelectedImageFiles().add(imageFile);
            } else if (!newValue && selection.getSelectedImageFiles().contains(imageFile)) {
                selection.getSelectedImageFiles().remove(imageFile);
            }
        });
        selection.getSelectedImageFiles().addListener((ListChangeListener.Change<? extends ImageFile> change) -> selectedButton.setSelected(change.getList().contains(imageFile)));
        GridPane.setMargin(selectedButton, new Insets(5, 5, 5, 5));

        Label fileNameLabel = new Label();
        fileNameLabel.textProperty().bind(imageFile.getFileName().getSavedValue());
        fileNameLabel.maxWidthProperty().bind(Bindings.subtract(widthAndHeightProperty, 10));
        GridPane.setMargin(fileNameLabel, new Insets(5, 5, 5, 5));

        this.add(imageLabel, 0, 0, 1, 1);
        this.add(new Separator(), 0, 1, 1, 1);
        this.add(selectedButton, 0, 2, 1, 1);
        this.add(fileNameLabel, 0, 3, 1, 1);

        this.onUpdateSelectedImageFiles(imageFile, selection.getSelectedImageFiles());
        selection.getSelectedImageFiles().addListener((ListChangeListener.Change<? extends ImageFile> change) -> this.onUpdateSelectedImageFiles(imageFile, change.getList()));

        this.onUpdatePrimaryImageFile(imageFile, selection.getPrimaryImageFile().getValue());
        selection.getPrimaryImageFile().addListener((o, oldValue, newValue) -> this.onUpdatePrimaryImageFile(imageFile, newValue));

    }

    private void updateImage(ImageFile imageFile, Label imageLabel, IntegerProperty widthAndHeightProperty, int widthAndHeight, Executor thumnailsScalingExecutor) {
        thumnailsScalingExecutor.execute(() -> {
            Platform.runLater(() -> imageLabel.setText("Loading image..."));
            imageLabel.visibleProperty().addListener((o, oldValue, newValue) -> {
                System.err.println("X: " + newValue);
            });

            // TODO: We cannot use the original image, we *have* to scale it so that we don't run into
            //       an OutOfMemoryError!
            try {
                BufferedImage image = imageFile.loadBufferedImage();
                ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
                imageView.setPreserveRatio(true);
                imageView.fitWidthProperty().bind(widthAndHeightProperty);
                imageView.fitHeightProperty().bind(widthAndHeightProperty);
                Platform.runLater(() -> {
                    imageLabel.setText("");
                    imageLabel.setGraphic(imageView);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    imageLabel.setGraphic(null);
                    imageLabel.setText("Cannot load image!");
                });
            }
        });
    }

    private void onUpdateSelectedImageFiles(ImageFile imageFile, ObservableList<? extends ImageFile> selectedImageFiles) {
        if (selectedImageFiles.contains(imageFile)) {
            Platform.runLater(() -> this.setBackground(Background.fill(Color.rgb(255, 255, 255))));
        } else {
            Platform.runLater(() -> this.setBackground(Background.fill(Color.rgb(224, 224, 224))));
        }
    }

    private void onUpdatePrimaryImageFile(ImageFile imageFile, ImageFile primaryImageFile) {
        if (Objects.equals(imageFile, primaryImageFile)) {
            Platform.runLater(() -> this.setBorder(Border.stroke(Color.rgb(255, 0, 0))));
        } else {
            Platform.runLater(() -> this.setBorder(Border.stroke(Color.rgb(204, 204, 204))));
        }
    }

}
