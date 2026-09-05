package de.simone.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.StringUtils;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.Task.Status;

import de.simone.btree.RTask;

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
    private final Map<Task<?>, DefaultMutableTreeNode> nodeMap = new HashMap<>();
    private DefaultMutableTreeNode executingNode;
    private BehaviorTree<?> behaviorTree;
    private Timer repaintTimer;
    private List<DefaultMutableTreeNode> nodesToRepaint;
    Dimension dimension;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BehaviorTreeTree(BehaviorTree<?> behaviorTree) {
        super(new DefaultTreeModel(new DefaultMutableTreeNode(new NodeInfo("Behavior Tree", Task.Status.FRESH))));
        this.behaviorTree = behaviorTree;
        this.nodesToRepaint = new ArrayList<>();

        repaintTimer = new Timer(100, e -> {
            if (!nodesToRepaint.isEmpty()) {
                executingNode = nodesToRepaint.removeFirst();
                repaint();
            }
        });
        repaintTimer.start();

        treeModel = (DefaultTreeModel) getModel();
        behaviorTree.addListener(new BehaviorTree.Listener() {
            @Override
            public void childAdded(Task task, int index) {
                //
            }

            @Override
            public void statusUpdated(Task task, Status previousStatus) {
                DefaultMutableTreeNode node = nodeMap.get(task);
                dimension = getSize();
                if (node == null)
                    return;

                NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
                nodeInfo.status = task.getStatus();
                nodesToRepaint.add(node);
            }
        });

        setBackground(Color.BLACK);
        setCellRenderer(new ExecutingTaskRenderer());
        setRootVisible(true);

        buildTree();
    }

    private void buildTree() {
        DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) treeModel.getRoot());
        // rootNode.removeAllChildren();
        nodeMap.clear();
        executingNode = null;

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
        String label = (task instanceof RTask) ? ((RTask) task).getName() : task.getClass().getSimpleName();
        NodeInfo nodeInfo = new NodeInfo(label, task.getStatus());
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeInfo);
        nodeMap.put(task, node);
        for (int i = 0; i < task.getChildCount(); i++) {
            node.add(createNode(task.getChild(i)));
        }
        return node;
    }

    public void stop() {
        executingNode = null;
        repaint();
    }

    private class ExecutingTaskRenderer extends DefaultTreeCellRenderer {
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
            String label = StringUtils.abbreviate(nodeInfo.label, 80) + " ";
            label += nodeInfo.status == Task.Status.FRESH ? "" : nodeInfo.status.toString();
            setText(label);
            setForeground(Color.WHITE);
            if (value == executingNode) {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
                setOpaque(true);
            } else {
                setOpaque(false);
            }
            return this;
        }
    }
}
