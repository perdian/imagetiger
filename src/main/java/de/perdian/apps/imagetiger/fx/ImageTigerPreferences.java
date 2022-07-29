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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageTigerPreferences {

    private static final Logger log = LoggerFactory.getLogger(ImageTigerPreferences.class);

    private Path storageDirectory = null;

    public ImageTigerPreferences(Path storageDirectory) {
        this.setStorageDirectory(storageDirectory);
    }

    static ImageTigerPreferences createFromUserHome() throws IOException {
        Path userHomePath = Path.of(System.getProperty("user.home"), ".imagetiger");
        if (!Files.exists(userHomePath)) {
            log.debug("Creating new preferences directory in user home: {}", userHomePath);
            Files.createDirectories(userHomePath);
        } else {
            log.debug("Using preferences directory in user home: {}", userHomePath);
        }
        return new ImageTigerPreferences(userHomePath);
    }

    private Path getStorageDirectory() {
        return this.storageDirectory;
    }
    private void setStorageDirectory(Path storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

}
