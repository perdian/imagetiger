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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import de.perdian.apps.imagetiger.model.ImageDataKey;
import de.perdian.apps.imagetiger.model.ImageDataProperty;
import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

class DefaultImageFile implements ImageFile {

    private File osFile = null;
    private BooleanProperty primary = null;
    private ReadOnlyBooleanProperty dirty = null;
    private BufferedImage cachedBufferedImage = null;
    private Exception cachedBufferedImageException = null;
    private ImageDataProperty<String> fileName = null;
    private ImageDataProperty<Instant> fileDate = null;
    private Map<ImageDataKey, ImageDataProperty<String>> properties = null;

    DefaultImageFile(File osFile) {

        this.setOsFile(osFile);
        this.setPrimary(new SimpleBooleanProperty(false));
        this.setFileName(new ImageDataProperty<>(osFile.getName()));
        this.setFileDate(new ImageDataProperty<>(Instant.ofEpochMilli(osFile.lastModified())));
        this.setProperties(new HashMap<>());

        BooleanProperty dirtyProperty = new SimpleBooleanProperty();
        List<BooleanBinding> dirtyProviders = new ArrayList<>();
        dirtyProviders.add(this.getFileName().getDirty());
        dirtyProviders.add(this.getFileDate().getDirty());
        this.getProperties().forEach((key, value) -> dirtyProviders.add(value.getDirty()));
        this.setDirty(dirtyProperty);

        dirtyProviders.forEach(dirtyProvider -> {
            dirtyProvider.addListener((o, oldValue, newValue) -> {
                boolean newDirtyState = newValue;
                for (BooleanBinding otherDirtyProvider : dirtyProviders) {
                    if (otherDirtyProvider != dirtyProvider) {
                        newDirtyState = newDirtyState || dirtyProvider.get();
                    }
                }
                if (Objects.equals(dirtyProperty.get(), newDirtyState)) {
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
            File newOsFile = new File(osFile.getParentFile(), this.getFileName().getNewValue().getValue());
            osFile.renameTo(newOsFile);
            this.getFileName().getSavedValue().setValue(this.getFileName().getNewValue().getValue());
            fileUpdated = true;
        }
        Instant newFileDate = this.getFileDate().getNewValue().getValue();
        if (newFileDate.toEpochMilli() != osFile.lastModified()) {
            osFile.setLastModified(newFileDate.toEpochMilli());
            fileUpdated = true;
        }
        this.getFileDate().getSavedValue().setValue(newFileDate);
        this.getFileDate().getNewValue().setValue(newFileDate);
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
    public BooleanProperty getPrimary() {
        return this.primary;
    }
    private void setPrimary(BooleanProperty primary) {
        this.primary = primary;
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
    private void setFileName(ImageDataProperty<String> fileName) {
        this.fileName = fileName;
    }

    @Override
    public ImageDataProperty<Instant> getFileDate() {
        return this.fileDate;
    }
    private void setFileDate(ImageDataProperty<Instant> fileDate) {
        this.fileDate = fileDate;
    }

    @Override
    public Map<ImageDataKey, ImageDataProperty<String>> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<ImageDataKey, ImageDataProperty<String>> properties) {
        this.properties = properties;
    }

}
