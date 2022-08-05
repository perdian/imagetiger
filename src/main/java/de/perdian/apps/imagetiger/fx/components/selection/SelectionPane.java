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
package de.perdian.apps.imagetiger.fx.components.selection;

import de.perdian.apps.imagetiger.fx.ImageTigerPreferences;
import de.perdian.apps.imagetiger.fx.components.selection.actions.ActionsPane;
import de.perdian.apps.imagetiger.fx.components.selection.files.FileListPane;
import de.perdian.apps.imagetiger.fx.components.selection.files.FileThumbnailsPane;
import de.perdian.apps.imagetiger.fx.components.selection.tags.TagsPane;
import de.perdian.apps.imagetiger.fx.model.Selection;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SelectionPane extends GridPane {

    public SelectionPane(Selection selection, ImageTigerPreferences preferences) {

        FileListPane fileListPane = new FileListPane(selection);
        fileListPane.disableProperty().bind(selection.getBusy());
        GridPane.setVgrow(fileListPane, Priority.ALWAYS);

        FileThumbnailsPane fileThumbnailsPane = new FileThumbnailsPane(selection, preferences);
        fileThumbnailsPane.disableProperty().bind(selection.getBusy());
        TitledPane fileThumbnailsTitledPane = new TitledPane("Thumbnails", fileThumbnailsPane);
        fileThumbnailsTitledPane.setFocusTraversable(false);
        fileThumbnailsTitledPane.setCollapsible(false);
        fileThumbnailsTitledPane.setMaxHeight(Double.MAX_VALUE);
        fileThumbnailsTitledPane.setBorder(null);
        GridPane.setHgrow(fileThumbnailsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(fileThumbnailsTitledPane, Priority.ALWAYS);

        ActionsPane actionsPane = new ActionsPane(selection);
        actionsPane.disableProperty().bind(selection.getBusy());
        actionsPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane actionsTitledPane = new TitledPane("Actions", actionsPane);
        actionsTitledPane.setFocusTraversable(false);
        actionsTitledPane.setCollapsible(false);
        GridPane.setHgrow(actionsTitledPane, Priority.ALWAYS);

        TagsPane tagsPane = new TagsPane(selection);
        tagsPane.disableProperty().bind(selection.getBusy());
        tagsPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane tagsTitledPane = new TitledPane("Tags", tagsPane);
        tagsTitledPane.setFocusTraversable(false);
        tagsTitledPane.setCollapsible(false);
        GridPane.setHgrow(tagsTitledPane, Priority.ALWAYS);

        this.setHgap(10);
        this.setVgap(10);
        this.add(fileListPane, 0, 0, 1, 1);
        this.add(fileThumbnailsTitledPane, 1, 0, 1, 1);
        this.add(actionsTitledPane, 0, 1, 2, 1);
        this.add(tagsTitledPane, 0, 2, 2, 1);

    }

}
