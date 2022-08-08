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

import de.perdian.apps.imagetiger.fx.model.Selection;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Window;

class ThumbnailsPaneContextMenuEventHandler implements EventHandler<ContextMenuEvent> {

    private Node parentNode = null;
    private Selection selection = null;

    ThumbnailsPaneContextMenuEventHandler(Node parentNode, Selection selection) {
        this.setParentNode(parentNode);
        this.setSelection(selection);

    }

    @Override
    public void handle(ContextMenuEvent event) {
        Window ownerWindow = this.getParentNode().getScene().getWindow();
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(new MenuItem("FOO"));
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

}
