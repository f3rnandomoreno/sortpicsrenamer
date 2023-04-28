package org.moreno.sortpics.gui;

import org.moreno.sortpics.model.FirstPanelModel;
import org.moreno.sortpics.model.ImageFileData;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static org.moreno.sortpics.rename.NameUtils.isImage;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class ThumbnailListCellRenderer extends DefaultListCellRenderer {

    public ThumbnailListCellRenderer(FirstPanelModel model) {
        this.model = model;
    }

    private final int thumbnailSize = 128; // Tamaño de las miniaturas
    private final FirstPanelModel model;
    private final ImageIcon loadingImageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Loading_icon.jpg"));

    private final String TEMPLATE = "<html>%s<font size=+0>%s</font><br><font size=-2>%s</font></html>";
    private final String NEW_NAME_TEMPLATE = "<font size=-2>Nuevo nombre:</font>";
    private final Color COLOR_GREEN = new Color(166, 226, 46);
    private final Color COLOR_ORANGE = new Color(255, 157, 70);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String labelText;
        if (value instanceof ImageFileData) {
            ImageFileData imageData = (ImageFileData) value;
            File imageFile = imageData.getOriginalFile();
            if (imageFile.exists() && imageFile.isFile() && isImage(imageFile)) {
                String imagePath = imageData.getOriginalFile().getAbsolutePath();
                ImageIcon thumbnailIcon = model.getThumbnailCache().get(imageFile.getAbsolutePath());
                if (thumbnailIcon == null) {
                    label.setIcon(loadingImageIcon);
                } else {
                    ImageIcon originalIcon = model.getThumbnailCache().get(imagePath);
                    int originalWidth = originalIcon.getIconWidth();
                    int originalHeight = originalIcon.getIconHeight();

                    // Calcular el nuevo ancho y alto conservando el aspect ratio
                    int newWidth;
                    int newHeight;
                    if (originalWidth > originalHeight) {
                        newWidth = thumbnailSize;
                        newHeight = (int) (((double) originalHeight / originalWidth) * thumbnailSize);
                    } else {
                        newHeight = thumbnailSize;
                        newWidth = (int) (((double) originalWidth / originalHeight) * thumbnailSize);
                    }

                    thumbnailIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));
                    label.setIcon(thumbnailIcon);
                }
                // Verificar si el nombre de archivo es igual al nuevo nombre y establecer el fondo
                if (imageFile.getName().equals(imageData.getNewName())) {
                    label.setBackground(COLOR_GREEN);
                    labelText = String.format(TEMPLATE, "", imageData.getNewName(), imageData.getOriginalFile().getAbsolutePath());

                } else {
                    label.setBackground(COLOR_ORANGE);
                    labelText = String.format(TEMPLATE, NEW_NAME_TEMPLATE, imageData.getNewName(), imageData.getOriginalFile().getAbsolutePath());

                }
                label.setText(labelText);
                label.setPreferredSize(new Dimension(thumbnailSize, thumbnailSize + 2));
            } else {
                labelText = String.format(TEMPLATE, "", imageData.getNewName(), imageData.getOriginalFile().getAbsolutePath());
                label.setText(labelText);
            }
        }
        return label;
    }

}
