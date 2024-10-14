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
package net.reevik.swing.components;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputAdapter;

public class JAdvancedInputField extends JComponent {

  public static final int OFFSET_AFTER_BUTTON = 66;
  private final List<Object> content;
  private int cursorX = 0;
  private final int cursorY = 16;
  private int cursorsOffset = 0;
  private final Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");

  public JAdvancedInputField() {

    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        super.focusGained(e);
        cursorsOffset = content.size();
        repaint();
      }

      @Override
      public void focusLost(FocusEvent e) {
        super.focusLost(e);
        repaint();
      }
    });

    content = new ArrayList<>();
    initPopupMenu();
    setFocusable(true);
    setBackground(Color.RED);
    setBorder(BorderFactory.createLineBorder(Color.BLACK));
    addMouseListener(new MouseInputAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        super.mouseClicked(e);
        grabFocus();
        repaint();
      }
    });
    addKeyListener(new KeyAdapter() {

      private static boolean isPrintableChar(char c) {
        return !Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED;
      }

      @Override
      public void keyPressed(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          if (cursorsOffset > 0) {
            if (content.get(cursorsOffset - 1) instanceof String s) {
              cursorX -= getFontMetrics(getFont()).stringWidth(s);
            } else {
              cursorX -= OFFSET_AFTER_BUTTON;
            }
            cursorsOffset--;
            repaint();
          }
          return;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          if (cursorsOffset < content.size()) {
            cursorsOffset++;
            if (content.get(cursorsOffset - 1) instanceof String s) {
              cursorX += getFontMetrics(getFont()).stringWidth(s);
            } else {
              cursorX += OFFSET_AFTER_BUTTON;
            }
            repaint();
          }
          return;
        }

        if (isBackspace(e) && cursorsOffset > 0) {
          if (content.get(cursorsOffset - 1) instanceof String s) {
            cursorX -= getFontMetrics(getFont()).stringWidth(s);
          } else {
            remove(((JButton) content.get(cursorsOffset - 1)));
            cursorX -= OFFSET_AFTER_BUTTON;
          }
          content.remove(cursorsOffset - 1);
          cursorsOffset--;
          repaint();
          return;
        }

        if (!isPrintableChar(keyChar)) {
          return;
        }

        addText(String.valueOf(keyChar));
        cursorsOffset++;
        repaint();
      }
    });
  }

  private void initPopupMenu() {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem("New UUID Generator");
    menuItem.addActionListener(e -> {
      addButton("UUID");
      cursorsOffset++;
      requestFocusInWindow();
      repaint();
    });
    popupMenu.add(menuItem);
    setComponentPopupMenu(popupMenu);
  }

  private boolean isBackspace(KeyEvent e) {
    return e.getKeyCode() == 8;
  }

  private void addText(String text) {
    content.add(cursorsOffset, text);
    cursorX += getFontMetrics(getFont()).stringWidth(text);
  }

  private void addButton(String label) {
    var button = new JButton(label);
    button.setBounds(cursorX + 3, cursorY - 14, 60, 18);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setFont(button.getFont());
    button.setBackground(new Color(63, 100, 139, 255));
    content.add(cursorsOffset, button);
    add(button);
    cursorX += OFFSET_AFTER_BUTTON;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Draw the blinking cursor at the current cursor position if visible
    if (hasFocus()) {
      g.drawLine(cursorX, cursorY - 12, cursorX, cursorY + 3);  // Vertical line for the cursor
    }

    int x = 0;
    int y = cursorY;
    for (Object obj : content) {
      if (obj instanceof String) {
        g.drawString((String) obj, x, y);
        x += getFontMetrics(getFont()).stringWidth((String) obj);
      } else if (obj instanceof JButton button) {
        button.setLocation(x + 3, y - 14);
        x += button.getWidth() + 6;
      }
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 200);  // Define custom component size
  }

  public String getContent() {
    var strContent = new StringBuilder();
    for (Object item : content) {
      if (item instanceof JButton button) {
        strContent.append("${");
        strContent.append(button.getText());
        strContent.append("}");
      } else {
        strContent.append(item);
      }
    }
    return strContent.toString();
  }

  public void setContent(String strContent) {
    destructComponents();
    content.clear();
    cursorX = 0;
    cursorsOffset = 0;
    renderStringContent(strContent);
  }

  private void renderStringContent(String strContent) {
    var matcher = pattern.matcher(strContent);
    var start = 0;
    while (matcher.find()) {
      var precedingText = strContent.substring(start, matcher.start());
      for (char c : precedingText.toCharArray()) {
        addText(String.valueOf(c));
        cursorsOffset++;
      }
      addButton(matcher.group(1));
      cursorsOffset++;
      start = matcher.end();
    }

    if (start < strContent.length() - 1) {
      var precedingText = strContent.substring(start);
      for (char c : precedingText.toCharArray()) {
        addText(String.valueOf(c));
        cursorsOffset++;
      }
    }
  }

  private void destructComponents() {
    content.stream().filter(o -> o instanceof JButton)
        .forEach(b -> JAdvancedInputField.this.remove((JButton) b));
  }
}
