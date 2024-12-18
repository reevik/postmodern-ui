package net.reevik.swing;

import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import net.reevik.swing.components.JFlatComboBox;
import net.reevik.swing.components.JFlatComboBox.Configuration;

public class Main {

    public static void main(String[] args) {

        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "Postmodern");
            System.setProperty("apple.awt.application.appearance", "system");
        }

        SwingUtilities.invokeLater(() -> {
            FlatXcodeDarkIJTheme.setup();
            UIManager.put("Component.arrowType", "chevron");
            var jFrame = new JFrame();
            jFrame.setLayout(new FlowLayout());
            jFrame.setSize(800, 400);

            /*
            Configuration headerConfig = new Configuration("Header", (a) -> {
            }, Color.darkGray, Color.gray, Color.RED, 100, 11);

            Configuration bodyConfig = new Configuration("Body", (a) -> {
            }, Color.darkGray, Color.gray, Color.RED, 100, 11);

            Configuration queryConfig = new Configuration("Query", (a) -> {
            }, Color.darkGray, Color.gray, Color.RED, 100, 11);

            JToggleGroup jToggleGroup = new JToggleGroup();
            jToggleGroup.addToggle(new JFlatToggleButton(headerConfig));
            jToggleGroup.addToggle(new JFlatToggleButton(bodyConfig));
            jToggleGroup.addToggle(new JFlatToggleButton(queryConfig));
            jToggleGroup.selectFirst();


            JHttpViewer jHttpViewer = new JHttpViewer();
            jHttpViewer.setText("""
                    {"var"[ "feen" , "stieg"]}
                    """);
            jHttpViewer.setPreferredSize(new Dimension(400, 50));
            jHttpViewer.setMaximumSize(new Dimension(400, 50));
            jHttpViewer.setMinimumSize(new Dimension(400, 50));


            Configuration headerConfig = new Configuration("Header", (a) -> {
            }, Color.darkGray, Color.gray, Color.RED, 100, 11);

            Configuration bodyConfig = new Configuration("Body", (a) -> {
            }, Color.darkGray, Color.gray, Color.RED, 100, 11);

            Configuration queryConfig = new Configuration("Query", (a) -> {
            }, Color.darkGray, Color.gray, Color.RED, 100, 11);

            JToggleGroup jToggleGroup = new JToggleGroup();
            jToggleGroup.addToggle(new JFlatToggleButton(headerConfig));
            jToggleGroup.addToggle(new JFlatToggleButton(bodyConfig));
            jToggleGroup.addToggle(new JFlatToggleButton(queryConfig));
            jToggleGroup.toggleFirst();

             */
            // JAdvancedInputField inputField = new JAdvancedInputField();

            Configuration queryConfig = new Configuration(
                List.of("Local", "Production", "Stage", "Development"), (a) -> {
            }, Color.darkGray, Color.gray, Color.lightGray, 80, 12, true);
            jFrame.add(new JFlatComboBox(queryConfig));
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jFrame.setVisible(true);
        });
    }
}