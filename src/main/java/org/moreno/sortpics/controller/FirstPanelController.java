/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.controller;

import org.moreno.sortpics.controller.task.ImageLoaderWorker;
import org.moreno.sortpics.controller.task.SortPhotosTask;
import org.moreno.sortpics.gui.FolderAnalyzer;
import org.moreno.sortpics.gui.ThumbnailListCellRenderer;
import org.moreno.sortpics.model.FirstPanelModel;
import org.moreno.sortpics.model.ImageFileData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class FirstPanelController {

    FolderAnalyzer view;
    FirstPanelModel model;
    SortPhotosTask sortPhotosTask = null;
    ImageLoaderWorker imageLoaderWorker;

    public FirstPanelController(FolderAnalyzer view, FirstPanelModel model) {
        this.view = view;
        this.model = model;
        imageLoaderWorker = new ImageLoaderWorker(model);
        imageLoaderWorker.execute();
        ThumbnailListCellRenderer renderer = new ThumbnailListCellRenderer(model);
        view.setCellRenderer(renderer);
        view.getBtSortPhotos().addActionListener(this::btSortPhotosActionPerformed);
        view.getBtChooseFolder().addActionListener(this::chooseFolderActionPerformed);
        view.getMenuItemMove().addActionListener(this::moveMenuItemActionPerformed);
        view.getMenuItemDelete().addActionListener(this::deleteMenuItemActionPerformed);
        Preferences prefs = Preferences.userRoot().node("com.moreno.sortpics");
        String lastPath = prefs.get("lastPath", null);
        if (lastPath != null) {
            model.setDirectory(new File(lastPath));
            this.view.getTfFolderToOrder().setText(lastPath);
        }
    }

    private void deleteMenuItemActionPerformed(ActionEvent actionEvent) {
        // create confirmation dialog
        // get list from view
        List selectedValuesList = view.getLsFilesToProcess().getSelectedValuesList();
        //create confirmation dialog
        int result = JOptionPane.showConfirmDialog(view.getMainPanel(), "Are you sure you want to delete the selected files?", "Delete files", JOptionPane.YES_NO_OPTION);
        // if confirmed
        if (result == JOptionPane.YES_OPTION) {
            // delete files
            for (Object selectedValue : selectedValuesList) {
                ImageFileData imageFileData = (ImageFileData) selectedValue;
                try {
                    imageFileData.remove();
                } catch (NoSuchFileException e) {
                    JOptionPane.showMessageDialog(view.getMainPanel(), "The file does not exists", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    // show dialog with error
                    JOptionPane.showMessageDialog(view.getMainPanel(), "Error deleting file: " + imageFileData.getOriginalFile().getName() + "- " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            model.getFiles().removeAll(selectedValuesList);
            Collections.sort(model.getFiles());
            // update view
            view.updateJList(model.getFiles());
        }
    }

    private void moveMenuItemActionPerformed(ActionEvent actionEvent) {
        // get list from view
        List selectedValuesList = view.getLsFilesToProcess().getSelectedValuesList();
        System.out.printf("selectedValuesList: %s%n", selectedValuesList);
    }

    private void btSortPhotosActionPerformed(java.awt.event.ActionEvent evt) {
        view.getBtSortPhotos().setEnabled(false);
        view.setProgressValue(0);
        System.out.println("btSortPhotosActionPerformed init");
        model.getFiles().clear();
        sortPhotosTask = new SortPhotosTask(this, model);
        sortPhotosTask.addPropertyChangeListener(
                (PropertyChangeEvent evt1) -> {
                    if ("state".equals(evt1.getPropertyName())) {
                        SwingWorker.StateValue state = (SwingWorker.StateValue) evt1.getNewValue();
                        if (state == SwingWorker.StateValue.DONE) {
                            this.view.getBtSortPhotos().setEnabled(true);
                        }
                    }
                    if ("progress".equals(evt1.getPropertyName())) {
                        Integer progressValue = (Integer) evt1.getNewValue();
                        this.view.setProgressValue(progressValue);
                    }
                });
        sortPhotosTask.execute();
    }

    private void chooseFolderActionPerformed(java.awt.event.ActionEvent evt) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (model.getDirectory() != null) {
            fileChooser.setCurrentDirectory(model.getDirectory());
        }
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            var directory = fileChooser.getSelectedFile();
            model.setDirectory(directory);
            view.getTfFolderToOrder().setText(directory.getAbsolutePath());
            var prefs = Preferences.userRoot().node("com.moreno.sortpics");
            prefs.put("lastPath", directory.getAbsolutePath());
            view.setProgressValue(0);
        }
    }

//    public void updateJList(ImageFileData imgFileData) {
//        this.view.updateJList(imgFileData);
//    }

    public void updateJList(List<ImageFileData> listImg) {
        this.view.updateJList(listImg);
    }

    public void setStateText(String stateText) {
        this.view.getLbInfo().setText(stateText);
    }

    public void sortJList() {
        this.view.sortJList();
    }

    public void initExecutorService() {
        imageLoaderWorker.initExecutorService();
    }

    public void shutdownExecutorService() {
        imageLoaderWorker.shutdownExecutorService();
    }
}
