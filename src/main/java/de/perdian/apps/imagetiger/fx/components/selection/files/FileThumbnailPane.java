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
package de.perdian.apps.imagetiger.fx.components.selection.files;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.Executor;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

class FileThumbnailPane extends BorderPane {

    FileThumbnailPane(Selection selection, ImageFile imageFile, IntegerProperty widthAndHeightProperty, Executor thumnailsScalingExecutor) {

        Label imageLabel = new Label();
        imageLabel.setAlignment(Pos.CENTER);
        imageLabel.prefWidthProperty().bind(widthAndHeightProperty);
        imageLabel.prefHeightProperty().bind(widthAndHeightProperty);
        this.updateImage(imageFile, imageLabel, widthAndHeightProperty, widthAndHeightProperty.intValue(), thumnailsScalingExecutor);
        this.setCenter(imageLabel);

        Label fileNameLabel = new Label();
        fileNameLabel.textProperty().bind(imageFile.getFileName());
        fileNameLabel.maxWidthProperty().bind(Bindings.subtract(widthAndHeightProperty, 10));
        BorderPane infoPane = new BorderPane();
        infoPane.setPadding(new Insets(5, 5, 5, 5));
        infoPane.setCenter(fileNameLabel);
        this.setBottom(infoPane);

        this.onUpdateSelectedImageFiles(imageFile, selection.getSelectedImageFiles());
        selection.getSelectedImageFiles().addListener((ListChangeListener.Change<? extends ImageFile> change) -> this.onUpdateSelectedImageFiles(imageFile, change.getList()));

        this.onUpdatePrimaryImageFile(imageFile, selection.getPrimaryImageFile().getValue());
        selection.getPrimaryImageFile().addListener((o, oldValue, newValue) -> this.onUpdatePrimaryImageFile(imageFile, newValue));

        this.setOnMouseClicked(event -> this.onMouseClickedEvent(imageFile, event, selection));

    }

    private void onMouseClickedEvent(ImageFile imageFile, MouseEvent event, Selection selection) {
        if (event.getButton() == MouseButton.PRIMARY) {
            this.toogleSelectedImageState(imageFile, selection);
        }
    }

    private void toogleSelectedImageState(ImageFile imageFile, Selection selection) {
        if (selection.getSelectedImageFiles().contains(imageFile)) {
            if (Objects.equals(imageFile, selection.getPrimaryImageFile().getValue())) {
                selection.getPrimaryImageFile().setValue(null);
            }
            selection.getSelectedImageFiles().remove(imageFile);
        } else {
            selection.getPrimaryImageFile().setValue(imageFile);
            selection.getSelectedImageFiles().add(imageFile);
        }
    }

    private void updateImage(ImageFile imageFile, Label imageLabel, IntegerProperty widthAndHeightProperty, int widthAndHeight, Executor thumnailsScalingExecutor) {
        thumnailsScalingExecutor.execute(() -> {
            Platform.runLater(() -> imageLabel.setText("Loading image..."));
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
