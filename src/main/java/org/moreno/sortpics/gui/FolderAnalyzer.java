package org.moreno.sortpics.gui;

import lombok.Getter;
import org.moreno.sortpics.model.ImageFileData;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class FolderAnalyzer {
    private JButton btChooseFolder;
    private JTextField tfFolderToOrder;
    private JList<ImageFileData> lsFilesToProcess;
    private JLabel lbInfo;
    private JLabel lbFilesProcessing;
    private JButton btSortPhotos;
    private JProgressBar pbOrderProgress;
    private JPanel mainPanel;

    public void setCellRenderer(ThumbnailListCellRenderer renderer) {
        this.lsFilesToProcess.setCellRenderer(renderer);
    }

    public void setProgressValue(int progress) {
        this.pbOrderProgress.setValue(progress);
    }

    public void sortJList() {
        DefaultListModel<ImageFileData> newListModel = new DefaultListModel<>();
        ListModel<ImageFileData> listModel = this.lsFilesToProcess.getModel();
        List<ImageFileData> list = new ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            list.add(listModel.getElementAt(i));
        }
        Collections.sort(list);

        newListModel.addAll(list);
        this.lsFilesToProcess.setModel(newListModel);
    }

    public void updateJList(List<ImageFileData> listImg) {
        DefaultListModel<ImageFileData> newListModel = new DefaultListModel<>();
        newListModel.addAll(listImg);
        this.lsFilesToProcess.setModel(newListModel);
    }
}
