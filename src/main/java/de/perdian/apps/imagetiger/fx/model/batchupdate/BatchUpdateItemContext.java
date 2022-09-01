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

class BatchUpdateItemContext {

    private BatchUpdateContext updateContext = null;
    private BatchUpdateItem item = null;

    BatchUpdateItemContext(BatchUpdateContext updateContext, BatchUpdateItem item) {
        this.setUpdateContext(updateContext);
        this.setItem(item);
    }

    String evaluate(String expression) {
        return "foo";
    }

    private BatchUpdateContext getUpdateContext() {
        return this.updateContext;
    }
    private void setUpdateContext(BatchUpdateContext updateContext) {
        this.updateContext = updateContext;
    }

    private BatchUpdateItem getItem() {
        return this.item;
    }
    private void setItem(BatchUpdateItem item) {
        this.item = item;
    }

}
