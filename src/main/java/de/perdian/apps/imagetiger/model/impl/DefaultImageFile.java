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

import de.perdian.apps.imagetiger.model.ImageFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

class DefaultImageFile implements ImageFile {

    private File osFile = null;
    private BooleanProperty primary = null;
    private BooleanProperty dirty = null;

    DefaultImageFile(File osFile) {
        this.setOsFile(osFile);
        this.setPrimary(new SimpleBooleanProperty(false));
        this.setDirty(new SimpleBooleanProperty(false));
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

}
