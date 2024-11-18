package net.reevik.swing.components.toggle;

import java.awt.Color;
import java.util.function.Predicate;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JToggleGroup extends RoundedPanel implements ToggleListener {

    private final List<ToggleListenable> toggles = new ArrayList<>();
    private final List<ToggleChangeListenable> toggleChangeListenables = new ArrayList<>();
    private Predicate<ToggleListenable> canTogglePredicate;
    private Predicate<ToggleListenable> canUnogglePredicate;

    public JToggleGroup(int arcWidth, int arcHeight, Color backgroundColor) {
        super(arcWidth, arcHeight, backgroundColor);
    }

    public JToggleGroup() {
        super(2, 2, Color.lightGray);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public void toggleFirst() {
        toggles.stream().findFirst().ifPresent(ToggleListenable::toggle);
    }

    public void addToggle(ToggleListenable toggle) {
        if (!toggles.contains(toggle)) {
            toggles.add(toggle);
            toggle.addListener(this);
            if (toggles.isEmpty()) {
                toggle.toggle();
            }
        }
        if (toggle instanceof JComponent component) {
            add(component);
        }
    }

    @Override
    public void onToggle(ToggleEvent event) {
        untoggleOthers(event.toggle());
        toggleChangeListenables.forEach(t -> t.onToggleChange(event));

    }

    private void untoggleOthers(ToggleListenable toggledToggle) {
        for (var toggle : toggles) {
            if (toggle != toggledToggle) {
                toggle.untoggle();
            }
        }
    }

    @Override
    public void onUnToggle(ToggleEvent event) {
    }

    public void cleanToggles() {
        toggles.clear();
    }

    public void addChangeListenable(ToggleChangeListenable changeListenable) {
        if (!toggleChangeListenables.contains(changeListenable)) {
            toggleChangeListenables.add(changeListenable);
        }
    }

    public void setCanTogglePredicate(Predicate<ToggleListenable> canTogglePredicate) {
        this.canTogglePredicate = canTogglePredicate;
        toggles.forEach(toggle -> toggle.setCanTogglePredicate(canTogglePredicate));
    }

    public Predicate<ToggleListenable> getCanUnogglePredicate() {
        return canUnogglePredicate;
    }

}
