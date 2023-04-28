package org.moreno.sortpics;

import org.apache.maven.surefire.shared.io.FileUtils;
import org.apache.maven.surefire.shared.io.FilenameUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moreno.sortpics.controller.FirstPanelController;
import org.moreno.sortpics.controller.task.SortPhotosTask;
import org.moreno.sortpics.gui.FirstPanel;
import org.moreno.sortpics.model.FirstPanelModel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
@ExtendWith(MockitoExtension.class)
public class TestCommon {
    // don't delete them
    protected static final String PATH_FILES_TO_TEST_ORIGINALS = "./tests/files_to_process";

    // directory to store the file that are going to be processing
    protected static final String PATH_FILES_TESTING = "./tests/files_testing";
    protected static final String PATH_EMPTY = "./tests/empty";
    protected static final int NUMBER_OF_FILES_RENAMED = 1;
    protected static final int NUMBER_OF_FILES_NO_RENAMED = 6;
    protected static final int NUMBER_OF_FILES = NUMBER_OF_FILES_RENAMED + NUMBER_OF_FILES_NO_RENAMED;

    protected FirstPanelModel firstPanelModel = new FirstPanelModel();
    protected FirstPanel view = new FirstPanel();
    protected FirstPanelController firstPanelController = new FirstPanelController(view, firstPanelModel);
    protected SortPhotosTask sut;

    protected static void setLastModifiedDatesAllFiles(List<File> files) {
        files.stream().forEach((file) -> {
            try {
                LocalDateTime ldt = LocalDateTime.of(2023, 4, 12, 11, 0, 0);
                ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
                long millis = zdt.toInstant().toEpochMilli();
                file.setLastModified(millis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    protected boolean isJPEG(String path) {
        String extension = FilenameUtils.getExtension(path);
        return extension.toLowerCase().contains("jpeg") || extension.toLowerCase().contains("jpg");
    }

    protected void copyFilesToTest() throws IOException {
        File source = new File(PATH_FILES_TO_TEST_ORIGINALS);
        File testPath = new File(PATH_FILES_TESTING);
        FileUtils.copyDirectory(source, testPath);
    }

    protected void initSortPhotosTask(String path) {
        firstPanelModel.setDirectory(new File(path));
        this.sut = new SortPhotosTask(firstPanelController, firstPanelModel);
    }
}
