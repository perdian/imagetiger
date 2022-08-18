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
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignU;

import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

class BatchUpdateActionsPane extends FlowPane {

    BatchUpdateActionsPane(ObservableList<BatchUpdateItem> allItems, ObservableList<BatchUpdateItem> selectedItems, BatchUpdateSettings settings, JobExecutor jobExecutor) {

        Button prepareButton = new Button("Update properties", new FontIcon(MaterialDesignU.UPDATE));
        prepareButton.setOnAction(new BatchUpdatePrepareActionEventHandler(selectedItems, settings, jobExecutor));
        prepareButton.disableProperty().bind(Bindings.or(Bindings.isEmpty(selectedItems), Bindings.or(Bindings.not(settings.getReady()), jobExecutor.getBusy())));

        Button executeButton = new Button("Execute update", new FontIcon(MaterialDesignP.PLAY));
        executeButton.disableProperty().bind(Bindings.or(Bindings.isEmpty(allItems), jobExecutor.getBusy()));

        this.setHgap(5);
        this.getChildren().addAll(prepareButton, executeButton);

    }

}
