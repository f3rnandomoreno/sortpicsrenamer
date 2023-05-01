/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;
import org.moreno.sortpics.rename.CameraTimestampName;
import org.moreno.sortpics.rename.JpegFileMetadata;
import org.moreno.sortpics.rename.NameUtils;
import org.moreno.sortpics.utils.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Objects;

import static org.moreno.sortpics.utils.ColorGenerator.getColorFromDate;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
@Builder
@Data
@AllArgsConstructor
public class ImageFileData implements Comparable<ImageFileData> {

    private String timeDateString;
    private long size;
    private String newPath;
    private String absolutePath;
    private File originalFile;
    private String fileName;
    private String newName;
    private boolean imageFile;
    private boolean file;
    private boolean videoFile;
    private String dayDateString;
    private String htmlColor;

    // create constructor of required fields originalFile, newPath, absolutePath
    public ImageFileData(File originalFile, String newPath, String absolutePath) {
        this.originalFile = originalFile;
        this.newPath = newPath;
        this.absolutePath = absolutePath;
    }


    public ImageFileData(File file, int number) throws IOException, ImageReadException, ParseException {
        this.originalFile = file;
        this.file = file.isFile();
        this.size = file.length();
        this.newPath = FilenameUtils.getFullPath(file.getCanonicalPath());
        this.newName = CameraTimestampName.getName(file.getAbsolutePath(), number);
        this.absolutePath = originalFile.getAbsolutePath();
        this.fileName = originalFile.getName();
        this.imageFile = NameUtils.isImage(file);
        this.videoFile = false;
        this.dayDateString = getDayDate();
        this.timeDateString = getTimeDate();
        this.htmlColor = getColorFromDate(dayDateString);
    }

    public void moveToNewName() throws IOException {
        if (!newNameIsDifferent()) {
            var newFullPath = newPath + newName;
            var path = Files.move(Path.of(originalFile.getCanonicalPath()), Path.of(newFullPath), StandardCopyOption.REPLACE_EXISTING);
            setOriginalFile(new File(newFullPath));
            Log.debug("moved file: " + path);
        }
    }

    public boolean newNameIsDifferent() {
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

    public String getDayDate() {
        return JpegFileMetadata.getDateFromFileName(newName).getDateString();
    }

    public String getTimeDate() {
        return JpegFileMetadata.getDateFromFileName(newName).getTimeString();
    }

    public void setOriginalFile(File originalFile) {
        this.originalFile = originalFile;
        this.absolutePath = originalFile.getAbsolutePath();
        this.fileName = originalFile.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageFileData that = (ImageFileData) o;

        try {
            return size == that.size && timeDateString.equals(that.timeDateString) && dayDateString.equals(that.dayDateString) && FileUtils.contentEquals(originalFile, that.originalFile);
        } catch (IOException e) {
            // log error
            Log.debug("error comparing files: " + e.getMessage());
            return false;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(size, dayDateString);
    }

    @Override
    public String toString() {
        return "ImageFileData{" +
                "absolutePath='" + absolutePath + '\'' +
                '}';
    }

    @Override
    public int compareTo(ImageFileData o) {
        return this.newName.compareTo(o.newName);
    }
}
