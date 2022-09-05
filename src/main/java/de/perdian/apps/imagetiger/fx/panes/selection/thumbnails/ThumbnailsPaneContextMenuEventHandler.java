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
package de.perdian.apps.imagetiger.fx.panes.selection.thumbnails;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import de.perdian.apps.imagetiger.fx.ImageTigerPreferences;
import de.perdian.apps.imagetiger.fx.model.selection.Selection;
import de.perdian.apps.imagetiger.fx.panes.selection.batchupdate.BatchUpdateDialog;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Window;

class ThumbnailsPaneContextMenuEventHandler implements EventHandler<ContextMenuEvent> {

    private Node parentNode = null;
    private Selection selection = null;
    private JobExecutor jobExecutor = null;
    private ImageTigerPreferences preferences = null;

    ThumbnailsPaneContextMenuEventHandler(Node parentNode, Selection selection, JobExecutor jobExecutor, ImageTigerPreferences preferences) {
        this.setParentNode(parentNode);
        this.setSelection(selection);
        this.setJobExecutor(jobExecutor);
        this.setPreferences(preferences);
    }

    @Override
    public void handle(ContextMenuEvent event) {

        MenuItem batchUpdateMenuItem = new MenuItem("Batch update", new FontIcon(MaterialDesignF.FOLDER_TABLE_OUTLINE));
        batchUpdateMenuItem.setOnAction(e -> new BatchUpdateDialog(this.getParentNode().getScene().getWindow(), this.getSelection(), this.getJobExecutor(), this.getPreferences()).show());

        Window ownerWindow = this.getParentNode().getScene().getWindow();
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(batchUpdateMenuItem);
        contextMenu.show(ownerWindow, event.getScreenX(), event.getScreenY());

    }

    private Node getParentNode() {
        return this.parentNode;
    }
    private void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    private ImageTigerPreferences getPreferences() {
        return this.preferences;
    }
    private void setPreferences(ImageTigerPreferences preferences) {
        this.preferences = preferences;
    }

}
