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

import java.io.File;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateSettings;
import de.perdian.apps.imagetiger.fx.model.selection.Selection;
import de.perdian.apps.imagetiger.fx.support.jobs.JobExecutor;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Helper class that directly launches a {@code BatchUpdateDialogPane} without the rest of the application
 */

public class BatchUpdateDialogPaneLauncher {

    public static void main(String[] args) {
        Application.launch(BatchUpdateDialogPaneLauncherApplication.class, args);
    }

    public static class BatchUpdateDialogPaneLauncherApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {

            JobExecutor jobExecutor = new JobExecutor();
            Selection selection = new Selection(jobExecutor);
            selection.updateSelectedDirectory(new File(System.getProperty("user.home"), "Downloads/images"), true);

            BatchUpdateSettings settings = new BatchUpdateSettings();
            settings.getOriginalFileNamePattern().setValue("IMG_(?<plainName>.*)");
            settings.getNewFileName().setValue("#{counter} #{file.name}");
            settings.getNewFileExtension().setValue("#{lowercase(file.extension)}");
            settings.getNewFileDateLocalString().setValue("#{file.properties['datetime']}");
            settings.getNewFileDateLocalZone().setValue("#{file.properties['datetime_zone']}");

            BatchUpdateDialogPane primaryPane = new BatchUpdateDialogPane(settings, selection, jobExecutor);
            primaryPane.setPadding(new Insets(10, 10, 10, 10));
            primaryPane.setPrefSize(1600, 1200);

            Scene primaryScene = new Scene(primaryPane);
            primaryStage.centerOnScreen();
            primaryStage.setScene(primaryScene);
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.show();

        }

    }

}
