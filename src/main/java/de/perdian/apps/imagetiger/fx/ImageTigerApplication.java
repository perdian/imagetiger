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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ImageTigerApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(ImageTigerApplication.class);

    private ImageTigerPreferences preferences = null;

    @Override
    public void init() throws Exception {
        log.info("Initialzing application");

        log.debug("Loading preferences");
        this.setPreferences(ImageTigerPreferences.createFromUserHome());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting application UI");

        ImageTigerPane mainPane = new ImageTigerPane(this.getPreferences());
        Scene mainScene = new Scene(mainPane);
        Rectangle2D mainScreenBounds = Screen.getPrimary().getBounds();

        primaryStage.setTitle("ImageTiger");
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);
        primaryStage.setWidth(Math.min(1400, mainScreenBounds.getWidth() - 250));
        primaryStage.setHeight(Math.min(900, mainScreenBounds.getHeight() - 250));
        primaryStage.show();

        log.info("Finished starting application UI");
    }

    private ImageTigerPreferences getPreferences() {
        return this.preferences;
    }
    private void setPreferences(ImageTigerPreferences preferences) {
        this.preferences = preferences;
    }

}
