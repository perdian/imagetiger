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

import de.perdian.apps.imagetiger.fx.model.selection.Selection;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

public class BatchUpdateDialog extends Dialog<Void> {

    public BatchUpdateDialog(Window parentWindow, Selection selection, JobExecutor jobExecutor) {

        BatchUpdateDialogPane batchUpdateDialogPane = new BatchUpdateDialogPane(selection, jobExecutor);
        batchUpdateDialogPane.setPadding(new Insets(10, 10, 10, 10));

        this.getDialogPane().setContent(batchUpdateDialogPane);
        this.getDialogPane().setPrefSize(1600, 1200);
        this.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> this.close());

        this.setTitle("Batch image update");
        this.initOwner(parentWindow);

    }

}
