package de.simone.ui.forms;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import de.simone.Config;
import de.simone.UIUtils;
import de.simone.command.LogisticCenter;
import de.simone.ui.system.Form;

/**
 * Displays the gdx-ai behavior tree using a JTree, highlighting the currently
 * executing LeafTask.
 */
public class BehaviorTreeView extends Form {

    record BehaviorTreeInfo(String name, BehaviorTree<?> behaviorTree) {
        @Override
        public String toString() {
            return name;
        }
    }

    private Vector<BehaviorTreeInfo> behaviorTrees = new Vector<>();
    private JCheckBox scrollToExecutingNode;
    private JButton stepBehaviorTree;
    private JButton runBehaviorTree;
    private JComboBox<BehaviorTreeInfo> treeJComboBox;
    private JScrollPane currentScrollPane;
    private JPanel header;
    private JPanel controlPanel;

    public BehaviorTreeView() {
        BehaviorTreeInfo logBT = new BehaviorTreeInfo("Logistic", LogisticCenter.behaviorTree);
        // BehaviorTreeInfo info = new BehaviorTreeInfo("Combat Center",
        // CombatCenter.getInstance().behaviorTree);
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
        stepBehaviorTree = new JButton("Step");
        stepBehaviorTree.addActionListener(e -> Config.treeStatus = Config.BehaviorTreeStatus.Stepping);
        runBehaviorTree = new JButton(Config.treeStatus == Config.BehaviorTreeStatus.Running ? "Suspend" : "Run");
        runBehaviorTree.addActionListener(e -> {
            if (Config.treeStatus == Config.BehaviorTreeStatus.Running) {
                Config.treeStatus = Config.BehaviorTreeStatus.Suspended;
                runBehaviorTree.setText("Run");
                stepBehaviorTree.setEnabled(false);

            } else {
                Config.treeStatus = Config.BehaviorTreeStatus.Running;
                runBehaviorTree.setText("Suspend");
                stepBehaviorTree.setEnabled(true);
            }
        });
        Box controlsBox = Box.createHorizontalBox();
        controlsBox.add(new JLabel("Behavior Tree:"));
        controlsBox.add(Box.createHorizontalStrut(10));
        controlsBox.add(treeJComboBox);
        controlPanel = UIUtils.getControlPanel("Controls", scrollToExecutingNode, stepBehaviorTree,
                runBehaviorTree, controlsBox);

        treeJComboBox.setSelectedIndex(0);
    }

    public JComponent getTitle() {
        return header;
    }

    public JComponent getControls() {
        return controlPanel;
    }
}
