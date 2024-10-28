package net.reevik.swing.components.toggle;

import java.util.function.Predicate;

public interface ToggleListenable {
    void addListener(ToggleListener toggleListener);

    void toggle();

    void untoggle();

    void setMemberOfToggleGroup(boolean memberOfToggleGroup);

    boolean canToggle();

    void setCanTogglePredicate(Predicate<ToggleListenable> canTogglePredicate);

    boolean isToggled();
}
