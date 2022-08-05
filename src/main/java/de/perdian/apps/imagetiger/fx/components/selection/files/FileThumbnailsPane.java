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

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.perdian.apps.imagetiger.fx.ImageTigerPreferences;
import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class FileThumbnailsPane extends BorderPane {

    public FileThumbnailsPane(Selection selection, ImageTigerPreferences preferences) {

        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(10, 10, 10, 10));
        flowPane.setHgap(10);
        flowPane.setVgap(10);

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        int widthAndHeightDefault = 50;
        IntegerProperty widthAndHeightProperty = preferences.createIntegerProperty("fileThumbnails.widthAndHeight", widthAndHeightDefault);

        Slider widthAndHeightSlider = new Slider(10, 500, widthAndHeightProperty.getValue());
        widthAndHeightSlider.setFocusTraversable(false);
        widthAndHeightSlider.setPadding(new Insets(0, 0, 0, 10));
        widthAndHeightSlider.valueProperty().bindBidirectional(widthAndHeightProperty);
        flowPane.widthProperty().addListener((o, oldValue, newValue) -> {
            if (newValue.intValue() > 0) {
                widthAndHeightSlider.setMax(newValue.intValue() - 30);
            }
        });

        BorderPane settingsPane = new BorderPane();
        settingsPane.setPadding(new Insets(7.5, 7.5, 7.5, 7.5));
        settingsPane.setLeft(new Label("Thumbnail width and height"));
        settingsPane.setCenter(widthAndHeightSlider);

        this.setCenter(scrollPane);
        this.setBottom(settingsPane);

        Executor thumnailsScalingExecutorTarget = Executors.newFixedThreadPool(5);
        selection.getAvailableImageFiles().addListener((ListChangeListener.Change<? extends ImageFile> change) -> {

            File selectedDirectory = selection.getSelectedDirectory().getValue();
            Executor thumnailsScalingExecutor = runnable -> {
                if (Objects.equals(selectedDirectory, selection.getSelectedDirectory().getValue())) {
                    thumnailsScalingExecutorTarget.execute(runnable);
                }
            };

            List<FileThumbnailPane> thumbnailPanes = change.getList().stream()
                .map(imageFile -> new FileThumbnailPane(selection, imageFile, widthAndHeightProperty, thumnailsScalingExecutor))
                .toList();

            Platform.runLater(() -> flowPane.getChildren().setAll(thumbnailPanes));

        });

    }

}
