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

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.imagetiger.model.ImageDataKey;
import de.perdian.apps.imagetiger.model.ImageFile;
import de.perdian.apps.imagetiger.model.ImageTigerConstants;
import de.perdian.apps.imagetiger.model.support.ChangeTrackingProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;

class DefaultImageFile implements ImageFile {

    private static final Logger log = LoggerFactory.getLogger(DefaultImageFile.class);

    private File osFile = null;
    private ReadOnlyBooleanProperty dirty = null;
    private ChangeTrackingProperty<String> fileName = null;
    private ChangeTrackingProperty<String> fileNameWithoutExtension = null;
    private ChangeTrackingProperty<String> fileExtension = null;
    private ChangeTrackingProperty<Instant> fileDate = null;
    private ChangeTrackingProperty<String> fileDateLocalString = null;
    private ChangeTrackingProperty<String> fileDateLocalZone = null;
    private Map<ImageDataKey, ChangeTrackingProperty<String>> properties = null;

    DefaultImageFile(File osFile) throws IOException {

        String fileName = osFile.getName();
        int fileExtensionSeparatorIndex = fileName.lastIndexOf(".");
        String fileNameWithoutExtension = fileExtensionSeparatorIndex < 0 ? "" : fileName.substring(0, fileExtensionSeparatorIndex);
        String fileExtension = fileExtensionSeparatorIndex < 0 ? "" : fileName.substring(fileExtensionSeparatorIndex + 1);

        ChangeTrackingProperty<String> fileNameWithoutExtensionProperty = new ChangeTrackingProperty<>(fileNameWithoutExtension);
        ChangeTrackingProperty<String> fileExtensionProperty = new ChangeTrackingProperty<>(fileExtension);
        ChangeTrackingProperty<String> fileNameProperty = new ChangeTrackingProperty<>(fileName);
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

        Instant fileDate = Files.getLastModifiedTime(osFile.toPath()).toInstant();
        ZoneId fileDateZoneId = ZoneId.systemDefault();
        LocalDateTime fileDateLocal = fileDate.atZone(fileDateZoneId).toLocalDateTime();

        ChangeTrackingProperty<Instant> fileDateProperty = new ChangeTrackingProperty<>(fileDate);
        ChangeTrackingProperty<String> fileDateLocalZone = new ChangeTrackingProperty<>(fileDateZoneId.toString());
        ChangeTrackingProperty<String> fileDateLocalAsStringProperty = new ChangeTrackingProperty<>(ImageTigerConstants.DATE_TIME_FORMATTER.format(fileDateLocal));
        fileDateLocalAsStringProperty.getNewValue().addListener((o, oldValue, newValue) -> {
            try {
                ZoneId newZoneId = ZoneId.of(fileDateLocalZone.getNewValue().getValue());
                LocalDateTime newFileDateLocal = LocalDateTime.parse(newValue, ImageTigerConstants.DATE_TIME_FORMATTER);
                Instant newFileDate = newFileDateLocal.atZone(newZoneId).toInstant();
                fileDateProperty.getNewValue().setValue(newFileDate);
            } catch (Exception e) {
                log.debug("Invalid new local date: {}", newValue, e);
            }
        });
        fileDateLocalZone.getNewValue().addListener((o, oldValue, newValue) -> {
            try {
                ZoneId newZoneId = ZoneId.of(newValue);
                LocalDateTime newFileDateLocal = LocalDateTime.parse(fileDateLocalAsStringProperty.getNewValue().getValue(), ImageTigerConstants.DATE_TIME_FORMATTER);
                Instant newFileDate = newFileDateLocal.atZone(newZoneId).toInstant();
                fileDateProperty.getNewValue().setValue(newFileDate);
            } catch (Exception e) {
                log.debug("Invalid new local date zone: {}", newValue, e);
            }
        });
        fileDateProperty.getOriginalValue().addListener((o, oldValue, newValue) -> fileDateLocalAsStringProperty.resetValue(ImageTigerConstants.DATE_TIME_FORMATTER.format(newValue.atZone(ZoneId.of(fileDateLocalZone.getNewValue().getValue())))));

        this.setOsFile(osFile);
        this.setFileName(fileNameProperty);
        this.setFileNameWithoutExtension(fileNameWithoutExtensionProperty);
        this.setFileExtension(fileExtensionProperty);
        this.setFileDate(fileDateProperty);
        this.setFileDateLocalString(fileDateLocalAsStringProperty);
        this.setFileDateLocalZone(fileDateLocalZone);

        Map<ImageDataKey, ChangeTrackingProperty<String>> properties = Arrays.stream(ImageDataKey.values())
            .collect(Collectors.toMap(key -> key, key -> new ChangeTrackingProperty<>(null)));
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
        Instant osFileDate = Files.getLastModifiedTime(osFile.toPath()).toInstant();
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
            this.setOsFile(newOsFile);
        }
        Instant newFileDate = this.getFileDate().getNewValue().getValue();
        if (!newFileDate.equals(osFileDate)) {
            osFile.setLastModified(newFileDate.toEpochMilli());
            fileUpdated = true;
            this.getFileDate().resetValue(newFileDate);
        }
        this.getFileDate().resetValue(newFileDate);
        return fileUpdated;
    }

    @Override
    public synchronized BufferedImage loadBufferedImage() throws Exception {
        return ImageIO.read(this.getOsFile());
    }

    @Override
    public void openInNativeViewer() {
        try {
            Desktop.getDesktop().open(this.getOsFile());
        } catch (Exception e) {
            log.warn("Cannot open file in native viewer: {}", this.getOsFile(), e);
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
    public ChangeTrackingProperty<String> getFileName() {
        return this.fileName;
    }
    public void setFileName(ChangeTrackingProperty<String> fileName) {
        this.fileName = fileName;
    }

    @Override
    public ChangeTrackingProperty<String> getFileNameWithoutExtension() {
        return this.fileNameWithoutExtension;
    }
    private void setFileNameWithoutExtension(ChangeTrackingProperty<String> fileNameWithoutExtension) {
        this.fileNameWithoutExtension = fileNameWithoutExtension;
    }

    @Override
    public ChangeTrackingProperty<String> getFileExtension() {
        return this.fileExtension;
    }
    private void setFileExtension(ChangeTrackingProperty<String> fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public ChangeTrackingProperty<Instant> getFileDate() {
        return this.fileDate;
    }
    private void setFileDate(ChangeTrackingProperty<Instant> fileDate) {
        this.fileDate = fileDate;
    }

    @Override
    public ChangeTrackingProperty<String> getFileDateLocalString() {
        return this.fileDateLocalString;
    }
    private void setFileDateLocalString(ChangeTrackingProperty<String> fileDateLocalString) {
        this.fileDateLocalString = fileDateLocalString;
    }

    @Override
    public ChangeTrackingProperty<String> getFileDateLocalZone() {
        return this.fileDateLocalZone;
    }
    private void setFileDateLocalZone(ChangeTrackingProperty<String> fileDateLocalZone) {
        this.fileDateLocalZone = fileDateLocalZone;
    }

    void resetPropertyValue(ImageDataKey key, String value) {
        this.getProperties().get(key).resetValue(value);
    }

    @Override
    public ChangeTrackingProperty<String> getProperty(ImageDataKey key) {
        return this.getProperties().get(key);
    }

    @Override
    public Map<ImageDataKey, ChangeTrackingProperty<String>> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<ImageDataKey, ChangeTrackingProperty<String>> properties) {
        this.properties = properties;
    }

}
