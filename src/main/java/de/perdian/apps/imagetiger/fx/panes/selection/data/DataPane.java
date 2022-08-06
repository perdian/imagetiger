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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DataPane extends GridPane {

    public DataPane(Selection selection) {

        DataPaneControlFactory controlFactory = new DataPaneControlFactory(selection);

        TextField fileExtensionField = controlFactory.createTextField(imageFile -> imageFile.getFileExtension());
        fileExtensionField.setPrefWidth(50);
        GridPane.setHgrow(fileExtensionField, Priority.SOMETIMES);

        TextField fileDateStringField = controlFactory.createTextField(imageFile -> imageFile.getFileDateLocalString());
        TextField fileDateZoneField = controlFactory.createTextField(imageFile -> imageFile.getFileDateLocalZone());

        this.setHgap(10);
        this.setVgap(5);
        this.add(controlFactory.createLabel("File name"), 0, 0, 1, 1);
        this.add(controlFactory.createTextField(imageFile -> imageFile.getFileNameWithoutExtension()), 1, 0, 1, 1);
        this.add(controlFactory.createLabel("."), 2, 0, 1, 1);
        this.add(fileExtensionField, 3, 0, 1, 1);
        this.add(controlFactory.createLabel("File date"), 0, 1, 1, 1);
        this.add(fileDateStringField, 1, 1, 3, 1);
        this.add(controlFactory.createLabel("File date zone"), 0, 2, 1, 1);
        this.add(fileDateZoneField, 1, 2, 3, 1);

    }

}
