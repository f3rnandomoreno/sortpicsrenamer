package org.moreno.sortpics.gui;

import org.moreno.sortpics.model.ImageFileData;
import org.moreno.sortpics.utils.Log;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.moreno.sortpics.controller.task.ImageLoaderWorker.readScaledImage;

public class DuplicatesWindow extends JFrame {
    private JTable imageTable;
    private JButton deleteButton;
    private DefaultTableModel tableModel;

    public DuplicatesWindow(Map<ImageFileData, List<ImageFileData>> duplicates) {
        setTitle("Duplicated files");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Column names for our table
        String[] columnNames = {"Select", "FileName", "Filepath", "Image"};

        // Create a model for our table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Boolean.class;
                    case 1:
                    case 2:
                        return String.class;
                    case 3:
                        return ImageIcon.class;
                    default:
                        return super.getColumnClass(columnIndex);
                }
            }
        };

        // Populate our table with data
        for (Map.Entry<ImageFileData, List<ImageFileData>> entry : duplicates.entrySet()) {
            try {
                addImageFileDataToTable(entry.getKey(), tableModel);
                for (ImageFileData duplicate : entry.getValue()) {
                    if (duplicate.isMediaFile()) {
                        addImageFileDataToTable(duplicate, tableModel);
                    }
                }
            } catch (IOException e) {
                // show log error
                Log.debug("Error loading image: " + entry.getKey().getAbsolutePath());

            }
        }

        imageTable = new JTable(tableModel);
        imageTable.getColumnModel().getColumn(2).setCellRenderer(new MultiLineTableCellRenderer());
        imageTable.setRowHeight(100);
        imageTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = imageTable.rowAtPoint(e.getPoint());
                    imageTable.getSelectionModel().setSelectionInterval(row, row);
                    showContextMenu(e.getX(), e.getY());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(imageTable);

        deleteButton = new JButton("Remove selected");
        deleteButton.addActionListener(e -> {
            for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
                Boolean isChecked = (Boolean) tableModel.getValueAt(i, 0);
                if (isChecked != null && isChecked) {
                    // remove file
                    Path pathFile = Path.of((String) tableModel.getValueAt(i, 2));
                    try {
                        Files.delete(pathFile);
                    } catch (IOException ex) {
                        // show error message
                        JOptionPane.showMessageDialog(null, "Error al eliminar el archivo: " + pathFile.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    System.out.println("File removed:" + tableModel.getValueAt(i, 2));
                    tableModel.removeRow(i);
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.SOUTH);
    }

    private void showContextMenu(int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Abrir ubicación del archivo");
        menuItem.addActionListener(e -> {
            int selectedRow = imageTable.getSelectedRow();
            String filePath = (String) tableModel.getValueAt(selectedRow, 2);
            openFileLocation(filePath);
        });
        menu.add(menuItem);
        menu.show(imageTable, x, y);
    }

    private void openFileLocation(String filePath) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();
            if (osName.indexOf("win") >= 0) {
                // Para Windows
                rt.exec("explorer.exe /select," + filePath);
            } else if (osName.indexOf("mac") >= 0) {
                // Para MacOS
                rt.exec("open \"" + Paths.get(filePath).getParent().toString() + "\"");
            } else {
                // Para sistemas Unix-like, puedes probar con xdg-open
                rt.exec("xdg-open " + Paths.get(filePath).getParent().toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addImageFileDataToTable(ImageFileData data, DefaultTableModel model) throws IOException {
        ImageIcon imageIcon = null;
        if (data.isImageFile()) {
            var thumbnailIcon = new ImageIcon(readScaledImage(data.getOriginalFile(), 100 * 3));
            imageIcon = new ImageIcon(thumbnailIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        }
        Object[] rowData = {false, data.getFileName(), data.getAbsolutePath(), imageIcon};
        model.addRow(rowData);
    }
}


