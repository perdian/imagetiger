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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.perdian.apps.imagetiger.fx.model.batchupdate.expression.ExpressionFile;

public class BatchUpdateItemExpressionRoot {

    private BatchUpdateContext updateContext = null;
    private ExpressionFile file = null;

    BatchUpdateItemExpressionRoot(BatchUpdateItem item, BatchUpdateContext updateContext) {
        this.setUpdateContext(updateContext);
        this.setFile(new ExpressionFile(item, updateContext.getOriginalFileNamePattern()));
    }

    public String counter() {
        return this.counter(null);
    }

    public String counter(String counterName) {
        AtomicInteger counter = (AtomicInteger)this.getUpdateContext().getObjects().computeIfAbsent("counter." + counterName, key -> new AtomicInteger());
        int counterValue = counter.incrementAndGet();
        int maxCounterCharacters = String.valueOf(this.getUpdateContext().getItems().size()).length();
        String counterFormatString = IntStream.range(0, maxCounterCharacters).mapToObj(i -> "0").collect(Collectors.joining());
        NumberFormat counterFormat = new DecimalFormat(counterFormatString);
        return counterFormat.format(counterValue);
    }

    public String lowercase(String input) {
        return input == null ? "" : input.toLowerCase();
    }

    private BatchUpdateContext getUpdateContext() {
        return this.updateContext;
    }
    private void setUpdateContext(BatchUpdateContext updateContext) {
        this.updateContext = updateContext;
    }

    public ExpressionFile getFile() {
        return this.file;
    }
    private void setFile(ExpressionFile file) {
        this.file = file;
    }

}
