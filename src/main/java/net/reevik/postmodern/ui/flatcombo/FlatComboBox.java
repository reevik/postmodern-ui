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
package net.reevik.postmodern.ui.flatcombo;

import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.VERTICAL;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class FlatComboBox extends JPanel {
  private static final int HEIGHT = 22;
  private boolean focus;
  private boolean active;
  private String selection;
  private final JLabel selectedLabel = new JLabel("");
  private final Configuration configuration;
  private List<SelectionListener> listeners = new ArrayList<>();

  public FlatComboBox(Configuration config) {
    configuration = config;
    selection = config.items().getFirst();

    setPreferredSize(new Dimension(configuration.width(), HEIGHT));
    setBackground(configuration.inactive());
    setOpaque(false);
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));

    var popupmenu = new JPopupMenu("Edit");
    popupmenu.setBackground(configuration.active());
    configuration.items().stream()
        .map(this::createMenuItem)
        .forEach(popupmenu::add);
    popupmenu.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        FlatComboBox.this.repaint();
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });

    var gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.weightx = 0.9;
    gridBagConstraints.anchor = WEST;

    selectedLabel.setText(selection);
    selectedLabel.setForeground(configuration.label());
    selectedLabel.setFont(selectedLabel.getFont().deriveFont(11f));
    add(selectedLabel, gridBagConstraints);

    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.anchor = EAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.fill = VERTICAL;

    var arrowButton = new JLabel("  ▾");
    arrowButton.setForeground(configuration.label());
    arrowButton.setSize(32, HEIGHT);
    arrowButton.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));

    add(arrowButton, gridBagConstraints);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        focus = true;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        repaint();
        super.mouseMoved(e);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        focus = false;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
        super.mouseExited(e);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (!active) {
          active = true;
          popupmenu.show(selectedLabel, -10, selectedLabel.getY() + selectedLabel.getHeight() + 2);
        } else {
          active = false;
          popupmenu.setVisible(false);
        }

        super.mousePressed(e);
      }
    });
  }

  private JMenuItem createMenuItem(String label) {
    var menuItem = new JMenuItem(label);
    menuItem.setIcon(null);
    menuItem.setIconTextGap(0);
    menuItem.setMargin(new Insets(0, -6, 0, 0));
    menuItem.setFont(menuItem.getFont().deriveFont(11f));
    menuItem.addActionListener(e -> {
      selection = e.getActionCommand();
      selectedLabel.setText(selection);
      configuration.action().accept(selection);
      FlatComboBox.this.repaint();
      FlatComboBox.this.active = false;
    });
    return menuItem;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(!focus && !active ? configuration.inactive() : configuration.active());
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
    g2.dispose();
  }

  @Override
  public void updateUI() {
    super.updateUI();
    setForeground(Color.BLACK);
    setBackground(Color.WHITE);
  }

  public void select(String selection) {
    this.selectedLabel.setText(selection);
    this.selection = selection;
  }

  public void addListener(SelectionListener selectionListener) {
    if (selectionListener == null) {
      return;
    }
    listeners.add(selectionListener);
  }

  public static interface SelectionListener {
    void onSelected();
  }

  public record Configuration(List<String> items,
                              Consumer<String> action,
                              Color inactive,
                              Color active,
                              Color label,
                              int width,
                              int fontSize) {
    public void add(String item) {
      items.add(item);
    }
  }
}
