/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.rename;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.moreno.sortpics.TestCommon;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Fernando Moreno Ruiz
 */
public class NameUtilsTest extends TestCommon {

    /**
     * This test case method verifies the correctness of filenames with dates embedded in them.
     * It initializes a sort task, lists all the media files in a specific directory, and extracts dates from the filenames.
     * It also ensures that the extracted dates are correct by asserting the expected number of files and their corresponding dates.
     *
     * <p>
     * The test is designed to:
     * <ul>
     * <li>Iterate over the list of files in the directory
     * <li>Extract the date from the filename if present and add it to a map with the file as the key
     * <li>Assert that the map has a specific size, here 4
     * <li>Assert that the map contains specific entries with their respective dates
     * </ul>
     */
    @Test
    public void testDateInFilename() {
        String directoryPath = PATH_FILES_TO_TEST_ORIGINALS;
        initSortPhotosTask(directoryPath);
        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());
        Map<File, LocalDateTime> mapFileDate = new HashMap<>();
        files.forEach(file -> {
            Optional<LocalDateTime> date = NameUtils.dateTimeOnFileName(file.getName());
            if (date.isPresent()) {
                mapFileDate.put(file, date.get());
            }
        });

        Assertions.assertThat(mapFileDate).hasSize(4);

        // Check that the map contains the expected entries.
        Assertions.assertThat(mapFileDate)
                .containsEntry(new File(directoryPath + "/Screenshot_2021_02_13_18_03_23_364_com.facebook.orca.jpg"),
                        LocalDateTime.of(2021, 2, 13, 18, 3, 23))
                .containsEntry(new File(directoryPath + "/Screenshot_2020_04_27_14_46_54.png"), LocalDateTime.of(2020, 4, 27, 14, 46, 54))
                .containsEntry(new File(directoryPath + "/VID_20220320_195957.mp4"), LocalDateTime.of(2022, 3, 20, 19, 59, 57));

    }

    @Test
    void testIsImage() {
        String directoryPath = PATH_FILES_TO_TEST_ORIGINALS;
        initSortPhotosTask(directoryPath);
        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());
        Map<File, Boolean> mapFileDate = new HashMap<>();
        files.forEach(file -> {
            boolean isImage = NameUtils.isImage(file.getAbsolutePath());
            mapFileDate.put(file, isImage);
        });

        // Check that the map contains the expected entries.
        Assertions.assertThat(mapFileDate)
                .containsEntry(new File(directoryPath + "/(2021-06-21)[11.48.17]-LastModifiedDate[0](signal_2021_06_21_114415_002).jpeg"), true)
                .containsEntry(new File(directoryPath + "/IMG_2796.MOV"), false)
                .containsEntry(new File(directoryPath + "/IMG_5639.JPG"), true)
                .containsEntry(new File(directoryPath + "/Screenshot_2021_02_13_18_03_23_364_com.facebook.orca.jpg"), true)
                .containsEntry(new File(directoryPath + "/Que es GetOrderPics.mp4"), false)
                .containsEntry(new File(directoryPath + "/Screenshot_2020_04_27_14_46_54.png"), true)
                .containsEntry(new File(directoryPath + "/VID_20220320_195957.mp4"), false);
    }

}
