package net.reevik.swing.components.toggle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.awt.GridBagConstraints.WEST;

public class JFlatToggleButton extends JPanel implements ToggleListenable {
    private static final int HEIGHT = 22;
    private static final int ARROW_WIDTH = 20;
    private boolean focus;
    private boolean active;
    private boolean toggled;
    private String selection;
    private final JLabel selectedLabel = new JLabel();
    private final JFlatToggleButton.Configuration configuration;
    private final List<ToggleListener> toggleListeners = new ArrayList<>();
    private boolean memberOfToggleGroup = false;

    public JFlatToggleButton(JFlatToggleButton.Configuration config) {
        this.configuration = config;
        this.selection = config.caption;
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    e.consume();
                }
            }
        });
        adjustWidthAccordingToTextWidth();
        setBackground(configuration.background);
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.anchor = WEST;

        selectedLabel.setText(selection);
        selectedLabel.setForeground(Color.LIGHT_GRAY);
        selectedLabel.setFont(selectedLabel.getFont().deriveFont(configuration.fontSize));
        add(selectedLabel, gridBagConstraints);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                focus = true;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                repaint();
                super.mouseEntered(e);
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
                if (memberOfToggleGroup) {
                    updateLabelOnToggled();
                    toggled = true;
                    toggleListeners.forEach(t -> t.onToggle(new ToggleEvent(JFlatToggleButton.this)));
                    return;
                }

                if (!toggled) {
                    updateLabelOnToggled();
                    toggled = true;
                    toggleListeners.forEach(t -> t.onToggle(new ToggleEvent(JFlatToggleButton.this)));
                } else {
                    toggled = false;
                    toggleListeners.forEach(t -> t.onUnToggle(new ToggleEvent(JFlatToggleButton.this)));
                    updateLabelOnUnToggled();
                }

                super.mousePressed(e);
            }
        });
    }

    private void updateLabelOnUnToggled() {
        selectedLabel.setForeground(Color.lightGray);
    }

    private void updateLabelOnToggled() {
        var color = new Color(63, 100, 139, 255);
        selectedLabel.setForeground(color);
    }

    private void adjustWidthAccordingToTextWidth() {
        int textWidth = getFontMetrics(getFont()).stringWidth(selection);
        setPreferredSize(new Dimension(textWidth + ARROW_WIDTH, HEIGHT));
        setMaximumSize(new Dimension(textWidth + ARROW_WIDTH, HEIGHT));
        setMinimumSize(new Dimension(textWidth + ARROW_WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(configuration.background);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
        g2.dispose();
    }

    public void select(String selection) {
        this.selectedLabel.setText(selection);
        this.selection = selection;
    }

    public record Configuration(String caption,
                                Consumer<String> action,
                                Color background,
                                Color toggled,
                                Color label,
                                int width,
                                int fontSize) {
    }

    public void addListener(ToggleListener toggleListener) {
        if (!toggleListeners.contains(toggleListener)) {
            toggleListeners.add(toggleListener);
        }
    }

    @Override
    public void toggle() {
        toggled = true;
        updateLabelOnToggled();
    }

    @Override
    public void untoggle() {
        toggled = false;
        updateLabelOnUnToggled();
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (toggled) {
            updateLabelOnToggled();
        } else {
            updateLabelOnUnToggled();
        }
        repaint();
    }

    public void setMemberOfToggleGroup(boolean memberOfToggleGroup) {
        this.memberOfToggleGroup = memberOfToggleGroup;
    }
}
