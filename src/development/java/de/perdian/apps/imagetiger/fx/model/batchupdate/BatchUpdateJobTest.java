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
package de.perdian.apps.imagetiger.fx.model.batchupdate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.imagetiger.fx.support.jobs.JobContext;
import de.perdian.apps.imagetiger.model.ImageDataKey;
import de.perdian.apps.imagetiger.model.ImageFile;
import de.perdian.apps.imagetiger.model.ImageFileParser;
import de.perdian.apps.imagetiger.model.impl.DefaultImageFileParser;
import de.perdian.apps.imagetiger.model.support.ChangeTrackingProperty;
import javafx.application.Platform;

public class BatchUpdateJobTest {

    private static final Logger log = LoggerFactory.getLogger(BatchUpdateJobTest.class);

    static {
        Platform.startup(() -> {});
    }

    public static void main(String[] args) {
        try {

            List<ImageFile> imageFiles = BatchUpdateJobTest.loadImageFiles();
            log.info("Processing {} image files", imageFiles.size());

            BatchUpdateSettings updateSettings = new BatchUpdateSettings();
            updateSettings.getOriginalFileNamePattern().setValue("IMG_(?<plainName>.*)");
            updateSettings.getNewFileName().setValue("#{counter} #{file.name}");
            updateSettings.getNewFileExtension().setValue("#{lowercase(file.extension)}");
            updateSettings.getNewFileDateLocalString().setValue("#{file.properties['datetime']}");
            updateSettings.getNewFileDateLocalZone().setValue("#{file.properties['datetime_zone']}");

            List<BatchUpdateItem> updateItems = imageFiles.stream().map(BatchUpdateItem::new).toList();
            BatchUpdateJob updateJob = new BatchUpdateJob(updateItems, updateSettings);
            updateJob.execute(new JobContextImpl());

            System.err.println("\nSettings FileDateLocalString: " + updateSettings.getNewFileDateLocalString().getValue());
            System.err.println("Settings FileDateLocalZone:   " + updateSettings.getNewFileDateLocalZone().getValue());
            System.err.println("Settings FileExtension:       " + updateSettings.getNewFileExtension().getValue());
            System.err.println("Settings FileName:            " + updateSettings.getNewFileName().getValue());

            for (int i=0; i < updateItems.size(); i++) {
                BatchUpdateItem updateItem = updateItems.get(i);

                String oldFileName = updateItem.getFileName().getOriginalValue().getValue();
                String newFileName = updateItem.getFileNameWithoutExtension().getNewValue().getValue() + "." + updateItem.getFileExtension().getNewValue().getValue();
                int fileNameLength = Math.max(oldFileName.length(), newFileName.length());

                System.err.append("\n[").append(String.valueOf(i+1)).append("/").append(String.valueOf(updateItems.size())).append("] ");
                System.err.append("   ").append(StringUtils.rightPad(oldFileName, fileNameLength));
                System.err.append("  @  ").append(updateItem.getFileDateLocalString().getOriginalValue().getValue());
                System.err.append(" [").append(updateItem.getFileDateLocalZone().getOriginalValue().getValue()).append("]");
                System.err.append("\n[").append(String.valueOf(i+1)).append("/").append(String.valueOf(updateItems.size())).append("] ");
                System.err.append("-> ").append(StringUtils.rightPad(newFileName, fileNameLength));
                System.err.append("  @  ").append(updateItem.getFileDateLocalString().getNewValue().getValue());
                System.err.append(" [").append(updateItem.getFileDateLocalZone().getNewValue().getValue()).append("]");
                System.err.append("\n");

                for (Map.Entry<ImageDataKey, ChangeTrackingProperty<String>> property : updateItem.getImageFile().getProperties().entrySet()) {
                    System.err.println("PROP " + property.getKey() + " = " + property.getValue().getOriginalValue().getValue());
                }

                System.err.flush();

            }

        } catch (Throwable e) {
            log.error("Cannot execute batch update", e);
        } finally {
            System.exit(0);
        }
    }

    private static List<ImageFile> loadImageFiles() {
        File sourceDirectory = new File(System.getProperty("user.home"), "Downloads/images");
        try {
            ImageFileParser imageFileParser = new DefaultImageFileParser();
            File[] sourceFiles = sourceDirectory.listFiles();
            List<ImageFile> imageFiles = new ArrayList<>();
            for (File sourceFile : sourceFiles) {
                if (imageFileParser.isPotentialImageFile(sourceFile)) {
                    imageFiles.add(imageFileParser.parseFile(sourceFile));
                }
            }
            return imageFiles;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load image files from directory: " + sourceDirectory, e);
        }
    }

    private static final class JobContextImpl implements JobContext {

        @Override
        public void updateProgress(String message) {
            this.updateProgress(message, null, null);
        }

        @Override
        public void updateProgress(String message, Integer step, Integer totalSteps) {
            log.info(message);
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

    }

}
