package net.reevik.swing.components.toggle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.WEST;
import static net.reevik.swing.graphics.GraphicalUtils.loadIcon;

public class JFlatToggleButton extends JPanel implements ToggleListenable {
    private static final int HEIGHT = 22;
    private static final int ARROW_WIDTH = 10;
    private static final String CLOSE_ICON = "/icons/x-circle.svg";
    private boolean focus;
    private boolean active;
    private boolean toggled;
    private String selection;
    private final JLabel selectedLabel = new JLabel();
    private final JButton closeButton = new JButton();
    private final JFlatToggleButton.Configuration configuration;
    private final List<ToggleListener> toggleListeners = new ArrayList<>();
    private boolean memberOfToggleGroup = false;
    private Predicate<ToggleListenable> canTogglePredicate = (toggle) -> true;

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
        setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.anchor = WEST;

        selectedLabel.setText(selection);
        selectedLabel.setForeground(Color.LIGHT_GRAY);
        selectedLabel.setFont(selectedLabel.getFont().deriveFont(configuration.fontSize));
        add(selectedLabel, gridBagConstraints);

        if (configuration.closeAction() != null) {
            closeButton.setIcon(loadIcon(CLOSE_ICON));
            closeButton.setOpaque(false);
            closeButton.setForeground(Color.LIGHT_GRAY);
            closeButton.setFont(selectedLabel.getFont().deriveFont((float) configuration.fontSize()));
            closeButton.setBorder(BorderFactory.createEmptyBorder());
            closeButton.addActionListener(e -> configuration.closeAction().accept(getCaption()));
            gridBagConstraints.weightx = 0.2;
            gridBagConstraints.anchor = EAST;
            gridBagConstraints.insets = new Insets(1, 2, 0, 2);
            add(closeButton, gridBagConstraints);
        }

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
                if (!canToggle()) {
                    return;
                }
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
        int rightPadding = ARROW_WIDTH;
        if (configuration.closeAction() != null) {
            rightPadding = 30;
        }
        int textWidth = getFontMetrics(getFont()).stringWidth(selection) + 10;
        setPreferredSize(new Dimension(textWidth + rightPadding, HEIGHT));
        setMaximumSize(new Dimension(textWidth + rightPadding, HEIGHT));
        setMinimumSize(new Dimension(textWidth + rightPadding, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(configuration.background());
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
                                Color foreground,
                                int width,
                                int fontSize,
                                Consumer<String> closeAction) {
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

    @Override
    public void setMemberOfToggleGroup(boolean memberOfToggleGroup) {
        this.memberOfToggleGroup = memberOfToggleGroup;
    }

    @Override
    public boolean canToggle() {
        return canTogglePredicate.test(this);
    }

    @Override
    public void setCanTogglePredicate(
            Predicate<ToggleListenable> canTogglePredicate) {
        this.canTogglePredicate = canTogglePredicate;
    }

    @Override
    public boolean isToggled() {
        return toggled;
    }

    public String getCaption() {
        return configuration.caption();
    }
}
