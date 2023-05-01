/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.controller.task;

import lombok.Getter;
import org.moreno.sortpics.model.FirstPanelModel;
import org.moreno.sortpics.rename.NameUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
@Getter
public class ImageLoaderWorker extends SwingWorker<Void, ImageIcon> {

    final FirstPanelModel model;
    private final int thumbnailSize = 128; // Thumbnail size in pixels
    private final ImageIcon loadingImageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Loading_icon.jpg"));
    volatile boolean programShutdown = false;
    private ImageIcon noLoadImageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Loading_icon.jpg"));
    private volatile int numberOfThreads = 2;
    private ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    public ImageLoaderWorker(FirstPanelModel model) {
        this.model = model;
    }

    public static BufferedImage readScaledImage(File imageFile, int thumbnailSize) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(imageFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    ImageReadParam param = reader.getDefaultReadParam();
                    int originalWidth = reader.getWidth(0);
                    int originalHeight = reader.getHeight(0);
                    int scaleFactor = Math.max(Math.max(originalWidth / thumbnailSize, originalHeight / thumbnailSize), 1);
                    param.setSourceSubsampling(scaleFactor, scaleFactor, 0, 0);
                    return reader.read(0, param);
                } finally {
                    reader.dispose();
                }
            }
        }
        throw new IOException("No ImageReader found for the specified file: " + imageFile.getPath());
    }

    @Override
    protected Void doInBackground() throws Exception {
        var listFiles = model.getFilesToCreateThumbnail();
        var thumbnailCache = model.getThumbnailCache();
        while (true) {
            Thread.sleep(100);
            if (!listFiles.isEmpty()) {
                executorService.execute(() -> {
                    if (!listFiles.isEmpty()) {
                        File imageFile = listFiles.pop();

                        if (!thumbnailCache.containsKey(imageFile.getAbsolutePath()) && NameUtils.isImage(imageFile)) {
                            ImageIcon thumbnailIcon;
                            try {
                                thumbnailIcon = new ImageIcon(readScaledImage(imageFile, thumbnailSize * 3));
                            } catch (IOException ex) {
                                thumbnailIcon = noLoadImageIcon;
                                System.out.println("Error:" + ex.getMessage());
                            }

                            thumbnailCache.put(imageFile.getAbsolutePath(), thumbnailIcon);
                        }
                    }
                });
            } else if (programShutdown) {
                executorService.shutdown();
            }
        }
    }

    public void shutdownExecutorService() {
        programShutdown = true;
    }

    public void initExecutorService() {
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        programShutdown = false;
    }
}
