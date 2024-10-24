package net.reevik.swing.components;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.VERTICAL;
import static javax.swing.text.SimpleAttributeSet.EMPTY;

public class JHttpViewer extends JPanel {
    private JTextPane editor = new JTextPane();
    private StyledDocument doc;

    public JHttpViewer() {
        setLayout(new GridBagLayout());
        editor.setEditable(false);
        editor.setFont(new Font("Courier", Font.PLAIN, 11));
        var lineNumbers = new JLineNumbers(this);

        doc = (StyledDocument) editor.getDocument();
        var layoutConstraints = new GridBagConstraints();

        layoutConstraints.gridx = 0;
        layoutConstraints.fill = VERTICAL;
        add(lineNumbers, layoutConstraints);

        layoutConstraints.gridx = 1;
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 1.0;
        layoutConstraints.fill = BOTH;

        var scrollableEditor = new JScrollPane(editor);
        scrollableEditor.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollableEditor.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollableEditor.setBorder(BorderFactory.createEmptyBorder());
        scrollableEditor.setRowHeaderView(lineNumbers);
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("insertUpdate");
                revalidate();
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                System.out.println("removeUpdate");
                revalidate();
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate");
                revalidate();
                repaint();
            }
        });
        scrollableEditor.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("getViewport().addChangeListener");
                lineNumbers.revalidate();
                lineNumbers.repaint();
            }
        });
        scrollableEditor.addPropertyChangeListener(evt ->{
            System.out.println("addPropertyChangeListener");
            lineNumbers.revalidate();
            lineNumbers.repaint();
        });
        add(scrollableEditor, layoutConstraints);

        lineNumbers.setPreferredSize(new Dimension(32, lineNumbers.getHeight()));
        lineNumbers.setMinimumSize(new Dimension(32, lineNumbers.getHeight()));
        lineNumbers.setMaximumSize(new Dimension(32, lineNumbers.getHeight()));
    }

    public static int getHiddenPixels(JTextPane textPane) {
        try {
            // Get the visible rectangle of the JTextPane (viewport bounds)
            Rectangle visibleRect = textPane.getVisibleRect();
            int visibleY = visibleRect.y; // Top y-position of the visible area

            // Get the start offset (position) of the first visible character
            int startOffset = textPane.viewToModel2D(new Point(0, visibleY));

            // Get the rectangle of the line containing the first visible character
            Rectangle lineRect = textPane.modelToView(startOffset);

            if (lineRect != null) {
                // Calculate the hidden pixels by subtracting the top of the visible area from the top of the line
                int hiddenPixels = visibleY - lineRect.y;
                return Math.max(0, hiddenPixels);  // Ensure no negative values
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public void setText(String text) {
        editor.setText(text);
        render();
    }

    private void render() {
        var styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(EMPTY, StyleConstants.Foreground, new Color(73, 110, 149, 255));
        var pattern = Pattern.compile("[\\[:,]\\s?+\"(.+?)\"");
        applyPattern(editor.getText(), pattern, attributeSet);
    }

    private void applyPattern(String text, Pattern pattern, AttributeSet attributeSet) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            doc.setCharacterAttributes(matcher.start(1), matcher.end(1) - matcher.start(1), attributeSet, false);
        }
    }

    public JTextPane getEditor() {
        return editor;
    }
}