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
package de.perdian.apps.imagetiger.fx.panes.selection;

import de.perdian.apps.imagetiger.fx.ImageTigerPreferences;
import de.perdian.apps.imagetiger.fx.model.selection.Selection;
import de.perdian.apps.imagetiger.fx.panes.selection.actions.ActionsPane;
import de.perdian.apps.imagetiger.fx.panes.selection.data.DataPane;
import de.perdian.apps.imagetiger.fx.panes.selection.list.ListPane;
import de.perdian.apps.imagetiger.fx.panes.selection.thumbnails.ThumbnailsPane;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SelectionPane extends GridPane {

    public SelectionPane(Selection selection, JobExecutor jobExecutor, ImageTigerPreferences preferences) {

        ListPane listPane = new ListPane(selection);
        listPane.disableProperty().bind(selection.getBusy());
        GridPane.setVgrow(listPane, Priority.ALWAYS);

        ThumbnailsPane thumbnailsPane = new ThumbnailsPane(selection, jobExecutor, preferences);
        thumbnailsPane.disableProperty().bind(selection.getBusy());
        TitledPane fileThumbnailsTitledPane = new TitledPane("Thumbnails", thumbnailsPane);
        fileThumbnailsTitledPane.setFocusTraversable(false);
        fileThumbnailsTitledPane.setCollapsible(false);
        fileThumbnailsTitledPane.setMaxHeight(Double.MAX_VALUE);
        fileThumbnailsTitledPane.setBorder(null);
        GridPane.setHgrow(fileThumbnailsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(fileThumbnailsTitledPane, Priority.ALWAYS);

        ActionsPane actionsPane = new ActionsPane(selection);
        actionsPane.disableProperty().bind(selection.getBusy());
        actionsPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane actionsTitledPane = new TitledPane("Actions", actionsPane);
        actionsTitledPane.setFocusTraversable(false);
        actionsTitledPane.setCollapsible(false);

        DataPane dataPane = new DataPane(selection);
        dataPane.disableProperty().bind(selection.getBusy());
        dataPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane dataTitledPane = new TitledPane("Data", dataPane);
        dataTitledPane.setFocusTraversable(false);
        dataTitledPane.setCollapsible(false);
        GridPane.setHgrow(dataTitledPane, Priority.ALWAYS);

        this.setHgap(10);
        this.setVgap(10);
        this.add(listPane, 0, 0, 1, 1);
        this.add(actionsTitledPane, 0, 1, 1, 1);
        this.add(fileThumbnailsTitledPane, 1, 0, 1, 2);
        this.add(dataTitledPane, 0, 2, 2, 1);

    }

}
