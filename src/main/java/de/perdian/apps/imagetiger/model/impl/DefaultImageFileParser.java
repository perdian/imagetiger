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
package de.perdian.apps.imagetiger.model.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;

import de.perdian.apps.imagetiger.model.ImageFile;
import de.perdian.apps.imagetiger.model.ImageFileParser;

public class DefaultImageFileParser implements ImageFileParser {

    private static final Logger log = LoggerFactory.getLogger(DefaultImageFileParser.class);

    @Override
    public boolean isPotentialImageFile(File file) {
        return true;
    }

    @Override
    public ImageFile parseFile(File osFile) {
        DefaultImageFile imageFile = new DefaultImageFile(osFile);
        this.appendMetadata(imageFile, osFile);
        return imageFile;
    }

    private void appendMetadata(DefaultImageFile imageFile, File osFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(osFile);
            System.err.println(metadata);
        } catch (Exception e) {
            log.debug("Cannot read image metadata from file at: {}", osFile.getAbsolutePath(), e);
        }
    }

}
