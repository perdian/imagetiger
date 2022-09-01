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
package de.perdian.apps.imagetiger.fx.panes.selection.list;

import de.perdian.apps.imagetiger.fx.model.selection.Selection;
import javafx.scene.layout.BorderPane;

public class ListPane extends BorderPane {

    public ListPane(Selection selection) {
        ListTableView listTableView = new ListTableView(selection);
        listTableView.setFocusTraversable(false);
        this.setCenter(listTableView);
    }

}
