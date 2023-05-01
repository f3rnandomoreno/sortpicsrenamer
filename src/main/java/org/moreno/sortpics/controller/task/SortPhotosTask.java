/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.controller.task;

import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;
import org.moreno.sortpics.controller.FirstPanelController;
import org.moreno.sortpics.model.FirstPanelModel;
import org.moreno.sortpics.model.ImageFileData;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class SortPhotosTask extends SwingWorker<Void, ImageFileData> {

    private final FirstPanelController controller;
    private final FirstPanelModel model;

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
        controller.initExecutorService();
        numFiles = fileList.size();
        int i = 0;
        for (File file : fileList) {
            try {
                this.publish(new ImageFileData(file, i++));
                model.getFilesAtomicToCreateThumbnail().get().push(file);
                setProgress(100 * i / numFiles);
            } catch (ParseException | ImageReadException | IOException ex) {
                // TODO manage error
                Logger.getLogger(SortPhotosTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private Map<ImageFileData, ImageFileData> searchDuplicatedFiles(List<ImageFileData> fileList) {
        Map<ImageFileData, ImageFileData> duplicateFiles = new HashMap<>();
        if (fileList.size() < 2) {
            return duplicateFiles;
        }
        for (int i = 0; i < fileList.size() - 1; i++) {
            if (fileList.get(i).equals(fileList.get(i + 1))) {
                duplicateFiles.put(fileList.get(i), fileList.get(i + 1));
            }
        }
        return duplicateFiles;
    }

    @Override
    protected void process(List<ImageFileData> chunks) {
        for (ImageFileData imgFileData : chunks) {
            this.model.getFiles().add(imgFileData);
        }
    }

    @Override
    protected void done() {
        controller.setStateText("Search finished.");
        controller.shutdownExecutorService();
        this.model.sort();
        Map duplicateFiles = searchDuplicatedFiles(this.model.getFiles());
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
                    if (isMediaFile(extension)) {
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
