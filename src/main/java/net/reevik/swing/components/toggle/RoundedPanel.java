package net.reevik.swing.components.toggle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
  private final int arcWidth;
  private final int arcHeight;
  private final Color backgroundColor;

  public RoundedPanel(int arcWidth, int arcHeight, Color backgroundColor) {
    this.arcWidth = arcWidth;
    this.arcHeight = arcHeight;
    this.backgroundColor = backgroundColor;
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // Draw the rounded rectangle background
    g2.setColor(backgroundColor);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
    g2.dispose();
  }

  @Override
  public void updateUI() {
    super.updateUI();
    setForeground(Color.BLACK);
    setBackground(Color.WHITE);
  }
}