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
package de.perdian.apps.imagetiger.fx;

import java.io.File;
import java.util.Objects;

import de.perdian.apps.imagetiger.fx.actions.UpdateSelectionOnDirectoryChangeListener;
import de.perdian.apps.imagetiger.fx.components.directories.DirectoryPane;
import de.perdian.apps.imagetiger.fx.components.selection.SelectionPane;
import de.perdian.apps.imagetiger.fx.components.status.StatusPane;
import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import de.perdian.apps.imagetiger.fx.support.jobs.listeners.DisableWhileJobRunningJobListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class ImageTigerPane extends GridPane {

    ImageTigerPane(ImageTigerPreferences preferences) {

        Selection selection = new Selection();
        SelectionPane selectionPane = new SelectionPane(selection, preferences);
        GridPane.setHgrow(selectionPane, Priority.ALWAYS);

        JobExecutor jobExecutor = new JobExecutor();
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(selection.getBusy()));

        DirectoryPane directoryPane = new DirectoryPane();
        directoryPane.setMinWidth(300);
        directoryPane.selectedDirectoryProperty().addListener(new UpdateSelectionOnDirectoryChangeListener(selection, jobExecutor));
        GridPane.setVgrow(directoryPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane(jobExecutor);
        GridPane.setHgrow(statusPane, Priority.ALWAYS);

        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(10);
        this.setVgap(10);
        this.add(directoryPane, 0, 0, 1, 1);
        this.add(selectionPane, 1, 0, 1, 1);
        this.add(statusPane, 0, 1, 2, 1);

        // Ensure that the directory pane on the left side and the selection in which the files are displayed
        // are in synch and update with the initial values from the preferences
        File directoryDefault = new File(System.getProperty("user.home"));
        ObjectProperty<File> directoryPreferences = preferences.createFileProperty("selectedDirectory", directoryDefault);
        this.bindBidirectional(directoryPreferences, directoryPane.selectedDirectoryProperty());
        directoryPane.selectedDirectoryProperty().setValue(directoryPreferences.getValue());

    }

    private <T> void bindBidirectional(Property<T> p1, Property<T> p2) {
        p1.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue, p2.getValue())) {
                p2.setValue(newValue);
            }
        });
        p2.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue, p1.getValue())) {
                p1.setValue(newValue);
            }
        });
    }

}
