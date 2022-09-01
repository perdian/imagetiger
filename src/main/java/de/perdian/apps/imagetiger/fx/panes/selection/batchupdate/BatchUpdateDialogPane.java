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

import java.util.List;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateItem;
import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateSettings;
import de.perdian.apps.imagetiger.fx.model.selection.Selection;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class BatchUpdateDialogPane extends GridPane {

    BatchUpdateDialogPane(Selection selection, JobExecutor jobExecutor) {

        List<BatchUpdateItem> items = selection.getAvailableImageFiles().stream().map(BatchUpdateItem::new).toList();
        ObservableList<BatchUpdateItem> observableItems = FXCollections.observableArrayList(items);

        BatchUpdateSettings settings = new BatchUpdateSettings();

        BatchUpdateSettingsPane settingsPane = new BatchUpdateSettingsPane(settings);
        settingsPane.setPadding(new Insets(10, 10, 10, 10));
        settingsPane.disableProperty().bind(selection.getBusy());
        TitledPane settingsTitledPane = new TitledPane("Settings", settingsPane);
        settingsTitledPane.setFocusTraversable(false);
        settingsTitledPane.setCollapsible(false);
        GridPane.setHgrow(settingsTitledPane, Priority.ALWAYS);

        BatchUpdateItemTableView itemsTableView = new BatchUpdateItemTableView(observableItems);
        itemsTableView.disableProperty().bind(selection.getBusy());
        itemsTableView.getSelectionModel().selectAll();
        GridPane.setHgrow(itemsTableView, Priority.ALWAYS);
        GridPane.setVgrow(itemsTableView, Priority.ALWAYS);

        BatchUpdateActionsPane actionsPane = new BatchUpdateActionsPane(observableItems, itemsTableView.getSelectionModel().getSelectedItems(), settings, jobExecutor);
        actionsPane.setPadding(new Insets(10, 10, 10, 10));
        actionsPane.disableProperty().bind(selection.getBusy());
        TitledPane actionsTitledPane = new TitledPane("Actions", actionsPane);
        actionsTitledPane.setFocusTraversable(false);
        actionsTitledPane.setCollapsible(false);
        GridPane.setHgrow(actionsTitledPane, Priority.ALWAYS);

        this.add(settingsTitledPane, 0, 0, 1, 1);
        this.add(actionsTitledPane, 0, 1, 1, 1);
        this.add(itemsTableView, 0, 2, 1, 1);

        this.setHgap(10);
        this.setVgap(10);

    }

}
