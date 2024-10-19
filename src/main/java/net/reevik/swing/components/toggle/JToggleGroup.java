package net.reevik.swing.components.toggle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JToggleGroup extends JPanel implements ToggleListener {
    private final List<ToggleListenable> toggles = new ArrayList<>();
    private final List<ToggleChangeListenable> toggleChangeListenables = new ArrayList<>();

    public void selectFirst() {
        toggles.stream().findFirst().ifPresent(ToggleListenable::toggle);
    }

    public void addToggle(JFlatToggleButton toggle) {
        toggle.setMemberOfToggleGroup(true);
        if (!toggles.contains(toggle)) {
            toggles.add(toggle);
            toggle.addListener(this);
        }

        add(toggle);
    }

    @Override
    public void onToggle(ToggleEvent event) {
        untoggleAll();
        performAction(event, ToggleListenable::toggle);
        toggleChangeListenables.forEach(t -> t.onToggleChange(event));
    }

    private void untoggleAll() {
        for (var toggle : toggles) {
            toggle.untoggle();
        }
    }

    @Override
    public void onUnToggle(ToggleEvent event) {
        performAction(event, ToggleListenable::untoggle);
    }

    private void performAction(ToggleEvent event, Consumer<ToggleListenable> consumer) {
        for (var toggle : toggles) {
            if (toggle == event.toggle()) {
                consumer.accept(toggle);
            }
        }
    }

    public void cleanToggles() {
        toggles.clear();
    }

    public void addChangeListenable(ToggleChangeListenable changeListenable) {
        if (!toggleChangeListenables.contains(changeListenable)) {
            toggleChangeListenables.add(changeListenable);
        }
    }
}
