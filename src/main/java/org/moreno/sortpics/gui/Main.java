package org.moreno.sortpics.gui;

import org.moreno.sortpics.controller.FirstPanelController;
import org.moreno.sortpics.model.FirstPanelModel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Create a new instance of FolderAnalyzer
        FolderAnalyzer folderAnalyzer = new FolderAnalyzer();
        FirstPanelModel model = new FirstPanelModel();
        FirstPanelController firstPanelController = new FirstPanelController(folderAnalyzer, model);

        // Create a new JFrame to hold the FolderAnalyzer
        JFrame frame = new JFrame("Folder Analyzer");
        frame.setContentPane(folderAnalyzer.getMainPanel());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Pack the frame (resize it to fit its subcomponents), and then make it visible
        frame.pack();
        frame.setVisible(true);
    }

}
