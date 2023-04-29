/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.moreno.sortpics.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.ImageIcon;

import lombok.Data;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
@Data
public class FirstPanelModel {
    private File directory = new File("/Users/fmoreno/Google Drive/Mi unidad/Fotos/Compartidas");
    private List<ImageFileData> files = new ArrayList<>();
    private final AtomicReference<ConcurrentMap<String, ImageIcon>> thumbnailCache = new AtomicReference(new ConcurrentHashMap<>());
    private final AtomicReference<ConcurrentLinkedDeque<File>> filesToCreateThumbnail = new AtomicReference(new ConcurrentLinkedDeque<>());

    public FirstPanelModel() {
    }

    public ConcurrentMap<String, ImageIcon> getThumbnailCache() {
        return thumbnailCache.get();
    }

    public ConcurrentLinkedDeque<File> getFilesToCreateThumbnail() {
        return filesToCreateThumbnail.get();
    }

    public void sort() {
        Collections.sort(files);
    }

    public AtomicReference<ConcurrentLinkedDeque<File>> getFilesAtomicToCreateThumbnail() {
        return filesToCreateThumbnail;
    }
}
