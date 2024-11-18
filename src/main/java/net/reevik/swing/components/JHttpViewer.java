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
                revalidate();
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                revalidate();
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                revalidate();
                repaint();
            }
        });
        scrollableEditor.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lineNumbers.revalidate();
                lineNumbers.repaint();
            }
        });
        scrollableEditor.addPropertyChangeListener(evt ->{
            lineNumbers.revalidate();
            lineNumbers.repaint();
        });
        add(scrollableEditor, layoutConstraints);

        lineNumbers.setPreferredSize(new Dimension(32, lineNumbers.getHeight()));
        lineNumbers.setMinimumSize(new Dimension(32, lineNumbers.getHeight()));
        lineNumbers.setMaximumSize(new Dimension(32, lineNumbers.getHeight()));
    }

    public void setText(String text) {
        editor.setText(text);
        render();
    }

    private void render() {
        var styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(EMPTY, StyleConstants.Foreground, new Color(95,160,217, 255));
        var patternJson = Pattern.compile("[:]\\s?+\"(.+?)\"[,}]");
        //var patternParam= Pattern.compile(":\\s?+(.+?)\n");
        //applyPattern(editor.getText(), patternParam, attributeSet);
        applyPattern(editor.getText(), patternJson, attributeSet);
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