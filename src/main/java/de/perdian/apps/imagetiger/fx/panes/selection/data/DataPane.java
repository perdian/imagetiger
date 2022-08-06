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
package de.perdian.apps.imagetiger.fx.panes.selection.data;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageDataKey;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DataPane extends GridPane {

    public DataPane(Selection selection) {

        DataPaneControlFactory controlFactory = new DataPaneControlFactory(selection);

        TextField fileExtensionField = controlFactory.createTextField(imageFile -> imageFile.getFileExtension(), true);
        fileExtensionField.setPrefWidth(50);
        GridPane.setHgrow(fileExtensionField, Priority.SOMETIMES);

        TextField fileDateStringField = controlFactory.createTextField(imageFile -> imageFile.getFileDateLocalString(), true);
        TextField fileDateZoneField = controlFactory.createTextField(imageFile -> imageFile.getFileDateLocalZone(), true);

        GridPane fileDataPane = new GridPane();
        fileDataPane.setHgap(10);
        fileDataPane.setVgap(5);
        fileDataPane.add(controlFactory.createLabel("File name"), 0, 0, 1, 1);
        fileDataPane.add(controlFactory.createTextField(imageFile -> imageFile.getFileNameWithoutExtension(), true), 1, 0, 1, 1);
        fileDataPane.add(controlFactory.createLabel("."), 2, 0, 1, 1);
        fileDataPane.add(fileExtensionField, 3, 0, 1, 1);
        fileDataPane.add(controlFactory.createLabel("File date"), 0, 1, 1, 1);
        fileDataPane.add(fileDateStringField, 1, 1, 3, 1);
        fileDataPane.add(controlFactory.createLabel("File date zone"), 0, 2, 1, 1);
        fileDataPane.add(fileDateZoneField, 1, 2, 3, 1);
        GridPane.setHgrow(fileDataPane, Priority.ALWAYS);

        GridPane metaDataPane = new GridPane();
        metaDataPane.setHgap(10);
        metaDataPane.setVgap(5);
        metaDataPane.add(controlFactory.createLabel("Metadata date"), 0, 0, 1, 1);
        metaDataPane.add(controlFactory.createTextField(imageFile -> imageFile.getProperty(ImageDataKey.DATETIME), false), 1, 0, 1, 1);
        metaDataPane.add(controlFactory.createLabel("Metadata timezone"), 2, 0, 1, 1);
        metaDataPane.add(controlFactory.createTextField(imageFile -> imageFile.getProperty(ImageDataKey.DATETIME_ZONE), false), 3, 0, 1, 1);
        metaDataPane.add(controlFactory.createLabel("Metadata width"), 0, 1, 1, 1);
        metaDataPane.add(controlFactory.createTextField(imageFile -> imageFile.getProperty(ImageDataKey.WIDTH), false), 1, 1, 1, 1);
        metaDataPane.add(controlFactory.createLabel("Metadata height"), 2, 1, 1, 1);
        metaDataPane.add(controlFactory.createTextField(imageFile -> imageFile.getProperty(ImageDataKey.HEIGHT), false), 3, 1, 1, 1);
        metaDataPane.add(controlFactory.createLabel("Metadata type"), 0, 2, 1, 1);
        metaDataPane.add(controlFactory.createTextField(imageFile -> imageFile.getProperty(ImageDataKey.MIME_TYPE), false), 1, 2, 3, 1);
        GridPane.setHgrow(metaDataPane, Priority.ALWAYS);

        this.setHgap(20);
        this.add(fileDataPane, 0, 0, 1, 1);
        this.add(metaDataPane, 1, 0, 1, 1);

    }

}
