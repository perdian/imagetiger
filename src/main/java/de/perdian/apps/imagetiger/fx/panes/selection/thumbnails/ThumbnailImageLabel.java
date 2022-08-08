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
package de.perdian.apps.imagetiger.fx.panes.selection.thumbnails;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;

import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

class ThumbnailImageLabel extends Label {

    private Image currentImage = null;

    ThumbnailImageLabel(ImageFile imageFile, IntegerProperty widthAndHeightProperty, Executor thumnailsScalingExecutor) {
        this.minWidthProperty().bind(this.prefWidthProperty());
        this.minHeightProperty().bind(this.prefHeightProperty());
        this.maxWidthProperty().bind(this.prefWidthProperty());
        this.maxHeightProperty().bind(this.prefHeightProperty());
        this.recomputeImage(imageFile, widthAndHeightProperty.intValue(), widthAndHeightProperty.intValue(), thumnailsScalingExecutor);
        widthAndHeightProperty.addListener((o, oldValue, newValue) -> this.recomputeImage(imageFile, widthAndHeightProperty.getValue(), widthAndHeightProperty.getValue(), thumnailsScalingExecutor));
        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {
                imageFile.openInNativeViewer();
            }
        });
    }

    private void recomputeImage(ImageFile imageFile, int width, int height, Executor thumnailsScalingExecutor) {
        if (width <= 0 || height <= 0) {
            return; // Invalid image bounds
        } else if (this.getCurrentImage() != null && width == (int)this.getCurrentImage().getWidth() && height == (int)this.getCurrentImage().getHeight()) {
            return; // Identical image dimensions, no rescaling necessary
        } else {
            this.setText("Loading image...");
            thumnailsScalingExecutor.execute(() -> this.recomputeImageAsync(imageFile, width, height));
        }
    }

    private void recomputeImageAsync(ImageFile imageFile, int width, int height) {

        // Only compute a scaled version if the bounds are still identical. There might have been several
        // rescaling requests all piled up to be performed but as we only care about the most recent one,
        // we'll ignore everything that isn't relevant any more
        if (width != (int)this.getPrefWidth() || height != this.getPrefHeight()) {
            return;
        }

        try {

            BufferedImage originalImage = imageFile.loadBufferedImage();
            BufferedImage scaledImage = this.createScaledImage(originalImage, width, height);
            Image image = SwingFXUtils.toFXImage(scaledImage, null);
            originalImage.flush();

            // Only update the image if the bounds are still identical
            synchronized (this) {
                if (width == (int)this.getPrefWidth() && height == (int)this.getPrefHeight()) {
                    Platform.runLater(() -> {
                        this.setText("");
                        this.setGraphic(new ImageView(image));
                    });
                    this.setCurrentImage(image);;
                }
            }

        } catch (Throwable e) {
            Platform.runLater(() -> {
                this.setGraphic(null);
                this.setText("Cannot load image!");
            });
        }
    }

    private BufferedImage createScaledImage(BufferedImage originalImage, int width, int height) {

        double factorX = (double)width / originalImage.getWidth();
        double factorY = (double)height / originalImage.getHeight();
        double factor = Math.min(factorX, factorY);
        int newWidth = (int)(originalImage.getWidth() * factor);
        int newHeight = (int)(originalImage.getHeight() * factor);

        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D scaledGraphics = scaledImage.createGraphics();
        scaledGraphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        scaledGraphics.dispose();
        return scaledImage;

    }

    private Image getCurrentImage() {
        return this.currentImage;
    }
    private void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

}
