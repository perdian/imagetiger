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

import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import de.perdian.apps.imagetiger.fx.support.tables.TableViewHelper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

class BatchUpdateItemTableView extends TableView<BatchUpdateItem> {

    BatchUpdateItemTableView(ObservableList<BatchUpdateItem> items) {
        super(items);


        TableColumn<BatchUpdateItem, Boolean> dirtyColumn = new TableColumn<>("");
        dirtyColumn.setCellValueFactory(callback -> callback.getValue().getDirty());
        dirtyColumn.setCellFactory(TableViewHelper.createIconCellCallback(MaterialDesignF.FLAG, null));
        dirtyColumn.setSortable(false);
        dirtyColumn.setReorderable(false);
        dirtyColumn.setMinWidth(25);
        dirtyColumn.setMaxWidth(25);

        TableColumn<BatchUpdateItem, String> originalFileNameColumn = new TableColumn<>("Original file name");
        originalFileNameColumn.setPrefWidth(300);
        originalFileNameColumn.setCellValueFactory(features -> features.getValue().getFileName().getOriginalValue());

        TableColumn<BatchUpdateItem, String> newFileNameWithoutExtensionColumn = new TableColumn<>("New file name");
        newFileNameWithoutExtensionColumn.setCellValueFactory(features -> features.getValue().getFileNameWithoutExtension().getNewValue());

        TableColumn<BatchUpdateItem, String> newFileExtensionColumn = new TableColumn<>("Ext");
        newFileExtensionColumn.setMinWidth(75);
        newFileExtensionColumn.setMaxWidth(75);
        newFileExtensionColumn.setCellValueFactory(features -> features.getValue().getFileExtension().getNewValue());

        TableColumn<BatchUpdateItem, String> originalFileDateLocalColumn = new TableColumn<>("Original file date");
        originalFileDateLocalColumn.setCellValueFactory(features -> features.getValue().getFileDateLocalString().getOriginalValue());
        originalFileDateLocalColumn.setMinWidth(200);
        originalFileDateLocalColumn.setPrefWidth(300);
        originalFileDateLocalColumn.setMaxWidth(300);

        TableColumn<BatchUpdateItem, String> newFileDateLocalStringColumn = new TableColumn<>("New file date");
        newFileDateLocalStringColumn.setCellValueFactory(features -> features.getValue().getFileDateLocalString().getNewValue());
        newFileDateLocalStringColumn.setMinWidth(200);
        newFileDateLocalStringColumn.setPrefWidth(300);
        newFileDateLocalStringColumn.setMaxWidth(300);

        TableColumn<BatchUpdateItem, String> newFileDateLocalZoneColumn = new TableColumn<>("New file date zone");
        newFileDateLocalZoneColumn.setCellValueFactory(features -> features.getValue().getFileDateLocalZone().getNewValue());
        newFileDateLocalZoneColumn.setMinWidth(150);
        newFileDateLocalZoneColumn.setPrefWidth(200);
        newFileDateLocalZoneColumn.setMaxWidth(200);

        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().add(dirtyColumn);
        this.getColumns().addAll(List.of(originalFileNameColumn, newFileNameWithoutExtensionColumn, newFileExtensionColumn));
        this.getColumns().addAll(List.of(originalFileDateLocalColumn, newFileDateLocalStringColumn, newFileDateLocalZoneColumn));

    }

}
