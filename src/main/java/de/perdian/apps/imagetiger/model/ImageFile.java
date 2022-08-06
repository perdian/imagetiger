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
package de.perdian.apps.imagetiger.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import javafx.beans.property.ReadOnlyBooleanProperty;

public interface ImageFile {

    BufferedImage loadBufferedImage() throws Exception;

    ReadOnlyBooleanProperty getDirty();

    ImageDataProperty<String> getFileName();
    ImageDataProperty<String> getFileNameWithoutExtension();
    ImageDataProperty<String> getFileExtension();
    ImageDataProperty<Instant> getFileDate();
    Map<ImageDataKey, ImageDataProperty<String>> getProperties();

    boolean updateOsFile() throws IOException;

}
