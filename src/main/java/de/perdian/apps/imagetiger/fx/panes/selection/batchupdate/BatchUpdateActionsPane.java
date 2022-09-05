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

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignU;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateItem;
import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateSettings;
import de.perdian.apps.imagetiger.fx.panes.selection.batchupdate.actions.BatchUpdateComputeActionEventHandler;
import de.perdian.apps.imagetiger.fx.panes.selection.batchupdate.actions.BatchUpdateResetActionEventHandler;
import de.perdian.apps.imagetiger.fx.panes.selection.batchupdate.actions.BatchUpdateTransferActionEventHandler;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;

class BatchUpdateActionsPane extends ButtonBar {

    BatchUpdateActionsPane(ObservableList<BatchUpdateItem> allItems, ObservableList<BatchUpdateItem> selectedItems, BatchUpdateSettings settings, JobExecutor jobExecutor, ObjectProperty<EventHandler<ActionEvent>> onTransferProperty) {

        Button computeButton = new Button("Compute new properties", new FontIcon(MaterialDesignU.UPDATE));
        computeButton.setOnAction(new BatchUpdateComputeActionEventHandler(selectedItems, settings, jobExecutor));
        computeButton.disableProperty().bind(Bindings.or(Bindings.isEmpty(selectedItems), Bindings.or(Bindings.not(settings.getReady()), jobExecutor.getBusy())));
        ButtonBar.setButtonData(computeButton, ButtonData.LEFT);

        Button resetButton = new Button("Reset properties", new FontIcon(MaterialDesignR.RELOAD));
        resetButton.setOnAction(new BatchUpdateResetActionEventHandler(selectedItems, jobExecutor));
        resetButton.disableProperty().bind(Bindings.or(Bindings.isEmpty(selectedItems), Bindings.or(Bindings.not(settings.getReady()), jobExecutor.getBusy())));
        ButtonBar.setButtonData(resetButton, ButtonData.LEFT);

        Button transferButton = new Button("Update image files", new FontIcon(MaterialDesignE.EXIT_TO_APP));
        transferButton.setOnAction(new BatchUpdateTransferActionEventHandler(allItems, jobExecutor, onTransferProperty));
        transferButton.disableProperty().bind(Bindings.or(Bindings.isEmpty(allItems), jobExecutor.getBusy()));

        this.getButtons().addAll(computeButton, resetButton, transferButton);

    }

}
