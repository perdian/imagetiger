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
package de.perdian.apps.imagetiger.fx.model.batchupdate.expression;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.imagetiger.fx.model.batchupdate.BatchUpdateItem;

public class ExpressionFile {

    private String name = null;
    private String extension = null;
    private Matcher originalFileNameMatcher = null;
    private boolean originalFileNameMatcherActive = false;
    private Map<String, String> properties = null;

    public ExpressionFile(BatchUpdateItem item, Pattern originalFileNamePattern) {
        this.setName(item.getFileNameWithoutExtension().getOriginalValue().getValue());
        this.setExtension(item.getFileExtension().getOriginalValue().getValue());
        this.setProperties(
            item.getImageFile().getProperties().entrySet().stream()
                .filter(entry -> StringUtils.isNotEmpty(entry.getValue().getOriginalValue().getValue()))
                .collect(Collectors.toMap(entry -> entry.getKey().toString().toLowerCase(), entry -> entry.getValue().getOriginalValue().getValue()))
        );

        if (originalFileNamePattern != null) {
            Matcher originalFileNameMatcher = originalFileNamePattern.matcher(item.getFileNameWithoutExtension().getOriginalValue().getValue());
            this.setOriginalFileNameMatcherActive(originalFileNameMatcher.matches());
            this.setOriginalFileNameMatcher(originalFileNameMatcher);
        }
    }

    public String getName() {
        return this.name;
    }
    private void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return this.extension;
    }
    private void setExtension(String extension) {
        this.extension = extension;
    }

    public String group(int groupIndex) {
        if (this.getOriginalFileNameMatcher() == null || !this.isOriginalFileNameMatcherActive()) {
            throw new IllegalArgumentException("Cannot find parsed group " + groupIndex);
        } else {
            return this.getOriginalFileNameMatcher().group(groupIndex);
        }
    }

    public String group(String groupName) {
        if (this.getOriginalFileNameMatcher() == null || !this.isOriginalFileNameMatcherActive()) {
            throw new IllegalArgumentException("Cannot find parsed group '" + groupName + "'");
        } else {
            return this.getOriginalFileNameMatcher().group(groupName);
        }
    }

    private Matcher getOriginalFileNameMatcher() {
        return this.originalFileNameMatcher;
    }
    private void setOriginalFileNameMatcher(Matcher originalFileNameMatcher) {
        this.originalFileNameMatcher = originalFileNameMatcher;
    }

    private boolean isOriginalFileNameMatcherActive() {
        return this.originalFileNameMatcherActive;
    }
    private void setOriginalFileNameMatcherActive(boolean originalFileNameMatcherActive) {
        this.originalFileNameMatcherActive = originalFileNameMatcherActive;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
