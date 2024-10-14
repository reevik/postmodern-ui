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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

public class AdvancedInputFieldTableCellEditor extends AbstractCellEditor implements TableCellEditor {
  private final JAdvancedInputField inputBox;
  private final EditorDelegate delegate;

  public AdvancedInputFieldTableCellEditor(JAdvancedInputField inputField) {

    this.inputBox = inputField;
    this.delegate = new EditorDelegate() {
      public void setValue(Object value) {
        inputField.setContent((value != null) ? value.toString() : "");
      }

      public Object getCellEditorValue() {
        return inputField.getContent();
      }
    };

    inputBox.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT ||
            e.getKeyCode() == KeyEvent.VK_RIGHT) {
          e.consume();  // Prevent JTable from processing the key event
        }
      }
    });

    InputMap inputMap = inputBox.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "none");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "none");
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {
    delegate.setValue(value);
    // It's required to gain focus in InputBox, and it should be invoked later.
    SwingUtilities.invokeLater(inputBox::requestFocusInWindow);
    return inputBox;
  }

  @Override
  public Object getCellEditorValue() {
    return delegate.getCellEditorValue();
  }

  @Override
  public boolean isCellEditable(EventObject anEvent) {
    return delegate.isCellEditable(anEvent);
  }

  @Override
  public boolean shouldSelectCell(EventObject anEvent) {
    return delegate.shouldSelectCell(anEvent);
  }

  @Override
  public boolean stopCellEditing() {
    return delegate.stopCellEditing();
  }

  @Override
  public void cancelCellEditing() {
    delegate.cancelCellEditing();
  }

  protected class EditorDelegate implements ActionListener, ItemListener, Serializable {

    /**  The value of this cell. */
    protected Object value;

    /**
     * Constructs an {@code EditorDelegate}.
     */
    protected EditorDelegate() {}

    /**
     * Returns the value of this cell.
     * @return the value of this cell
     */
    public Object getCellEditorValue() {
      return value;
    }

    /**
     * Sets the value of this cell.
     * @param value the new value of this cell
     */
    public void setValue(Object value) {
      this.value = value;
    }

    /**
     * Returns true if <code>anEvent</code> is <b>not</b> a
     * <code>MouseEvent</code>.  Otherwise, it returns true
     * if the necessary number of clicks have occurred, and
     * returns false otherwise.
     *
     * @param   anEvent         the event
     * @return  true  if cell is ready for editing, false otherwise
     * @see #setClickCountToStart
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent) {
      /*
      if (anEvent instanceof MouseEvent) {
        return ((MouseEvent)anEvent).getClickCount() >= clickCountToStart;
      }
       */
      return true;
    }

    /**
     * Returns true to indicate that the editing cell may
     * be selected.
     *
     * @param   anEvent         the event
     * @return  true
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent) {
      return true;
    }

    /**
     * Returns true to indicate that editing has begun.
     *
     * @param anEvent          the event
     * @return true to indicate editing has begun
     */
    public boolean startCellEditing(EventObject anEvent) {
      return true;
    }

    /**
     * Stops editing and
     * returns true to indicate that editing has stopped.
     * This method calls <code>fireEditingStopped</code>.
     *
     * @return  true
     */
    public boolean stopCellEditing() {
      fireEditingStopped();
      return true;
    }

    /**
     * Cancels editing.  This method calls <code>fireEditingCanceled</code>.
     */
    public void cancelCellEditing() {
      fireEditingCanceled();
    }

    /**
     * When an action is performed, editing is ended.
     * @param e the action event
     * @see #stopCellEditing
     */
    public void actionPerformed(ActionEvent e) {
      AdvancedInputFieldTableCellEditor.this.stopCellEditing();
    }

    /**
     * When an item's state changes, editing is ended.
     * @param e the action event
     * @see #stopCellEditing
     */
    public void itemStateChanged(ItemEvent e) {
      AdvancedInputFieldTableCellEditor.this.stopCellEditing();
    }
  }
}
