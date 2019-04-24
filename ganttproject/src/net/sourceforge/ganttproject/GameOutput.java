package net.sourceforge.ganttproject;

import letzplay.GanttGameLoop;
import letzplay.Output;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class GameOutput {
    private static Output output;

    private static List<JTextArea> texts = new LinkedList<>();

    public static void addTextArea(JTextArea area){
        texts.add(area);
    }

    public static void refresh() {
        GanttGameLoop.getGameLoop().update();
        output = GanttGameLoop.getGameLoop().output();
        for (JTextArea a:texts) {
            a.setText(output.lvl + "\n" + output.quests.toString());
        }
    }
}
