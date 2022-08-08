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

import de.perdian.apps.imagetiger.fx.model.Selection;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public class BatchUpdateDialogActionEventHandler implements EventHandler<ActionEvent> {

    private Node parentNode = null;
    private Selection selection = null;
    private JobExecutor jobExecutor = null;

    public BatchUpdateDialogActionEventHandler(Node parentNode, Selection selection, JobExecutor jobExecutor) {
        this.setParentNode(parentNode);
        this.setSelection(selection);
        this.setJobExecutor(jobExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
        BatchUpdateDialog dialog = new BatchUpdateDialog(this.getParentNode().getScene().getWindow(), this.getSelection(), this.getJobExecutor());
        dialog.show();
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

}
