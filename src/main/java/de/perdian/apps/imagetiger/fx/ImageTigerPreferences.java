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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ImageTigerPreferences {

    private static final Logger log = LoggerFactory.getLogger(ImageTigerPreferences.class);

    private Properties propertyValues = null;
    private Path propertyValuesPath = null;
    private Map<String, StringProperty> properties = null;

    public ImageTigerPreferences(Path storageDirectory) {
        Path propertyValuesFile = storageDirectory.resolve("property-values");

        Properties propertyValues = new Properties();
        if (Files.exists(propertyValuesFile)) {
            log.debug("Loading existing preferences values from file at: {}", propertyValuesFile);
            try (InputStream propertyValuesFileStream = new BufferedInputStream(Files.newInputStream(propertyValuesFile))) {
                propertyValues.loadFromXML(propertyValuesFileStream);
            } catch (Exception e) {
                log.error("Cannot read application prefences from file at: {}", propertyValuesFile, e);
            }
        }

        this.setPropertyValues(propertyValues);
        this.setPropertyValuesPath(propertyValuesFile);
        this.setProperties(new HashMap<>());
    }

    public ObjectProperty<File> createFileProperty(String propertyName, File defaultValue) {
        return this.createObjectProperty(propertyName, defaultValue, File::new, File::getAbsolutePath);
    }

    public <T> ObjectProperty<T> createObjectProperty(String propertyName, T defaultValue, Function<String, ? extends T> toObjectFunction, Function<T, String> toStringFunction) {
        String initialStringValue = this.getPropertyValues().getProperty(propertyName, defaultValue == null ? null : toStringFunction.apply(defaultValue));
        ObjectProperty<T> objectProperty = new SimpleObjectProperty<>(StringUtils.isEmpty(initialStringValue) ? null : toObjectFunction.apply(initialStringValue));
        StringProperty boundProperty = this.createProperty(propertyName, defaultValue == null ? null : toStringFunction.apply(defaultValue));
        boundProperty.addListener((o, oldValue, newValue) -> {
            T newObject = StringUtils.isEmpty(newValue) ? null : toObjectFunction.apply(newValue);
            if (!Objects.equals(newObject, objectProperty.getValue())) {
                objectProperty.setValue(newObject);
            }
        });
        objectProperty.addListener((o, oldValue, newValue) -> {
            String newString = newValue == null ? null : toStringFunction.apply(newValue);
            if (!Objects.equals(newString, boundProperty.getValue())) {
                boundProperty.setValue(newString);
            }
        });
        return objectProperty;
    }

    public synchronized StringProperty createProperty(String propertyName, String defaultValue) {
        return this.getProperties().computeIfAbsent(propertyName, newKey -> {
            String initialValue = this.getPropertyValues().getProperty(propertyName);
            if (StringUtils.isEmpty(initialValue)) {
                initialValue = defaultValue;
                this.updatePropertyValue(propertyName, defaultValue);
            }
            StringProperty newProperty = new SimpleStringProperty(initialValue);
            newProperty.addListener((o, oldValue, newValue) -> this.updatePropertyValue(propertyName, newValue));
            return newProperty;
        });
    }

    private synchronized void updatePropertyValue(String propertyName, String propertyValue) {
        String existingPropertyValue = this.getPropertyValues().getProperty(propertyName);
        if (!Objects.equals(propertyValue, existingPropertyValue)) {
            if (StringUtils.isEmpty(propertyValue)) {
                this.getPropertyValues().remove(propertyName);
            } else {
                this.getPropertyValues().setProperty(propertyName, propertyValue);
            }
            log.debug("Updating preferences values into file at: {}", this.getPropertyValuesPath());
            try (OutputStream targetStream = new BufferedOutputStream(Files.newOutputStream(this.getPropertyValuesPath()))) {
                this.getPropertyValues().storeToXML(targetStream, "Preferences");
                targetStream.flush();
            } catch (Exception e) {
                log.error("Cannot write application prefences into file at: {}", this.getPropertyValuesPath(), e);
            }
        }
    }

    private Properties getPropertyValues() {
        return this.propertyValues;
    }
    private void setPropertyValues(Properties propertyValues) {
        this.propertyValues = propertyValues;
    }

    private Path getPropertyValuesPath() {
        return this.propertyValuesPath;
    }
    private void setPropertyValuesPath(Path propertyValuesPath) {
        this.propertyValuesPath = propertyValuesPath;
    }

    private Map<String, StringProperty> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<String, StringProperty> properties) {
        this.properties = properties;
    }

}
