package net.reevik.swing.components;

import net.reevik.swing.components.toggle.ToggleEvent;
import net.reevik.swing.components.toggle.ToggleListenable;
import net.reevik.swing.components.toggle.ToggleListener;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.awt.GridBagConstraints.WEST;

public class JSelectiveToggleButton extends JPanel implements ToggleListenable {

    private static final int HEIGHT = 22;
    public static final int ARROW_WIDTH = 20;
    private boolean focus;
    private boolean active;
    private boolean toggled;
    private boolean memberOfToggleGroup;
    private String selection;
    private final JLabel selectedLabel = new JLabel();
    private final JSelectiveToggleButton.Configuration configuration;
    private final List<SelectionListener> selectionListeners = new ArrayList<>();
    private final List<ToggleListener> toggleListeners = new ArrayList<>();
    private Predicate<ToggleListenable> canTogglePredicate = (toggle) -> true;

    public JSelectiveToggleButton(JSelectiveToggleButton.Configuration config) {
        this.configuration = config;
        this.selection = config.items().getFirst();
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
        var popupmenu = new JPopupMenu("Edit");

        popupmenu.setBackground(configuration.toggled);
        configuration.items().stream()
                .map(this::createMenuItem)
                .forEach(popupmenu::add);

        popupmenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                JSelectiveToggleButton.this.repaint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.anchor = WEST;

        selectedLabel.setText(selection + "  ▾");
        selectedLabel.setForeground(Color.LIGHT_GRAY);
        selectedLabel.setFont(selectedLabel.getFont().deriveFont(11f));
        add(selectedLabel, gridBagConstraints);

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
                if (memberOfToggleGroup) {
                    if (!toggled) {
                        toggle();
                        active = false;
                        toggleListeners.forEach((t) -> t.onToggle(new ToggleEvent(JSelectiveToggleButton.this)));
                    } else if (!active) {
                        active = true;
                        popupmenu.show(selectedLabel, -10, selectedLabel.getY() + selectedLabel.getHeight() + 2);
                    }
                } else {
                    if (!toggled) {
                        toggle();
                        active = false;
                        toggleListeners.forEach((t) -> t.onToggle(new ToggleEvent(JSelectiveToggleButton.this)));
                    } else if (!active) {
                        active = true;
                        popupmenu.show(selectedLabel, -10, selectedLabel.getY() + selectedLabel.getHeight() + 2);
                    } else {
                        untoggle();
                        popupmenu.setVisible(false);
                        toggleListeners.forEach((t) -> t.onUnToggle(new ToggleEvent(JSelectiveToggleButton.this)));
                    }
                }

                super.mousePressed(e);
            }
        });
    }

    private void updateLabelOnUnToggle() {
        selectedLabel.setForeground(Color.lightGray);
    }

    private void updateLabelOnToggle() {
        Color color = new Color(63, 100, 139, 255);
        selectedLabel.setForeground(color);
    }

    private JMenuItem createMenuItem(String label) {
        var menuItem = new JMenuItem(label);
        menuItem.setIcon(null);
        menuItem.setIconTextGap(0);
        menuItem.setMargin(new Insets(0, -6, 0, 0));
        menuItem.setFont(menuItem.getFont().deriveFont(configuration.fontSize));
        menuItem.addActionListener(e -> {
            selection = e.getActionCommand();
            selectedLabel.setText(selection + "  ▾");
            configuration.action().accept(selection);
            selectionListeners.forEach(c -> c.onSelectionChange(selection));
            JSelectiveToggleButton.this.active = false;
            JSelectiveToggleButton.this.repaint();
            adjustWidthAccordingToTextWidth();
        });
        return menuItem;
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

    public void addListener(SelectionListener toggleListener) {
        if (!selectionListeners.contains(toggleListener)) {
            selectionListeners.add(toggleListener);
        }
    }

    public record Configuration(List<String> items,
                                Consumer<String> action,
                                Color background,
                                Color toggled,
                                Color label,
                                int width,
                                int fontSize) {

        public void add(String item) {
            items.add(item);
        }
    }

    public interface SelectionListener {
        void onSelectionChange(String newSelection);
    }

    @Override
    public void addListener(ToggleListener toggleListener) {
        if (!toggleListeners.contains(toggleListener)) {
            toggleListeners.add(toggleListener);
        }
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (!toggled) {
            updateLabelOnUnToggle();
            active = false;
        } else {
            updateLabelOnToggle();
        }
        repaint();
    }

    @Override
    public void toggle() {
        this.toggled = true;
        updateLabelOnToggle();
        repaint();
    }

    @Override
    public void untoggle() {
        this.toggled = false;
        this.active = false;
        updateLabelOnUnToggle();
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
}
