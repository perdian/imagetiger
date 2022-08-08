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
package de.perdian.apps.imagetiger.fx.panes.selection.actions;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class ActionsPane extends BorderPane {

    public ActionsPane(Selection selection) {

        ObservableList<ImageFile> dirtyFiles = selection.getDirtyImageFiles();
        Button saveChangedFilesButton = new Button("Save changed files", new FontIcon(MaterialDesignC.CONTENT_SAVE));
        saveChangedFilesButton.setOnAction(event -> selection.saveDirtyFiles());
        saveChangedFilesButton.disableProperty().bind(Bindings.isEmpty(dirtyFiles));

        FlowPane buttonPane = new FlowPane();
        buttonPane.setPrefWidth(0);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().add(saveChangedFilesButton);
        this.setCenter(buttonPane);

    }

}
