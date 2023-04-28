/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.gui;

import org.apache.maven.surefire.shared.io.FileUtils;
import org.apache.sanselan.ImageReadException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreno.sortpics.TestCommon;
import org.moreno.sortpics.model.ImageFileData;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class ImageFileDataTest extends TestCommon {
    ImageFileData sut;

    @BeforeEach
    void setUp() throws IOException {
        copyFilesToTest();
    }

    @AfterEach
    void clean() throws IOException {
        FileUtils.deleteDirectory(new File(PATH_FILES_TESTING));
    }

    @Test
    void moveToNoCameraStampNameTest() throws IOException, ImageReadException, ParseException {
        File file = new File(PATH_FILES_TESTING + "/(2021-06-21)[11.48.17]-LastModifiedDate[0](signal_2021_06_21_114415_002).jpeg");
        sut = new ImageFileData(file, 0);
        sut.moveToNoCameraStampName();
        Assertions.assertThat(new File(PATH_FILES_TESTING + "/signal_2021_06_21_114415_002.jpeg").exists()).isTrue();
        System.out.println("sut:" + sut);
    }


}
