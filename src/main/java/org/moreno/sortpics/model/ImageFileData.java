/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;
import org.moreno.sortpics.rename.CameraTimestampName;
import org.moreno.sortpics.rename.JpegFileMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
@Builder
@Data
@RequiredArgsConstructor
public class ImageFileData implements Comparable<ImageFileData> {

    private final File originalFile;
    private final String newPath;
    private String newName;

    public ImageFileData(File file, String string, String string1) {
        // show trace of enter here
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    public ImageFileData(File file, int number) throws IOException, ImageReadException, ParseException {
        this.originalFile = file;
        this.newPath = FilenameUtils.getFullPath(file.getCanonicalPath());
        this.newName = CameraTimestampName.getName(file.getAbsolutePath(), number);
    }

    public void moveToNewName() throws IOException {
        if (!isAlreadyRenamed()) {
            var path = Files.move(Path.of(originalFile.getCanonicalPath()), Path.of(newPath + newName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("moved file: " + path);
        }
    }

    public void moveToNoCameraStampName() throws IOException {
        if (isAlreadyRenamed()) {
            var extension = FilenameUtils.getExtension(originalFile.getName());
            var data = JpegFileMetadata.getInfoData(originalFile.getName());
            var path = Files.move(Path.of(originalFile.getCanonicalPath()), Path.of(newPath + data + "." + extension), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("moved file: " + path);
        }
    }

    public void remove() throws IOException {
        Path pathFile = Path.of(originalFile.getCanonicalPath());
        Files.delete(pathFile);
        System.out.println("remove file: " + pathFile);
    }

    public boolean isAlreadyRenamed() {
        return JpegFileMetadata.isCameraTimestampNaming(originalFile.getAbsolutePath());
    }

    @Override
    public int compareTo(ImageFileData o) {
        return this.newName.compareTo(o.newName);
    }
}
