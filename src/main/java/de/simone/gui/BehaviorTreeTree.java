package de.simone.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;

import de.simone.btree.combat.CombatTask;
import de.simone.btree.logistic.LogisticTask;

public class BehaviorTreeTree extends JTree {
    static class NodeInfo {
        String label;
        Task.Status status;

        NodeInfo(String label, Task.Status status) {
            this.label = label;
            this.status = status;
        }
    }

    private final DefaultTreeModel treeModel;
    private final Map<String, DefaultMutableTreeNode> nodeMap = new HashMap<>();
    private BehaviorTree<?> behaviorTree;
    private Dimension dimension;

    public BehaviorTreeTree(BehaviorTree<?> behaviorTree) {
        super(new DefaultTreeModel(new DefaultMutableTreeNode(new NodeInfo("Behavior Tree", Task.Status.FRESH))));
        this.behaviorTree = behaviorTree;
        treeModel = (DefaultTreeModel) getModel();
        setBackground(Color.BLACK);
        setCellRenderer(new ExecutingTaskRenderer());
        setRootVisible(true);
        buildTree();
    }

    public static String getNodeName(Task<?> task) {
        String name = task.getClass().getSimpleName();
        if (task instanceof LogisticTask || task instanceof CombatTask) {
            name = task.toString();
        }
        return name;
    }

    private void buildTree() {
        DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) treeModel.getRoot());
        // rootNode.removeAllChildren();
        nodeMap.clear();
        if (behaviorTree != null) {
            for (int i = 0; i < behaviorTree.getChildCount(); i++) {
                rootNode.add(createNode(behaviorTree.getChild(i)));
            }
        }

        treeModel.reload();

        // getRowCount() grows while expanding, so this expands the whole tree
        for (int row = 0; row < getRowCount(); row++) {
            expandRow(row);
        }
    }

    private DefaultMutableTreeNode createNode(Task<?> task) {
        String label = getNodeName(task);
        NodeInfo nodeInfo = new NodeInfo(label, task.getStatus());
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeInfo);
        nodeMap.put(label, node);
        for (int i = 0; i < task.getChildCount(); i++) {
            node.add(createNode(task.getChild(i)));
        }
        return node;
    }

    private class ExecutingTaskRenderer extends JLabel implements TreeCellRenderer {
        private String htmlTemplate = "<html>%s %s</html>";

        public ExecutingTaskRenderer() {
            super();
            // setFont(new Font("Courier New", Font.PLAIN, 14));
            setFont(new Font("Consolas", Font.PLAIN, 14));
        }

        @Override
        public Component getTreeCellRendererComponent(JTree t, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (dimension != null) {
                Dimension dim = getSize();
                setSize(dimension.width, dim.height);
            }
            NodeInfo nodeInfo = (NodeInfo) ((DefaultMutableTreeNode) value).getUserObject();

            String leafString = nodeInfo.label;
            // String leafString = StringUtils.abbreviate(nodeInfo.label, 80) + " ";
            if (nodeInfo.status == Task.Status.RUNNING)
                leafString += "<b style='color:yellow;'>RUNNING</b>";
            if (nodeInfo.status == Task.Status.SUCCEEDED)
                leafString += "<b style='color:gray;'>SUCCEEDED</b>";
            if (nodeInfo.status == Task.Status.FAILED)
                leafString += "<b style='color:red;'>FAILED</b>";

            setText(String.format(htmlTemplate, leafString, ""));
            setForeground(Color.WHITE);
            return this;
        }
    }
}
