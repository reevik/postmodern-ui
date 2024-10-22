package net.reevik.swing.components;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.VERTICAL;

public class JHttpViewer extends JPanel {

    public JHttpViewer() {
        setBackground(Color.BLUE);
        setLayout(new GridBagLayout());
        var lineNumbers = new JPanel();
        lineNumbers.setBackground(Color.darkGray);
        var editor = new JTextArea();
        editor.setFont(new Font("Courier", Font.PLAIN, 11));
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
        add(scrollableEditor, layoutConstraints);

        lineNumbers.setPreferredSize(new Dimension(32, lineNumbers.getHeight()));
        lineNumbers.setMinimumSize(new Dimension(32, lineNumbers.getHeight()));
        lineNumbers.setMaximumSize(new Dimension(32, lineNumbers.getHeight()));
    }
}