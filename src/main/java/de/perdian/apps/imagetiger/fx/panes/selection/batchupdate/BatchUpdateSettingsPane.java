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

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class BatchUpdateSettingsPane extends GridPane {

    BatchUpdateSettingsPane(BatchUpdateSettings settings) {

        Label originalFileNamePatternLabel = new Label("Original file name pattern");
        TextField originalFileNamePatternField = new TextField();
        originalFileNamePatternField.textProperty().bindBidirectional(settings.getOriginalFileNamePattern());

        Label newFileNameLabel = new Label("New file name");
        TextField newFileNameField = new TextField();
        newFileNameField.textProperty().bindBidirectional(settings.getNewFileName());
        GridPane.setHgrow(newFileNameField, Priority.ALWAYS);

        Label newFileExtensionLabel = new Label("New file extension");
        TextField newFileExtensionField = new TextField();
        newFileExtensionField.textProperty().bindBidirectional(settings.getNewFileExtension());
        newFileExtensionField.setPrefWidth(150);

        Label newFileDateStringLabel = new Label("New file date");
        TextField newFileDateStringField = new TextField();
        newFileDateStringField.textProperty().bindBidirectional(settings.getNewFileDateLocalString());
        GridPane.setHgrow(newFileDateStringField, Priority.ALWAYS);

        Label newFileDateZoneLabel = new Label("New file date timezone");
        TextField newFileDateZoneField = new TextField();
        newFileDateZoneField.textProperty().bindBidirectional(settings.getNewFileDateLocalZone());
        newFileDateZoneField.setPrefWidth(150);

        this.setHgap(10);
        this.setVgap(5);
        this.add(originalFileNamePatternLabel, 0, 0, 2, 1);
        this.add(originalFileNamePatternField, 0, 1, 2, 1);
        this.add(newFileNameLabel, 0, 2, 1, 1);
        this.add(newFileNameField, 0, 3, 1, 1);
        this.add(newFileExtensionLabel, 1, 2, 1, 1);
        this.add(newFileExtensionField, 1, 3, 1, 1);
        this.add(newFileDateStringLabel, 0, 4, 1, 1);
        this.add(newFileDateStringField, 0, 5, 1, 1);
        this.add(newFileDateZoneLabel, 1, 4, 1, 1);
        this.add(newFileDateZoneField, 1, 5, 1, 1);

    }

}
