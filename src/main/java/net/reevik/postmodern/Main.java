package net.reevik.postmodern;

import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import net.reevik.postmodern.ui.flatcombo.FlatComboBox;
import net.reevik.postmodern.ui.flatcombo.FlatComboBox.Configuration;

public class Main {

  public static void main(String[] args) {

    if (SystemInfo.isMacOS) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("apple.awt.application.name", "Postmodern");
      System.setProperty("apple.awt.application.appearance", "system");
    }

    FlatXcodeDarkIJTheme.setup();
    UIManager.put("Component.arrowType", "chevron");
    var jFrame = new JFrame();
    jFrame.setLayout(new GridBagLayout());
    jFrame.setSize(500, 400);

    var configuration = new Configuration(List.of("DEV", "STAGE", "PROD"),
        (s) -> System.out.println("selected = " + s),
        Color.darkGray, Color.gray, Color.LIGHT_GRAY, 80, 10);
    jFrame.add(new FlatComboBox(configuration));
    jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    jFrame.setVisible(true);
  }
}