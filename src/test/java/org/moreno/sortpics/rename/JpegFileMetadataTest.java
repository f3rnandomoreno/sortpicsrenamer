/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.rename;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.moreno.sortpics.TestCommon;
import org.moreno.sortpics.task.SortPhotosTaskTest;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class JpegFileMetadataTest extends TestCommon {

    /**
     * This is a test case method to verify the proper functioning of the 'getName' method in the 'CameraTimestampName' class
     * for JPEG files that do not have a date in their name and have not been renamed.
     *
     * <p>
     * The test method works as follows:
     * <ul>
     * <li>Initializes a sort task with a specific directory of files.
     * <li>Lists all the media files in the specified directory.
     * <li>Filters out the files that do not meet the following criteria:
     *     <ul>
     *     <li>Is not named following the camera timestamp naming convention.
     *     <li>Is a JPEG file.
     *     <li>Does not have a date in its filename.
     *     </ul>
     * <li>Asserts that the new name, created by the 'getName' method, is as expected for the first file in the filtered list.
     * </ul>
     *
     * <p>
     * If any exception occurs during the process, it gets logged.
     * </p>
     *
     * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
     */
    @Test
    public void testGetNameJPEGWithoutDateInNameNoRenamed() {

        initSortPhotosTask(PATH_FILES_TO_TEST_ORIGINALS);

        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());

        var filesToProcess = files.stream().filter((file) -> {
            String path = file.getAbsolutePath();
            return !JpegFileMetadata.isCameraTimestampNaming(path)
                    && isJPEG(path) && NameUtils.dateTimeOnFileName(path).isEmpty();
        }).toList();
        System.out.println("Files to Process: " + filesToProcess);
        File file = filesToProcess.get(0);
        try {
            String newName = CameraTimestampName.getName(file.getAbsolutePath(), 1);
            Assertions.assertThat(newName).isEqualTo("(2022-05-28)[20.42.52]-Apple_iPhone X[1](IMG_5639).JPG");
            System.out.println("new name:" + CameraTimestampName.getName(file.getAbsolutePath(), 1));
        } catch (Exception ex) {
            Logger.getLogger(SortPhotosTaskTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testGetNameVideosWithoutDateInNameNoRenamed() {


        initSortPhotosTask(PATH_FILES_TO_TEST_ORIGINALS);

        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());
        setLastModifiedDatesAllFiles(files);
        var filesToProcess = files.stream().filter((file) -> {
            String path = file.getAbsolutePath();
            return !JpegFileMetadata.isCameraTimestampNaming(path)
                    && !isJPEG(path) && NameUtils.dateTimeOnFileName(path).isEmpty();
        }).toList();
        System.out.println("Files to Process: " + filesToProcess);
        List<String> newNames = filesToProcess.stream().map((file) -> {
            try {
                return CameraTimestampName.getName(file.getAbsolutePath(), 1);
            } catch (Exception ex) {
                return "";
            }
        }).toList();
        Assertions.assertThat(newNames).containsExactlyElementsOf(
                List.of(
                        "(2023-04-12)[11.00.00]-LastModifiedDate[1](IMG_2796).MOV",
                        "(2023-04-12)[11.00.00]-LastModifiedDate[1](Que es GetOrderPics).mp4"
                ));
        System.out.println("------------------------------------");
        System.out.println("newNames:" + newNames);
    }

    @Test
    public void testGetNameWithDateInNameNoRenamed() {

        initSortPhotosTask(PATH_FILES_TO_TEST_ORIGINALS);

        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());

        var filesToProcess = files.stream().filter((file) -> {
            String path = file.getAbsolutePath();
            return !JpegFileMetadata.isCameraTimestampNaming(path)
                    && NameUtils.dateTimeOnFileName(path).isPresent();
        }).toList();
        System.out.println("Files to Process: " + filesToProcess);
        List<String> newNames = filesToProcess.stream().map((file) -> {
            try {
                return CameraTimestampName.getName(file.getAbsolutePath(), 1);
            } catch (Exception ex) {
                return "";
            }
        }).toList();
        Assertions.assertThat(newNames).containsExactlyElementsOf(
                List.of(
                        "(2020-04-27)[14.46.54]-FromFileName[1](Screenshot_2020_04_27_14_46_54).png",
                        "(2021-02-13)[18.03.23]-FromFileName[1](Screenshot_2021_02_13_18_03_23_364_com.facebook.orca).jpg",
                        "(2022-03-20)[19.59.57]-FromFileName[1](VID_20220320_195957).mp4"
                ));
        System.out.println("------------------------------------");
        System.out.println("newNames:" + newNames);
    }

}
