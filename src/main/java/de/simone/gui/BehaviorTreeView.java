package de.simone.gui;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.badlogic.gdx.ai.btree.BehaviorTree;

import de.simone.Env;
import de.simone.command.LogisticCenter;
import de.simone.ui.menu.MyDrawerBuilder;
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
    private BehaviorTreeTree currentTree;

    public BehaviorTreeView() {
        BehaviorTreeInfo info = new BehaviorTreeInfo("Logistic", LogisticCenter.getInstance().behaviorTree);
        // BehaviorTreeInfo info = new BehaviorTreeInfo("Combat Center", CombatCenter.getInstance().behaviorTree);
        this.behaviorTrees.add(info);

        treeJComboBox = new JComboBox<>(behaviorTrees);
        treeJComboBox.addActionListener(e -> {
            BehaviorTreeInfo selected = (BehaviorTreeInfo) treeJComboBox.getSelectedItem();
            if (currentTree != null)
                remove(currentTree);

            currentTree = new BehaviorTreeTree((BehaviorTree<?>) selected.behaviorTree());
            add(new JScrollPane(currentTree), BorderLayout.CENTER);
        });

        setLayout(new BorderLayout());
        JPanel header = UIUtils.getHeader("Behavior Tree",
                "Displays the gdx-ai behavior tree using a JTree, highlighting the currently executing LeafTask.");

        scrollToExecutingNode = UIUtils.getCheckBox("Scroll to Executing Node", Env.scrollToExecutingNode,
                e -> Env.scrollToExecutingNode = scrollToExecutingNode.isSelected());
        stepBehaviorTree = new JButton("Step");
        stepBehaviorTree.addActionListener(e -> Env.treeStatus = Env.BehaviorTreeStatus.Stepping);
        runBehaviorTree = new JButton(Env.treeStatus == Env.BehaviorTreeStatus.Running ? "Suspend" : "Run");
        runBehaviorTree.addActionListener(e -> {
            if (Env.treeStatus == Env.BehaviorTreeStatus.Running) {
                Env.treeStatus = Env.BehaviorTreeStatus.Suspended;
                runBehaviorTree.setText("Run");
                stepBehaviorTree.setEnabled(false);

            } else {
                Env.treeStatus = Env.BehaviorTreeStatus.Running;
                runBehaviorTree.setText("Suspend");
                stepBehaviorTree.setEnabled(true);
            }
        });
        Box controlsBox = Box.createHorizontalBox();
        controlsBox.add(new JLabel("Behavior Tree:"));
        controlsBox.add(Box.createHorizontalStrut(10));
        controlsBox.add(treeJComboBox);

        JPanel controlPanel = UIUtils.getControlPanel("Controls", scrollToExecutingNode, stepBehaviorTree,
                runBehaviorTree, controlsBox);
        JPanel headerPanel = UIUtils.getInVerticalPanel(MyDrawerBuilder.getEnvView(), header, controlPanel);
        add(headerPanel, BorderLayout.NORTH);

        treeJComboBox.setSelectedIndex(0);
    }

}
