/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.controller.task;

import org.apache.commons.io.FilenameUtils;
import org.moreno.sortpics.controller.FirstPanelController;
import org.moreno.sortpics.gui.DuplicatesWindow;
import org.moreno.sortpics.model.FirstPanelModel;
import org.moreno.sortpics.model.ImageFileData;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class SortPhotosTask extends SwingWorker<Void, ImageFileData> {

    private final FirstPanelController controller;
    private final FirstPanelModel model;
    private final List<ErrorHandler> errorHandlers = new ArrayList<>();

    private int numFiles = 0;

    public SortPhotosTask(FirstPanelController controller, FirstPanelModel model) {
        this.controller = controller;
        this.model = model;
    }

    public static boolean isMediaFile(String extension) {
        String[] mediaExtensions = {
                "jpg", "jpeg", "png", "gif", "bmp", "tiff", "mp4", "avi", "mkv", "wmv", "mov", "3gp"
        };
        for (String mediaExtension : mediaExtensions) {
            if (mediaExtension.equals(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Void doInBackground() {
        List<File> fileList = listMediaFiles(model.getDirectory());
        //imageLoaderWorker.get().initExecutorService();
        numFiles = fileList.size();
        controller.initExecutorService();
        int i = 0;
        for (File file : fileList) {
            try {
                if (file.isFile()) {
                    ImageFileData imgFileData = new ImageFileData(file, i++);
                    this.publish(imgFileData);
                    model.getFilesAtomicToCreateThumbnail().get().push(file);
                }
                setProgress(100 * i / numFiles);
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                errorHandlers.add(new ErrorHandler(file, ex.getMessage()));
                Logger.getLogger(SortPhotosTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private Map<ImageFileData, List<ImageFileData>> searchDuplicatedFiles(List<ImageFileData> fileList) {
        Set<Integer> visitedPositions = new HashSet<>();
        Map<ImageFileData, List<ImageFileData>> duplicateFiles = new HashMap<>();
        if (fileList.size() < 2) {
            return duplicateFiles;
        }
        for (int i = 0; i < fileList.size() - 1; i++) {
            if (visitedPositions.contains(i)) {
                continue;
            }
            if (fileList.get(i).equals(fileList.get(i + 1))) {
                List<ImageFileData> duplicatedFiles = new ArrayList<>();
                duplicatedFiles.add(fileList.get(i + 1));
                duplicateFiles.put(fileList.get(i), duplicatedFiles);
                visitedPositions.add(i + 1);
                int j = 1;
                while (i + j < fileList.size() - 1 && fileList.get(i).equals(fileList.get(i + j + 1))) {
                    duplicatedFiles.add(fileList.get(i + j + 1));
                    visitedPositions.add(i + j + 1);
                    j++;
                }
            }
        }
        return duplicateFiles;
    }

    @Override
    protected void process(List<ImageFileData> chunks) {
        for (ImageFileData imgFileData : chunks) {
            System.out.println("Processing file: " + imgFileData.getFileName());
            this.model.getFiles().add(imgFileData);
            model.getFilesAtomicToCreateThumbnail().get().push(imgFileData.getOriginalFile());
        }
    }

    @Override
    protected void done() {
        controller.setStateText("Search finished.");
        controller.shutdownExecutorService();
        this.model.sort();
        Map duplicateFiles = searchDuplicatedFiles(this.model.getFiles());
        if (duplicateFiles.size() > 0) {
            DuplicatesWindow duplicatesWindow = new DuplicatesWindow(duplicateFiles);
            duplicatesWindow.setVisible(true);

        }
        // show error messages
        if (errorHandlers.size() > 0) {
            StringBuilder errorMessage = new StringBuilder();
            for (ErrorHandler errorHandler : errorHandlers) {
                errorMessage.append(errorHandler.getFile().getName()).append(": ").append(errorHandler.getMessage()).append("\n");
            }
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("Duplicated files: " + duplicateFiles);
        controller.updateJList(this.model.getFiles());
        this.controller.activateBtRenameFiles();

    }

    public List<File> listMediaFiles(File directory) {
        List<File> mediaFilesList = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            controller.setStateText("Looking for files to sort in: " + directory.getAbsolutePath());
            for (File file : files) {
                if (file.isDirectory()) {
                    mediaFilesList.addAll(listMediaFiles(file));
                } else {
                    String extension = FilenameUtils.getExtension(file.getName());

                    // filename does not start with . and is a media file
                    if (isMediaFile(extension) && file.getName().substring(0, 1).compareTo(".") != 0) {
                        mediaFilesList.add(file);
                    }
                }
            }
        }
        return mediaFilesList;
    }

//    public JList<ImageFileData> getListImages() {
//        return parent.getListFilesToProcess();
//    }
}
