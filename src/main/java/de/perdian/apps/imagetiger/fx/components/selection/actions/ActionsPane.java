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
package de.perdian.apps.imagetiger.fx.components.selection.actions;

import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class ActionsPane extends BorderPane {

    public ActionsPane(Selection selection) {

        Button selectButton = new Button("Select");
        selectButton.setOnAction(event -> {
            List<ImageFile> allFiles = selection.getAvailableImageFiles();
            if (allFiles.size() > 3) {
                List<ImageFile> selectedFiles = new ArrayList<>();
                selectedFiles.add(allFiles.get(0));
                selectedFiles.add(allFiles.get(2));
                selection.getSelectedImageFiles().setAll(selectedFiles);

                selection.getAvailableImageFiles().get(2).getDirty().setValue(true);
                selection.getAvailableImageFiles().get(4).getDirty().setValue(true);

            }
        });
        this.setCenter(selectButton);

    }

}
