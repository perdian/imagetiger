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

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

class BatchUpdateActionsPane extends FlowPane {

    BatchUpdateActionsPane(List<BatchUpdateItem> items, BatchUpdateSettings settings, JobExecutor jobExecutor) {

        Button executeButton = new Button("Execute update", new FontIcon(MaterialDesignP.PLAY_BOX));
        executeButton.setOnAction(new BatchUpdateExecuteActionEventHandler(items, settings, jobExecutor));
        executeButton.disableProperty().bind(Bindings.or(Bindings.not(settings.getReady()), jobExecutor.getBusy()));

        this.getChildren().add(executeButton);

    }

}
