/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;
import org.moreno.sortpics.rename.CameraTimestampName;
import org.moreno.sortpics.rename.JpegFileMetadata;
import org.moreno.sortpics.rename.NameUtils;
import static org.moreno.sortpics.utils.ColorGenerator.getColorFromDate;
import org.moreno.sortpics.utils.Log;

/**
 *
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ImageFileData implements Comparable<ImageFileData> {

    private final File originalFile;
    private String fileName;
    private String newName;
    private final String newPath;
    private final String absolutePath;
    private boolean imageFile;
    private boolean file;
    private boolean videoFile;
    private String timeDateString;
    private String htmlColor;

    public ImageFileData(File file, int number) throws IOException, ImageReadException, ParseException {
        this.originalFile = file;
        this.file = file.isFile();
        this.newPath = FilenameUtils.getFullPath(file.getCanonicalPath());
        this.newName = CameraTimestampName.getName(file.getAbsolutePath(), number);
        this.absolutePath = originalFile.getAbsolutePath();
        this.fileName = originalFile.getName();
        this.imageFile = NameUtils.isImage(file);
        this.videoFile = false;
        this.timeDateString = getDayDate();
        this.htmlColor = getColorFromDate(timeDateString);
    }

    public void moveToNewName() throws IOException {
        if (!newNameIsDifferent()) {
            var path = Files.move(Path.of(originalFile.getCanonicalPath()), Path.of(newPath + newName), StandardCopyOption.REPLACE_EXISTING);
            Log.debug("moved file: " + path);
        }
    }

    public boolean newNameIsDifferent(){
        return newName.equals(fileName);
    }

    public void moveToNoCameraStampName() throws IOException {
        Log.debug("enter moveToCameraStampName: " + originalFile.getName());
        if (isAlreadyRenamed()) {
            var extension = FilenameUtils.getExtension(originalFile.getName());
            var data = JpegFileMetadata.getInfoData(originalFile.getName());
            var path = Files.move(Path.of(originalFile.getCanonicalPath()), Path.of(newPath + data + "." + extension), StandardCopyOption.REPLACE_EXISTING);
            Log.debug("moved file: " + path);
        }
    }

    public void remove() throws IOException {
        Path pathFile = Path.of(originalFile.getCanonicalPath());
        Files.delete(pathFile);
        Log.debug("remove file: " + pathFile);
    }

    public boolean isAlreadyRenamed() {
        return JpegFileMetadata.isCameraTimestampNaming(originalFile.getAbsolutePath());
    }

    public String getDayDate(){
        return JpegFileMetadata.getDateFromFileName(newName).getDateString();
    }
    public String getTimeDate(){
        return JpegFileMetadata.getDateFromFileName(newName).getTimeString();
    }

    @Override
    public int compareTo(ImageFileData o) {
        return this.newName.compareTo(o.newName);
    }
}
