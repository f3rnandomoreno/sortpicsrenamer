package org.moreno.sortpics.controller;

import org.apache.commons.io.FileUtils;
import org.moreno.sortpics.controller.task.ImageLoaderWorker;
import org.moreno.sortpics.controller.task.SortPhotosTask;
import org.moreno.sortpics.gui.FolderAnalyzer;
import org.moreno.sortpics.gui.ThumbnailListCellRenderer;
import org.moreno.sortpics.model.FirstPanelModel;
import org.moreno.sortpics.model.ImageFileData;
import org.moreno.sortpics.rename.CameraTimestampName;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import static org.moreno.sortpics.gui.EditTextDialog.showEditTextDialog;

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
        // buttons
        view.getBtRenameFiles().setEnabled(false);
        view.getBtRenameFiles().addActionListener(this::btRenameFilesActionPerformed);
        view.getBtSortPhotos().addActionListener(this::btSortPhotosActionPerformed);
        view.getBtChooseFolder().addActionListener(this::chooseFolderActionPerformed);

        // menu items
        view.getMenuItemRemoveCameraTimestamp().addActionListener(this::removeCameraTimestampMenuItemActionPerformed);
        view.getMenuItemDelete().addActionListener(this::deleteMenuItemActionPerformed);
        view.getMenuItemRename().addActionListener(this::renameMenuItemActionPerformed);
        view.getMenuItemOpenFolder().addActionListener(this::openFolderMenuItemActionPerformed);
        view.getMenuItemRenameToNewName().addActionListener(this::renameToNewNameMenuItemActionPerformed);
        view.getMenuItemGetDateFromData().addActionListener(this::getDateFromDataMenuItemActionPerformed);
        view.getMenuItemCopyToSelectedFolder().addActionListener(this::copyToSelectedFolderMenuItemActionPerformed);

        // text fields
        view.getTfFolderToOrder().getDocument().addDocumentListener(new TextFieldChangeListener(this::setFolderToOrderOnDocumentChange));
        // get preferences and init text field
        Preferences prefs = Preferences.userRoot().node("com.moreno.sortpics");
        String lastPath = prefs.get("lastPath", null);
        if (lastPath != null) {
            model.setDirectory(new File(lastPath));
            this.view.getTfFolderToOrder().setText(lastPath);
        }

        // list
        ThumbnailListCellRenderer renderer = new ThumbnailListCellRenderer(model);
        view.setCellRenderer(renderer);
        // add mouse listener to list
        view.getLsFilesToProcess().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    ImageFileData image = (ImageFileData) view.getLsFilesToProcess().getSelectedValue();
                    try {
                        // open file with default application
                        Desktop.getDesktop().open(image.getOriginalFile());
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(view.getMainPanel(), "Error renaming file: " + image.getFileName() + "- " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    //view.getLsFilesToProcess().updateUI();
                }
            }
        });


    }

    private void copyToSelectedFolderMenuItemActionPerformed(ActionEvent actionEvent) {
        // get list from view
        List selectedValuesList = view.getLsFilesToProcess().getSelectedValuesList();
        //create confirmation dialog
        int result = JOptionPane.showConfirmDialog(view.getMainPanel(), "Are you sure you want to copy the selected files?", "Copy files", JOptionPane.YES_NO_OPTION);
        // if confirmed
        if (result == JOptionPane.YES_OPTION) {
            // copy files
            for (Object selectedValue : selectedValuesList) {
                ImageFileData imageFileData = (ImageFileData) selectedValue;
                try {
                    FileUtils.copyFile(imageFileData.getOriginalFile(), new File(model.getDirectory() + File.separator + FirstPanelModel.SELECTED_DIRECTORY + File.separator + imageFileData.getFileName()));
                    //imageFileData.getOriginalFile().copyTo(model.getDirectory() + File.separator + FirstPanelModel.SELECTED_DIRECTORY);
                } catch (IOException e) {
                    // show dialog with error
                    JOptionPane.showMessageDialog(view.getMainPanel(), "Error copying file: " + imageFileData.getFileName() + "- " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            // show confirmation dialog
            JOptionPane.showMessageDialog(view.getMainPanel(), "Files copied successfully", "Copy files", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openFolderMenuItemActionPerformed(ActionEvent actionEvent) {
        ImageFileData image = (ImageFileData) view.getLsFilesToProcess().getSelectedValue();
        try {
            // open directory with default application
            Desktop.getDesktop().open(image.getOriginalFile().getParentFile());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Error renaming file: " + image.getFileName() + "- " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setFolderToOrderOnDocumentChange(DocumentEvent documentEvent) {
        System.out.println("setFolderToOrderOnDocumentChange");
        String path = view.getTfFolderToOrder().getText();
        model.setDirectory(new File(path));
        Preferences prefs = Preferences.userRoot().node("com.moreno.sortpics");
        prefs.put("lastPath", path);
    }

    private void getDateFromDataMenuItemActionPerformed(ActionEvent actionEvent) {
        List selectedValuesList = view.getLsFilesToProcess().getSelectedValuesList();
        selectedValuesList.forEach(img -> {
            ImageFileData image = (ImageFileData) img;
            image.setNewName(CameraTimestampName.renameWithDateFromData(image.getNewName()));
        });
        view.getLsFilesToProcess().updateUI();
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
            // Eliminar elementos seleccionados del modelo
            DefaultListModel<ImageFileData> listModel = (DefaultListModel<ImageFileData>) view.getLsFilesToProcess().getModel();
            for (Object imageFileData : selectedValuesList) {
                listModel.removeElement(imageFileData);
            }
            // update view ui
            view.getLsFilesToProcess().updateUI();
        }
    }

    private void removeCameraTimestampMenuItemActionPerformed(ActionEvent actionEvent) {
        // get list from view
        List selectedValuesList = view.getLsFilesToProcess().getSelectedValuesList();
        for (Object selectedValue : selectedValuesList) {
            ImageFileData imageFileData = (ImageFileData) selectedValue;
            try {
                imageFileData.moveToNoCameraStampName();
                // update selected item
                view.getLsFilesToProcess().updateUI();
            } catch (IOException e) {
                // show dialog with error
                JOptionPane.showMessageDialog(view.getMainPanel(), "Error renaming file: " + imageFileData.getFileName() + "- " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        System.out.printf("selectedValuesList: %s%n", selectedValuesList);
    }


    private void renameToNewNameMenuItemActionPerformed(ActionEvent actionEvent) {
        List selectedValuesList = view.getLsFilesToProcess().getSelectedValuesList();
        selectedValuesList.forEach(img -> {
            ImageFileData image = (ImageFileData) img;
            try {
                image.moveToNewName();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getMainPanel(), "Error renaming file: " + image.getFileName() + "- " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        view.getLsFilesToProcess().updateUI();
    }

    private void btRenameFilesActionPerformed(ActionEvent actionEvent) {
        var model = view.getLsFilesToProcess().getModel();
        // create a new thread to rename files including view update
        new Thread(() -> renameFiles(model)).start();
        view.getLbInfo().setText("Renaming files...");

    }

    private void renameFiles(ListModel model) {
        initExecutorService();
        view.setProgressValue(0);
        view.getPbOrderProgress().setMaximum(model.getSize());
        IntStream.range(0, model.getSize()).forEach(ic -> {
            ImageFileData img = (ImageFileData) model.getElementAt(ic);
            try {
                // update progress
                img.moveToNewName();
                this.model.getFilesToCreateThumbnail().add(img.getOriginalFile());
                view.setProgressValue(ic);
            } catch (NoSuchFileException ex) {
                // show error message
                JOptionPane.showMessageDialog(view.getMainPanel(), "File not found: " + img.getFileName(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                // show error message
                JOptionPane.showMessageDialog(view.getMainPanel(), "Error renaming file: " + img.getFileName() + "- " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        shutdownExecutorService();
        view.getLsFilesToProcess().updateUI();
        view.getLbInfo().setText("Files renamed");
    }

    private void renameMenuItemActionPerformed(ActionEvent actionEvent) {
        ImageFileData selectedItem = (ImageFileData) view.getLsFilesToProcess().getSelectedValue();
        if (selectedItem != null) {
            var newName = showEditTextDialog(null, selectedItem.getNewName());
            selectedItem.setNewName(newName);
            sortJList();
            view.getLsFilesToProcess().updateUI();

        }
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

    public void activateBtRenameFiles() {
        this.view.getBtRenameFiles().setEnabled(true);
    }


}
