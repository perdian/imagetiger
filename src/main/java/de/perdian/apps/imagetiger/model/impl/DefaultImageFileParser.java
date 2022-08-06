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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.jfif.JfifDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

import de.perdian.apps.imagetiger.model.ImageDataKey;
import de.perdian.apps.imagetiger.model.ImageFile;
import de.perdian.apps.imagetiger.model.ImageFileParser;
import de.perdian.apps.imagetiger.model.ImageTigerConstants;

public class DefaultImageFileParser implements ImageFileParser {

    private static final Logger log = LoggerFactory.getLogger(DefaultImageFileParser.class);

    private static final Set<String> VALID_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");

    @Override
    public boolean isPotentialImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        int extensionStartIndex = fileName.lastIndexOf(".");
        String extension = extensionStartIndex > 0 ? fileName.substring(extensionStartIndex + 1) : null;
        return StringUtils.isNotEmpty(extension) && VALID_EXTENSIONS.contains(extension);
    }

    @Override
    public ImageFile parseFile(File osFile) throws IOException {
        DefaultImageFile imageFile = new DefaultImageFile(osFile);
        this.appendMetadata(imageFile, osFile);
        return imageFile;
    }

    private void appendMetadata(DefaultImageFile imageFile, File osFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(osFile);
//            for (Directory directory : metadata.getDirectories()) {
//                System.err.println(directory.toString() + " == " + directory.getClass().getName());
//                for (Tag tag : directory.getTags()) {
//                    System.err.println(" - " + tag.getTagName() + ": " + directory.getString(tag.getTagType()));
//                }
//            }
            for (ExifSubIFDDirectory exifImageDirectory : metadata.getDirectoriesOfType(ExifSubIFDDirectory.class)) {
                DirectoryToPropertyExtractor extractor = new DirectoryToPropertyExtractor(exifImageDirectory, imageFile);
                extractor.extractExifDate(ImageDataKey.DATETIME, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, ExifSubIFDDirectory.TAG_DATETIME);
                extractor.extractUtfOffset(ImageDataKey.DATETIME_ZONE, ExifSubIFDDirectory.TAG_TIME_ZONE_ORIGINAL, ExifSubIFDDirectory.TAG_TIME_ZONE);
            }
            for (FileTypeDirectory fileTypeDirectory : metadata.getDirectoriesOfType(FileTypeDirectory.class)) {
                DirectoryToPropertyExtractor extractor = new DirectoryToPropertyExtractor(fileTypeDirectory, imageFile);
                extractor.extractString(ImageDataKey.MIME_TYPE, FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE);
            }
            for (JpegDirectory jpegDirectory : metadata.getDirectoriesOfType(JpegDirectory.class)) {
                DirectoryToPropertyExtractor extractor = new DirectoryToPropertyExtractor(jpegDirectory, imageFile);
                extractor.extractString(ImageDataKey.WIDTH, JpegDirectory.TAG_IMAGE_WIDTH);
                extractor.extractString(ImageDataKey.HEIGHT, JpegDirectory.TAG_IMAGE_HEIGHT);
            }
            for (JfifDirectory jfifDirectory : metadata.getDirectoriesOfType(JfifDirectory.class)) {
                DirectoryToPropertyExtractor extractor = new DirectoryToPropertyExtractor(jfifDirectory, imageFile);
                extractor.extractString(ImageDataKey.RESOLUTION_X, JfifDirectory.TAG_RESX);
                extractor.extractString(ImageDataKey.RESOLUTION_Y, JfifDirectory.TAG_RESY);
            }
        } catch (Exception e) {
            log.debug("Cannot read image metadata from file at: {}", osFile.getAbsolutePath(), e);
        }
    }

    private static class DirectoryToPropertyExtractor {

        private static final DateTimeFormatter EXIF_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

        private Directory directory = null;
        private DefaultImageFile imageFile = null;

        DirectoryToPropertyExtractor(Directory directory, DefaultImageFile imageFile) {
            this.setDirectory(directory);
            this.setImageFile(imageFile);
        }

        @Override
        public String toString() {
            ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
            toStringBuilder.append("directory", this.getDirectory() + " (" + this.getDirectory().getClass().getName() + ")");
            toStringBuilder.append("imageFile", this.getImageFile());
            return toStringBuilder.toString();
        }

        void extractString(ImageDataKey key, int... tags) {
            for (int tag : tags) {
                String tagValue = this.getDirectory().getString(tag);
                if (StringUtils.isNotEmpty(tagValue)) {
                    this.getImageFile().resetPropertyValue(key, tagValue);
                }
            }
        }

        void extractExifDate(ImageDataKey key, int... tags) {
            for (int tag : tags) {
                try {
                    String tagValue = this.getDirectory().getString(tag);
                    if (StringUtils.isNotEmpty(tagValue)) {
                        LocalDateTime parsedDateTime = LocalDateTime.parse(tagValue, EXIF_DATETIME_FORMATTER);
                        this.getImageFile().resetPropertyValue(key, ImageTigerConstants.DATE_TIME_FORMATTER.format(parsedDateTime));
                        return;
                    }
                } catch (Exception e) {
                    log.warn("Cannot extract tag from directory targeted for: {} [{}]", key, this, e);
                }
            }
        }

        void extractUtfOffset(ImageDataKey key, int... tags) {
            for (int tag : tags) {
                String tagValue = this.getDirectory().getString(tag);
                if (StringUtils.isNotEmpty(tagValue)) {
                    ZoneOffset zoneOffset = ZoneOffset.of(tagValue);
                    this.getImageFile().resetPropertyValue(key, zoneOffset.toString());
                    return;
                }
            }
        }

        private Directory getDirectory() {
            return this.directory;
        }
        private void setDirectory(Directory directory) {
            this.directory = directory;
        }

        private DefaultImageFile getImageFile() {
            return this.imageFile;
        }
        private void setImageFile(DefaultImageFile imageFile) {
            this.imageFile = imageFile;
        }

    }

}
