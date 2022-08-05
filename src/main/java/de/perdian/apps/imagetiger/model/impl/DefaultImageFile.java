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

import javax.imageio.ImageIO;

import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class DefaultImageFile implements ImageFile {

    private File osFile = null;
    private BooleanProperty primary = null;
    private BooleanProperty dirty = null;
    private StringProperty fileName = null;
    private BufferedImage cachedBufferedImage = null;
    private Exception cachedBufferedImageException = null;

    DefaultImageFile(File osFile) {
        this.setOsFile(osFile);
        this.setPrimary(new SimpleBooleanProperty(false));
        this.setDirty(new SimpleBooleanProperty(false));
        this.setFileName(new SimpleStringProperty(osFile.getName()));
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
    public BooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(BooleanProperty dirty) {
        this.dirty = dirty;
    }

    @Override
    public StringProperty getFileName() {
        return this.fileName;
    }
    private void setFileName(StringProperty fileName) {
        this.fileName = fileName;
    }

}
