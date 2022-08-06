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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.imagetiger.model.ImageDataKey;
import de.perdian.apps.imagetiger.model.ImageDataProperty;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;

class DefaultImageFile implements ImageFile {

    private File osFile = null;
    private ReadOnlyBooleanProperty dirty = null;
    private BufferedImage cachedBufferedImage = null;
    private Exception cachedBufferedImageException = null;
    private ImageDataProperty<String> fileName = null;
    private ImageDataProperty<String> fileNameWithoutExtension = null;
    private ImageDataProperty<String> fileExtension = null;
    private ImageDataProperty<Instant> fileDate = null;
    private Map<ImageDataKey, ImageDataProperty<String>> properties = null;

    DefaultImageFile(File osFile) {

        String fileName = osFile.getName();
        int fileExtensionSeparatorIndex = fileName.lastIndexOf(".");
        String fileNameWithoutExtension = fileExtensionSeparatorIndex < 0 ? "" : fileName.substring(0, fileExtensionSeparatorIndex);
        String fileExtension = fileExtensionSeparatorIndex < 0 ? "" : fileName.substring(fileExtensionSeparatorIndex + 1);

        ImageDataProperty<String> fileNameWithoutExtensionProperty = new ImageDataProperty<>(fileNameWithoutExtension, false);
        ImageDataProperty<String> fileExtensionProperty = new ImageDataProperty<>(fileExtension, false);
        ImageDataProperty<String> fileNameProperty = new ImageDataProperty<>(fileName, false);
        ChangeListener<String> fileNameChangeListener = (o, oldValue, newValue) -> {
            StringBuilder newFileName = new StringBuilder();
            newFileName.append(fileNameWithoutExtensionProperty.getNewValue().getValue());
            if (StringUtils.isNotEmpty(fileExtensionProperty.getNewValue().getValue())) {
                newFileName.append(".").append(fileExtensionProperty.getNewValue().getValue());
            }
            fileNameProperty.getNewValue().setValue(newFileName.toString());
        };
        fileNameWithoutExtensionProperty.getNewValue().addListener(fileNameChangeListener);
        fileExtensionProperty.getNewValue().addListener(fileNameChangeListener);

        this.setOsFile(osFile);
        this.setFileName(fileNameProperty);
        this.setFileNameWithoutExtension(fileNameWithoutExtensionProperty);
        this.setFileExtension(fileExtensionProperty);
        this.setFileDate(new ImageDataProperty<>(Instant.ofEpochMilli(osFile.lastModified()), false));

        Map<ImageDataKey, ImageDataProperty<String>> properties = Arrays.stream(ImageDataKey.values())
            .collect(Collectors.toMap(key -> key, key -> new ImageDataProperty<>(null, true)));
        this.setProperties(properties);

        BooleanProperty dirtyProperty = new SimpleBooleanProperty();
        List<ObservableBooleanValue> dirtyProviders = new ArrayList<>();
        dirtyProviders.add(this.getFileNameWithoutExtension().getDirty());
        dirtyProviders.add(this.getFileExtension().getDirty());
        dirtyProviders.add(this.getFileDate().getDirty());
        properties.forEach((key, value) -> dirtyProviders.add(value.getDirty()));
        this.setDirty(dirtyProperty);

        dirtyProviders.forEach(dirtyProvider -> {
            dirtyProvider.addListener((o, oldValue, newValue) -> {
                boolean newDirtyState = newValue;
                for (ObservableBooleanValue otherDirtyProvider : dirtyProviders) {
                    if (otherDirtyProvider != dirtyProvider) {
                        newDirtyState = newDirtyState || dirtyProvider.get();
                    }
                }
                if (!Objects.equals(dirtyProperty.get(), newDirtyState)) {
                    dirtyProperty.setValue(newDirtyState);
                }
            });
        });

    }

    @Override
    public synchronized boolean updateOsFile() throws IOException {
        boolean fileUpdated = false;
        File osFile = this.getOsFile();
        if (this.getFileName().getDirty().get()) {
            String newFileName = this.getFileName().getNewValue().getValue();
            int newFileExtensionSeparatorIndex = newFileName.lastIndexOf(".");
            String newFileNameWithoutExtension = newFileExtensionSeparatorIndex < 0 ? "" : newFileName.substring(0, newFileExtensionSeparatorIndex);
            String newFileExtension = newFileExtensionSeparatorIndex < 0 ? "" : newFileName.substring(newFileExtensionSeparatorIndex + 1);
            File newOsFile = new File(osFile.getParentFile(), newFileName);
            osFile.renameTo(newOsFile);
            this.getFileNameWithoutExtension().resetValue(newFileNameWithoutExtension);
            this.getFileExtension().resetValue(newFileExtension);
            this.getFileName().resetValue(newFileName);
            fileUpdated = true;
        }
        Instant newFileDate = this.getFileDate().getNewValue().getValue();
        if (newFileDate.toEpochMilli() != osFile.lastModified()) {
            osFile.setLastModified(newFileDate.toEpochMilli());
            fileUpdated = true;
        }
        this.getFileDate().resetValue(newFileDate);
        return fileUpdated;
    }

    @Override
    public synchronized BufferedImage loadBufferedImage() throws Exception {
        if (this.cachedBufferedImageException != null) {
            throw this.cachedBufferedImageException;
        } else if (this.cachedBufferedImage != null) {
            return this.cachedBufferedImage;
        } else {
            try {
                return this.cachedBufferedImage = ImageIO.read(this.getOsFile());
            } catch (Exception e) {
                this.cachedBufferedImageException = e;
                throw e;
            }
        }
    }

    @Override
    public String toString() {
        return this.getOsFile().getName();
    }

    private File getOsFile() {
        return this.osFile;
    }
    private void setOsFile(File osFile) {
        this.osFile = osFile;
    }

    @Override
    public ReadOnlyBooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(ReadOnlyBooleanProperty dirty) {
        this.dirty = dirty;
    }

    @Override
    public ImageDataProperty<String> getFileName() {
        return this.fileName;
    }
    public void setFileName(ImageDataProperty<String> fileName) {
        this.fileName = fileName;
    }

    @Override
    public ImageDataProperty<String> getFileNameWithoutExtension() {
        return this.fileNameWithoutExtension;
    }
    private void setFileNameWithoutExtension(ImageDataProperty<String> fileNameWithoutExtension) {
        this.fileNameWithoutExtension = fileNameWithoutExtension;
    }

    @Override
    public ImageDataProperty<String> getFileExtension() {
        return this.fileExtension;
    }
    private void setFileExtension(ImageDataProperty<String> fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public ImageDataProperty<Instant> getFileDate() {
        return this.fileDate;
    }
    private void setFileDate(ImageDataProperty<Instant> fileDate) {
        this.fileDate = fileDate;
    }

    void resetPropertyValue(ImageDataKey key, String value) {
        this.getProperties().get(key).resetValue(value);
    }

    @Override
    public Map<ImageDataKey, ImageDataProperty<String>> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<ImageDataKey, ImageDataProperty<String>> properties) {
        this.properties = properties;
    }

}
