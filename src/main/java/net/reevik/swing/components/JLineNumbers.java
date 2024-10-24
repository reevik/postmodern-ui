package net.reevik.swing.components;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static java.awt.Font.PLAIN;

public class JLineNumbers extends JComponent {

    private final JHttpViewer httpViewer;
    private final FontMetrics fontMetrics;

    public JLineNumbers(JHttpViewer httpViewer) {
        this.httpViewer = httpViewer;
        this.fontMetrics = httpViewer.getFontMetrics(httpViewer.getFont());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.darkGray);
        g.clearRect(0, 0, getWidth(), getHeight());
        g.fillRect(0, 0, getWidth(), getHeight());
        var editor = httpViewer.getEditor();
        g.setFont(new Font("Courier", PLAIN, 11));
        var visibleRect = editor.getVisibleRect();
        g.setColor(Color.lightGray);

        int lineHeight = fontMetrics.getHeight() - 3;
        int startOffset = editor.viewToModel2D(new Point(0, visibleRect.y));
        int endOffset = editor.viewToModel2D(new Point(0, visibleRect.y + visibleRect.height));
        int startLine = getLineNumber(startOffset);
        int endLine = getLineNumber(endOffset);

        int y = -1 * (int)getInvisible() + lineHeight;
        for (int line = startLine; line <= endLine; line++) {
            g.drawString(Integer.toString(line), 5, y);
            y += lineHeight;
        }
    }

    private int getLineNumber(int offset) {
        Element root = httpViewer.getEditor().getDocument().getDefaultRootElement();
        return root.getElementIndex(offset) + 1;
    }

    private double getInvisible() {
        try {
            // Get the visible rectangle of the JTextPane (viewport bounds)
            Rectangle visibleRect = httpViewer.getEditor().getVisibleRect();
            int visibleY = visibleRect.y; // Top y-position of the visible area

            // Get the start offset (position) of the first visible character
            int startOffset = httpViewer.getEditor().viewToModel2D(new Point(0, visibleY));

            // Get the rectangle of the line containing the first visible character
            var lineRect = httpViewer.getEditor().modelToView2D(startOffset);

            if (lineRect != null) {
                // Calculate the hidden pixels by subtracting the top of the visible area from the top of the line
                double hiddenPixels = visibleY - lineRect.getY();
                return Math.max(0, hiddenPixels);  // Ensure no negative values
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}