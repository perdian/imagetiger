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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

class BatchUpdateContext {

    private List<BatchUpdateItem> items = null;
    private Map<String, Object> objects = null;
    private Pattern originalFileNamePattern = null;

    BatchUpdateContext(List<BatchUpdateItem> items, BatchUpdateSettings settings) {
        this.setItems(items);
        this.setObjects(new HashMap<>());
        this.setOriginalFileNamePattern(StringUtils.isEmpty(settings.getOriginalFileNamePattern().getValue()) ? null : Pattern.compile(settings.getOriginalFileNamePattern().getValue()));
    }

    BatchUpdateItemContext createItemContext(BatchUpdateItem item) {
        return new BatchUpdateItemContext(this, item);
    }

    List<BatchUpdateItem> getItems() {
        return this.items;
    }
    private void setItems(List<BatchUpdateItem> items) {
        this.items = items;
    }

    Map<String, Object> getObjects() {
        return this.objects;
    }
    private void setObjects(Map<String, Object> objects) {
        this.objects = objects;
    }

    Pattern getOriginalFileNamePattern() {
        return this.originalFileNamePattern;
    }
    private void setOriginalFileNamePattern(Pattern originalFileNamePattern) {
        this.originalFileNamePattern = originalFileNamePattern;
    }

}
