/*
 * Copyright (c) 2024 Erhan Bagdemir. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reevik.swing.layout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;

public class GridBuilder {

    private final JComponent container;
    private final GridBagConstraints constraints;

    public GridBuilder(JComponent container) {
        this.container = container;
        this.container.setLayout(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        this.constraints.gridx = 0;
        this.constraints.gridy = 0;
    }

    public GridBuilder next() {
        this.constraints.gridx += 1;
        return this;
    }

    public GridBuilder weightX(double weightx) {
        this.constraints.weightx = weightx;
        return this;
    }

    public GridBuilder weightY(double weighty) {
        this.constraints.weighty = weighty;
        return this;
    }

    public GridBuilder below() {
        this.constraints.gridy += 1;
        return this;
    }

    public GridBuilder fill(int direction) {
        this.constraints.fill = direction;
        return this;
    }

    public GridBuilder fix(int position) {
        this.constraints.anchor = position;
        return this;
    }

    public GridBuilder insets(int top, int left, int bottom, int right) {
        this.constraints.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public GridBuilder add(JComponent component) {
        this.container.add(component, this.constraints);
        return this;
    }
}
