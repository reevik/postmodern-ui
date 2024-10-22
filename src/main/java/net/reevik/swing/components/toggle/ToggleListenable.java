package net.reevik.swing.components.toggle;

public interface ToggleListenable {
    void addListener(ToggleListener toggleListener);

    void toggle();

    void untoggle();

    void setMemberOfToggleGroup(boolean memberOfToggleGroup);
}
