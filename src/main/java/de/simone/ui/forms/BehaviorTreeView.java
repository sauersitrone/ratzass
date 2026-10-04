package de.simone.ui.forms;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import de.simone.Config;
import de.simone.UIUtils;
import de.simone.command.DogTag;
import de.simone.command.LogisticCenter;
import de.simone.command.UnitsCenter;
import de.simone.command.UnitsCenterListener;
import de.simone.ui.system.Form;

/**
 * Displays the gdx-ai behavior tree using a JTree, highlighting the currently
 * executing LeafTask.
 */
public class BehaviorTreeView extends Form implements UnitsCenterListener {

    private Vector<BehaviorTreeInfo> behaviorTrees = new Vector<>();
    private JCheckBox scrollToExecutingNode;
    private JComboBox<BehaviorTreeInfo> treeJComboBox;
    private JScrollPane currentScrollPane;
    private JPanel header;
    private JPanel controlPanel;

    public BehaviorTreeView() {
        BehaviorTreeInfo logBT = new BehaviorTreeInfo("Logistic Center", LogisticCenter.behaviorTree);
        this.behaviorTrees.add(logBT);

        treeJComboBox = new JComboBox<>(behaviorTrees);
        treeJComboBox.addActionListener(e -> {
            BehaviorTreeInfo selected = (BehaviorTreeInfo) treeJComboBox.getSelectedItem();
            if (currentScrollPane != null)
                remove(currentScrollPane);

            currentScrollPane = new JScrollPane(new BehaviorTreeTree((BehaviorTree<?>) selected.behaviorTree()));
            add(currentScrollPane, BorderLayout.CENTER);
        });

        setLayout(new BorderLayout());
        header = UIUtils.getHeader("Behavior Tree",
                "Displays the gdx-ai behavior tree using a JTree, highlighting the currently executing LeafTask.");

        scrollToExecutingNode = UIUtils.getPropertyCheckBox("Scroll to Executing Node", Config.scrollToExecutingNode,
                e -> Config.scrollToExecutingNode = scrollToExecutingNode.isSelected());
        Box controlsBox = Box.createHorizontalBox();
        controlsBox.add(new JLabel("Behavior Tree:"));
        controlsBox.add(Box.createHorizontalStrut(10));
        controlsBox.add(treeJComboBox);
        controlPanel = UIUtils.getControlPanel("Controls", scrollToExecutingNode, controlsBox);

        treeJComboBox.setSelectedIndex(0);
        UnitsCenter.addListener(this);
    }

    public JComponent getTitle() {
        return header;
    }

    public JComponent getControls() {
        return controlPanel;
    }

    @Override
    public void updatePersonal(List<DogTag> units) {
        UnitsCenter.getSquads().forEach(squad -> {
            BehaviorTreeInfo info = new BehaviorTreeInfo(squad.squadID, squad.behaviorTree);
            if (!behaviorTrees.contains(info)) {
                behaviorTrees.add(info);
            }
        });
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
