package net.sourceforge.ganttproject;

import letzplay.GanttGameLoop;
import letzplay.Output;
import letzplay.quest.Quest;
import letzplay.quest.Task;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;

import java.util.LinkedList;
import java.util.List;


public class GameOutput {
    private static Output output = GanttGameLoop.getGameLoop().output();

    private static List<JTextField> lvls = new LinkedList<>();
    private static List<JTree> quests = new LinkedList<>();
    private static List<JTextArea> desc = new LinkedList<>();
    private static DefaultTreeModel treeModel;

    public static void addTextField(JTextField field) {
        lvls.add(field);
    }

    public static void addQuestsList(JTree list) {
        quests.add(list);
    }

    public static void addDesc(JTextArea list) {
        desc.add(list);
    }

    public static void refresh() {
        GanttGameLoop.getGameLoop().update();
        output = GanttGameLoop.getGameLoop().output();
        for (JTextField a : lvls) {
            a.setText("Current Level: " + output.lvl + ", " + output.exp + "% to next Level");
        }

        for (JTree b : quests) {
            createNodes((DefaultMutableTreeNode) treeModel.getRoot());
            b.setModel(treeModel);
            for (int i = 0; i < b.getRowCount(); i++) {
                b.expandRow(i);
            }
        }

    }

    public static Box getQuestBox() {

        JTextArea desc = new JTextArea();
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setPreferredSize(new Dimension(100, 100));





        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode("Quests");
        createNodes(top);

        treeModel = new DefaultTreeModel(top);

        JTree tree = new JTree(treeModel);
        tree.setPreferredSize(new Dimension(100, 100));
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();

                if (node == null) return;

                Object nodeInfo = node.getUserObject();
                for (int i = 0; i < output.quests.size(); i++) {
                    for (int j = 0; j < output.quests.get(i).getTaskList().size(); j++) {
                        if (output.quests.get(i).getTaskList().get(j).getTitle().equals(nodeInfo)) {
                            displayTask(output.quests.get(i).getTaskList().get(j));
                        }
                    }
                    if (output.quests.get(i).getTitle().equals(nodeInfo)) {
                        displayQuest(output.quests.get(i));
                    }
                }
            }
        });

        //TODO image not woring
        ImageIcon tutorialIcon = new ImageIcon(GameOutput.class.getResource("/icons/done_small.png"));
        if (tutorialIcon != null) {
            tree.setCellRenderer(new MyRenderer(tutorialIcon));
        }

        JTextField lvl = new JTextField();
        addTextField(lvl);
        addDesc(desc);
        addQuestsList(tree);

        JScrollPane scrollQuest = new JScrollPane(tree);

        JScrollPane scrollDesc = new JScrollPane(desc);

        refresh();

        final Box questBox = Box.createVerticalBox();
        questBox.add(lvl);
        questBox.add(scrollQuest);
        questBox.add(scrollDesc);
        questBox.setVisible(true);

        return questBox;
    }

    private static void createNodes(DefaultMutableTreeNode top) {
        top.removeAllChildren();

        DefaultMutableTreeNode quest = null;
        DefaultMutableTreeNode task = null;

        for (Quest q : output.quests) {
            quest = new DefaultMutableTreeNode(q.getTitle());
            top.add(quest);
            for (Task t : q.getTaskList()) {
                task = new DefaultMutableTreeNode(t.getTitle());
                quest.add(task);
            }
        }
        treeModel = new DefaultTreeModel(top);
    }

    private static void displayQuest(Quest q) {

        if (q != null) {
            for (JTextArea a : desc) {
                a.setText(q.getDescription());
            }

        } else { //null url
            for (JTextArea a : desc) {
                a.setText("NotFound");
            }
        }
    }

    private static void displayTask(Task t) {
        if (t != null) {
            for (JTextArea a : desc) {
                a.setText(t.getDescription());
            }

        } else { //null url
            for (JTextArea a : desc) {
                a.setText("NotFound");
            }
        }
    }

    static class MyRenderer extends DefaultTreeCellRenderer {
        Icon tutorialIcon;

        public MyRenderer(Icon icon) {
            tutorialIcon = icon;
        }

        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);
            if (isDone(value)) {
                setIcon(tutorialIcon);
            } else {
            }

            return this;
        }

        protected boolean isDone(Object value) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;

            Object nodeInfo = node.getUserObject();


            for (int i = 0; i < output.quests.size(); i++) {
                for (int j = 0; j < output.quests.get(i).getTaskList().size(); j++) {
                    if (output.quests.get(i).getTaskList().get(j).getTitle().equals(nodeInfo)) {
                        if(output.quests.get(i).getTaskList().get(j).isDone()){
                            return true;
                        }
                    }
                }
                if (output.quests.get(i).getTitle().equals(nodeInfo)) {
                    if(output.quests.get(i).finished()){
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
