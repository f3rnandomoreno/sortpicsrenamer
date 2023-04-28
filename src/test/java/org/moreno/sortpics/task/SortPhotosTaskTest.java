package org.moreno.sortpics.task;

import lombok.NoArgsConstructor;
import org.apache.maven.surefire.shared.io.FileUtils;
import org.apache.sanselan.ImageReadException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreno.sortpics.TestCommon;
import org.moreno.sortpics.model.ImageFileData;
import org.moreno.sortpics.rename.JpegFileMetadata;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author Fernando Moreno Ruiz
 */
@NoArgsConstructor
public class SortPhotosTaskTest extends TestCommon {

    @BeforeEach
    public void setUp() throws IOException {
        copyFilesToTest();
    }

    @AfterEach
    public void clean() throws IOException {
        FileUtils.deleteDirectory(new File(PATH_FILES_TESTING));
    }

    @Test
    public void testCopyFilesToDoTests() {
        firstPanelModel.setDirectory(new File(PATH_FILES_TESTING));
        initSortPhotosTask(PATH_FILES_TESTING);
        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());
        Assertions.assertThat(files).hasSize(NUMBER_OF_FILES);
    }

    @Test
    public void testGetAllJpgFiles_shouldReturnEmptyList() {
        firstPanelModel.setDirectory(new File(PATH_EMPTY));
        initSortPhotosTask(PATH_EMPTY);
        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());
        Assertions.assertThat(firstPanelModel.getDirectory().exists()).isTrue();
        Assertions.assertThat(files).hasSize(0);
    }

    @Test
    public void testGetAllJpgFiles_shouldReturnNotEmptyList() {
        firstPanelModel.setDirectory(new File(PATH_FILES_TESTING));
        initSortPhotosTask(PATH_FILES_TESTING);
        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());
        Assertions.assertThat(firstPanelModel.getDirectory().exists()).isTrue();
        Assertions.assertThat(files).hasSize(NUMBER_OF_FILES);
    }

    @Test
    public void testGetNotRenamedFiles() {
        initSortPhotosTask(PATH_FILES_TESTING);

        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());

        var filesToProcess = files.stream().filter((file) -> !JpegFileMetadata.isCameraTimestampNaming(file.getAbsolutePath())).toList();

        Assertions.assertThat(filesToProcess).hasSize(NUMBER_OF_FILES_NO_RENAMED);
    }

    @Test
    public void testGetRenamedFiles() {
        initSortPhotosTask(PATH_FILES_TESTING);

        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());

        var filesNoToProcess = files.stream().filter((file) -> {
            return JpegFileMetadata.isCameraTimestampNaming(file.getAbsolutePath());
        }).toList();

        Assertions.assertThat(filesNoToProcess).hasSize(NUMBER_OF_FILES_RENAMED);
    }

    @Test
    public void testRenameFiles() throws IOException, InterruptedException, ImageReadException, ParseException {
        initSortPhotosTask(PATH_FILES_TESTING);

        List<File> files = sut.listMediaFiles(firstPanelModel.getDirectory());

        setLastModifiedDatesAllFiles(files);

        System.out.println("files:" + files);
        Thread.sleep(1000);
        List<File> list = sut.listMediaFiles(firstPanelModel.getDirectory());
        int i = 0;
        for (File file : list) {
            new ImageFileData(file, i++).moveToNewName();
        }

        Assertions.assertThat(new File(PATH_FILES_TESTING + "/(2021-06-21)[11.48.17]-LastModifiedDate[0](signal_2021_06_21_114415_002).jpeg").exists()).isTrue();
        Assertions.assertThat(new File(PATH_FILES_TESTING + "/(2023-04-12)[11.00.00]-LastModifiedDate[1](IMG_2796).MOV").exists()).isTrue();
        Assertions.assertThat(new File(PATH_FILES_TESTING + "/(2022-05-28)[20.42.52]-Apple_iPhone X[2](IMG_5639).JPG").exists()).isTrue();
        Assertions.assertThat(new File(PATH_FILES_TESTING + "/(2023-04-12)[11.00.00]-LastModifiedDate[3](Que es GetOrderPics).mp4").exists()).isTrue();
        Assertions.assertThat(new File(PATH_FILES_TESTING + "/(2020-04-27)[14.46.54]-FromFileName[4](Screenshot_2020_04_27_14_46_54).png").exists()).isTrue();
        Assertions.assertThat(new File(PATH_FILES_TESTING + "/(2021-02-13)[18.03.23]-FromFileName[5](Screenshot_2021_02_13_18_03_23_364_com.facebook.orca).jpg").exists()).isTrue();
        Assertions.assertThat(new File(PATH_FILES_TESTING + "/(2022-03-20)[19.59.57]-FromFileName[6](VID_20220320_195957).mp4").exists()).isTrue();

    }

}
