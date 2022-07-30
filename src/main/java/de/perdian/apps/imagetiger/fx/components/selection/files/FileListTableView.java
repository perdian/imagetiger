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

import java.util.List;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class FileListTableView extends TableView<ImageFile> {

    FileListTableView(ObservableList<ImageFile> availableFiles) {
        super(availableFiles);

        TableColumn<ImageFile, Boolean> dirtyColumn = new TableColumn<>("");
        dirtyColumn.setCellValueFactory(callback -> callback.getValue().getDirty());
        dirtyColumn.setCellFactory(this.createIconCellCallback(MaterialDesignF.FLAG, null));
        dirtyColumn.setSortable(false);
        dirtyColumn.setReorderable(false);
        dirtyColumn.setMinWidth(25);
        dirtyColumn.setMaxWidth(25);

        TableColumn<ImageFile, Boolean> focusColumn = new TableColumn<>("");
        focusColumn.setCellValueFactory(callback -> callback.getValue().getPrimary());
        focusColumn.setCellFactory(this.createIconCellCallback(MaterialDesignA.ARROW_RIGHT_BOLD, null));
        focusColumn.setSortable(false);
        focusColumn.setReorderable(false);
        focusColumn.setMinWidth(25);
        focusColumn.setMaxWidth(25);

        TableColumn<ImageFile, String> fileNameColumn = new TableColumn<>("File name");
        fileNameColumn.setCellValueFactory(callback -> new SimpleStringProperty(callback.getValue().toString()));

        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().setAll(List.of(dirtyColumn, focusColumn, fileNameColumn));
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private Callback<TableColumn<ImageFile, Boolean>, TableCell<ImageFile, Boolean>> createIconCellCallback(Ikon trueIcon, Ikon falseIcon) {
        return column -> {
            TableCell<ImageFile, Boolean> tableCell = new TableCell<>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (!empty) {
                        if (item != null && item.booleanValue()) {
                            this.setGraphic(trueIcon == null ? null : new Label("", new FontIcon(trueIcon)));
                        } else {
                            this.setGraphic(falseIcon == null ? null : new Label("", new FontIcon(falseIcon)));
                        }
                    } else {
                        this.setGraphic(null);
                    }
                }
            };
            return tableCell;
        };
    }

}
