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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.BorderPane;

public class FileListPane extends BorderPane {

    public FileListPane(Selection selection) {

        AtomicBoolean tableViewSelectionListenerActive = new AtomicBoolean(false);
        FileListTableView tableView = new FileListTableView(selection.getAvailableImageFiles());
        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends ImageFile> change) -> {
            if (!tableViewSelectionListenerActive.get()) {
                List<? extends ImageFile> newSelectedFiles = change.getList();
                List<ImageFile> selectionFiles = selection.getSelectedImageFiles();
                if (!newSelectedFiles.equals(selectionFiles)) {
                    selection.getSelectedImageFiles().setAll(newSelectedFiles);
                }
            }
        });
        selection.getSelectedImageFiles().addListener((ListChangeListener.Change<? extends ImageFile> change) -> {
            List<? extends ImageFile> newSelectedFiles = change.getList();
            List<ImageFile> tableViewFiles = tableView.getSelectionModel().getSelectedItems();
            if (!newSelectedFiles.equals(tableViewFiles)) {
                tableViewSelectionListenerActive.set(true);
                try {
                    tableView.getSelectionModel().clearSelection();
                    for (int i=0; i < tableView.getItems().size(); i++) {
                        if (newSelectedFiles.contains(tableView.getItems().get(i))) {
                            tableView.getSelectionModel().select(i);
                        }
                    }
                } finally {
                    tableViewSelectionListenerActive.set(false);
                }
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (!tableViewSelectionListenerActive.get()) {
                if (!Objects.equals(newValue, selection.getPrimaryImageFile().getValue())) {
                    selection.getPrimaryImageFile().setValue(newValue);
                }
            }
        });
        this.setCenter(tableView);

    }

}
