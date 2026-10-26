package de.simone.ui.forms;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.formdev.flatlaf.FlatClientProperties;

import de.simone.UIUtils;
import de.simone.command.CombatCenterListener;
import de.simone.command.DogTag;
import de.simone.command.UnitsCenter;
import de.simone.command.UnitsCenterListener;
import de.simone.ui.system.Form;

public class CombatCenterView extends Form implements UnitsCenterListener, CombatCenterListener {

    private Vector<BehaviorTreeInfo> comboNBoxModel = new Vector<>();
    private JComboBox<BehaviorTreeInfo> treeJComboBox;
    private JPanel header;
    private JPanel controlPanel;
    private JTextArea textArea;
    private JSplitPane splitPane;
    private JTextArea placehloder;

    public CombatCenterView() {
        setLayout(new BorderLayout());

        treeJComboBox = new JComboBox<>(comboNBoxModel);
        treeJComboBox.putClientProperty(FlatClientProperties.STYLE, "minimumWidth:200;");
        treeJComboBox.addActionListener(e -> {
            BehaviorTreeInfo selected = (BehaviorTreeInfo) treeJComboBox.getSelectedItem();
            splitPane.remove(splitPane.getLeftComponent());
            JScrollPane pane = new JScrollPane(new BehaviorTreeTree((BehaviorTree<?>) selected.behaviorTree()));
            splitPane.setLeftComponent(pane);
        });

        header = UIUtils.getHeader("Combat Center",
                "Displays the Current active Squads and the communications between them and the military center.");

        Box controlsBox = Box.createHorizontalBox();
        controlsBox.setBorder(new EmptyBorder(5, 5, 5, 5));
        controlsBox.add(new JLabel("Behavior Tree:"));
        controlsBox.add(Box.createHorizontalStrut(10));
        controlsBox.add(treeJComboBox);
        controlPanel = UIUtils.getControlPanel("Squads. Select a squad to see the behavior tree", controlsBox);

        textArea = UIUtils.getConsoleTextArea();
        textArea.setText("\n\tCombat Console: Ready for combat.");

        placehloder = UIUtils.getConsoleTextArea();
        placehloder.setText("\n\tSquads Tree: No Squads.");

        JScrollPane right = new JScrollPane(textArea);
        splitPane = UIUtils.getSplitPane(getClass().getSimpleName(), placehloder, right, JSplitPane.HORIZONTAL_SPLIT);
        add(splitPane, BorderLayout.CENTER);
        UnitsCenter.addListener(this);
    }

    public JComponent getTitle() {
        return header;
    }

    public JComponent getControls() {
        return controlPanel;
    }

    @Override
    public void personalUpdated(List<DogTag> units) {
        UnitsCenter.getSquads().forEach(squad -> {
            BehaviorTreeInfo info = new BehaviorTreeInfo(squad.squadID, squad.behaviorTree);
            if (!comboNBoxModel.contains(info)) {
                comboNBoxModel.add(info);
            }
        });
    }

    @Override
    public void commsUpdated(String logs) {
        SwingUtilities.invokeLater(() -> textArea.setText(logs));
    }

    private record BehaviorTreeInfo(String name, BehaviorTree<?> behaviorTree) {
        @Override
        public String toString() {
            return name;
        }

        @Override
        public final boolean equals(Object arg0) {
            if (arg0 instanceof BehaviorTreeInfo other) {
                return name.equals(other.name);
            }
            return false;
        }
    }

}
