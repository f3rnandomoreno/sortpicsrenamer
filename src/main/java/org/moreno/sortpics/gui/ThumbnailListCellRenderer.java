package org.moreno.sortpics.gui;

import org.moreno.sortpics.model.ImageFileData;

import java.awt.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.moreno.sortpics.model.FirstPanelModel;

public class ThumbnailListCellRenderer extends DefaultListCellRenderer {

    public ThumbnailListCellRenderer(FirstPanelModel model) {
        this.model = model;
    }
    private final int thumbnailSize = 128; // Tamaño de las miniaturas
    private final FirstPanelModel model;
    private final ImageIcon loadingImageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Loading_icon.jpg"));
    private final ImageIcon noLoadedIcon = new ImageIcon(getClass().getClassLoader().getResource("images/no_load.jpg"));

    private final String TEMPLATE
            = "<html>"
            + "    <div>"
            + "        <p>%s<strong>%s</strong></p>"
            + "        <p>Archivo actual: <strong>%s</strong></p>"
            + "        <p>"
            + "            <font bgcolor=\"%s\"><span style=\"margin-right:5px;\">%s</span></font>"
            + "            <font bgcolor=\"%s\"><span style=\"margin-right:5px;\">%s</span></font>"
            + "        </p>"
            + "    </div>"
            + "</html>";
    private final String NEW_NAME_TEMPLATE = "<font size=-2>Nuevo nombre:</font>";
    private final Color COLOR_GREEN = new Color(76, 175, 80); // Material Design Green 500
    private final Color COLOR_ORANGE = new Color(255, 152, 0); // Material Design Orange 500

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String labelText;
        if (value instanceof ImageFileData) {
            ImageFileData imageData = (ImageFileData) value;

            var htmlColor = imageData.getHtmlColor();
            if (imageData.isFile() && imageData.isImageFile()) {
                String imagePath = imageData.getAbsolutePath();
                ImageIcon thumbnailIcon = model.getThumbnailCache().get(imageData.getAbsolutePath());
                if (thumbnailIcon == null) {
                    label.setIcon(loadingImageIcon);
                } else {
                    thumbnailIcon = scaleImageIcon(imagePath);
                    label.setIcon(thumbnailIcon);
                }

            } else {
                label.setIcon(noLoadedIcon);
            }
            // Verificar si el nombre de archivo es igual al nuevo nombre y establecer el fondo
            if (imageData.getFileName().equals(imageData.getNewName())) {
                label.setBackground(COLOR_GREEN);
                labelText = String.format(TEMPLATE, "", imageData.getNewName(), imageData.getOriginalFile().getAbsolutePath(), htmlColor, imageData.getDayDate(), htmlColor, imageData.getTimeDate());

            } else {
                label.setBackground(COLOR_ORANGE);
                labelText = String.format(TEMPLATE, NEW_NAME_TEMPLATE, imageData.getNewName(), imageData.getOriginalFile().getAbsolutePath(), htmlColor, imageData.getDayDate(), htmlColor, imageData.getTimeDate());

            }
            label.setText(labelText);
            label.setPreferredSize(new Dimension(thumbnailSize, thumbnailSize));
        }
        return label;
    }

    private ImageIcon scaleImageIcon(String imagePath) {
        ImageIcon thumbnailIcon;
        ImageIcon originalIcon = model.getThumbnailCache().get(imagePath);
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // calculate new width and height
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
        return thumbnailIcon;
    }

}
