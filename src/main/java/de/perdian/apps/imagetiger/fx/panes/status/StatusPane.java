/*
 * Copyright 2014-2022 Christian Seifert
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
package de.perdian.apps.imagetiger.fx.panes.status;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;

import de.perdian.apps.imagetiger.fx.support.jobs.Job;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import de.perdian.apps.imagetiger.fx.support.jobs.JobListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class StatusPane extends GridPane implements JobListener {

    private Label progressLabel = null;
    private ProgressBar progressBar = null;
    private Button cancelButton = null;
    private JobExecutor jobExecutor = null;

    public StatusPane(JobExecutor jobExecutor) {

        Label progressLabel = new Label("");
        GridPane.setHgrow(progressLabel, Priority.ALWAYS);
        this.setProgressLabel(progressLabel);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0);
        progressBar.setPrefWidth(150);
        progressBar.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(progressBar, Priority.ALWAYS);
        this.setProgressBar(progressBar);

        Button cancelButton = new Button("Cancel", new FontIcon(MaterialDesignS.STOP));
        cancelButton.setOnAction(action -> this.handleCancel());
        cancelButton.setDisable(true);
        this.setCancelButton(cancelButton);

        this.add(progressLabel, 0, 0, 1, 1);
        this.add(progressBar, 1, 0, 1, 1);
        this.add(cancelButton, 2, 0, 1, 1);
        this.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, null, null, null, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));
        this.setHgap(5);
        this.setPadding(new Insets(10, 0, 0, 0));

        this.setJobExecutor(jobExecutor);
        jobExecutor.addListener(this);

    }

    private void handleCancel() {
        Platform.runLater(() -> {
            this.getCancelButton().setDisable(true);
        });
        this.getJobExecutor().cancelCurrentJob();
    }

    @Override
    public void jobStarted(Job job) {
        Platform.runLater(() -> {
            this.getProgressBar().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            this.getProgressBar().setDisable(false);
            this.getCancelButton().setDisable(false);
        });
    }

    @Override
    public void jobProgress(Job job, String progressMessage, Integer progressStep, Integer totalProgressSteps) {
        Platform.runLater(() -> {
            if (progressStep != null && totalProgressSteps != null) {
                if (progressStep.intValue() < 0) {
                    this.getProgressBar().setProgress(-1d);
                } else  if (totalProgressSteps.intValue() == 0) {
                    this.getProgressBar().setProgress(0d);
                } else {
                    this.getProgressBar().setProgress((1d / totalProgressSteps) * (progressStep + 1));
                }
            }
            this.getProgressLabel().setText(progressMessage);
        });
    }

    @Override
    public void jobCompleted(Job job, boolean otherJobsActive) {
        if (!otherJobsActive) {
            Platform.runLater(() -> {
                this.getProgressLabel().setText(null);
                this.getProgressBar().setDisable(true);
                this.getProgressBar().setProgress(0);
                this.getCancelButton().setDisable(true);
            });
        }
    }

    private Label getProgressLabel() {
        return this.progressLabel;
    }
    private void setProgressLabel(Label progressLabel) {
        this.progressLabel = progressLabel;
    }

    private ProgressBar getProgressBar() {
        return this.progressBar;
    }
    private void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private Button getCancelButton() {
        return this.cancelButton;
    }
    private void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

}
