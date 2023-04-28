/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.controller.task;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.moreno.sortpics.model.FirstPanelModel;
import org.moreno.sortpics.rename.NameUtils;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
@Getter
public class ImageLoaderWorker extends SwingWorker<Void, ImageIcon> {

    private final int thumbnailSize = 128; // Tamaño de las miniaturas
    final FirstPanelModel model;
    //private final ConcurrentMap<String, ImageIcon> thumbnailCache = new ConcurrentHashMap<>();
    //private final ConcurrentLinkedDeque<File> listFiles = new ConcurrentLinkedDeque<>();
    private final ImageIcon loadingImageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Loading_icon.jpg"));
    private ImageIcon noLoadImageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Loading_icon.jpg"));

    private volatile int numberOfThreads = 20;
    private ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    volatile boolean programShutdown = false;

    public ImageLoaderWorker(FirstPanelModel model) {
        this.model = model;
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

                        //System.out.println("thumbnailCache.size():" + thumbnailCache.size());
                    }
                });
            } else if (programShutdown) {
                executorService.shutdown();
            }
        }
    }

    public BufferedImage readScaledImage(File imageFile, int thumbnailSize) throws IOException {
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

    public void shutdownExecutorService() {
        programShutdown = true;
    }

    public void initExecutorService() {
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        programShutdown = false;
    }

}
