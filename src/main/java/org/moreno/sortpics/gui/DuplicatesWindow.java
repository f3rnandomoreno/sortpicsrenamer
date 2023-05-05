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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.moreno.sortpics.controller.task.ImageLoaderWorker.readScaledImage;

public class DuplicatesWindow extends JFrame {
    private JTable imageTable;
    private JButton deleteButton;
    private JButton selectSameFolderButton;

    private JButton deselectSameFolderButton;
    private JButton selectAllButton;
    private JButton deselectAllButton;

    private DefaultTableModel tableModel;

    public DuplicatesWindow(Map<ImageFileData, List<ImageFileData>> duplicates) {
        setTitle("Duplicated files");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Column names for our table
        String[] columnNames = {"Select", "FileName", "Filepath", "Image", "id"};

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
                    case 4:
                        return Integer.class;
                    default:
                        return super.getColumnClass(columnIndex);
                }
            }
        };

        int idIndex = 0;
        // Populate our table with data
        for (Map.Entry<ImageFileData, List<ImageFileData>> entry : duplicates.entrySet()) {
            try {
                addImageFileDataToTable(idIndex, entry.getKey(), tableModel);
                for (ImageFileData duplicate : entry.getValue()) {
                    if (duplicate.isMediaFile()) {
                        addImageFileDataToTable(idIndex, duplicate, tableModel);
                    }
                }
                idIndex++;
            } catch (IOException e) {
                // show log error
                Log.debug("Error loading image: " + entry.getKey().getAbsolutePath());

            }
        }

        imageTable = new JTable(tableModel);
        imageTable.getColumnModel().getColumn(0).setMaxWidth(10);  // adjust as needed
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
            // Crear un mapa para contar los ids
            Map<Integer, Integer> idCountMap = new HashMap<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Integer id = (Integer) tableModel.getValueAt(i, 4);
                idCountMap.put(id, idCountMap.getOrDefault(id, 0) + 1);
            }

            for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
                Boolean isChecked = (Boolean) tableModel.getValueAt(i, 0);
                Integer id = (Integer) tableModel.getValueAt(i, 4);
                if (isChecked != null && isChecked && idCountMap.get(id) > 1) {
                    // Remover el archivo
                    Path pathFile = Path.of((String) tableModel.getValueAt(i, 2));
                    try {
                        Files.delete(pathFile);
                        System.out.println("File removed:" + tableModel.getValueAt(i, 2));
                    } catch (IOException ex) {
                        // Mostrar mensaje de error
                        JOptionPane.showMessageDialog(null, "Error deleting the file: " + pathFile + "- " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    tableModel.removeRow(i);

                    // Actualizar el conteo de ids
                    idCountMap.put(id, idCountMap.get(id) - 1);
                }
            }
        });

        selectSameFolderButton = new JButton("Select in same folder");
        selectSameFolderButton.addActionListener(e -> {
            int selectedRow = imageTable.getSelectedRow();
            if (selectedRow >= 0) {
                String selectedFilePath = (String) tableModel.getValueAt(selectedRow, 2);
                Integer selectedId = (Integer) imageTable.getModel().getValueAt(selectedRow, 4);
                Path selectedFolder = Paths.get(selectedFilePath).getParent();


                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String filePath = (String) tableModel.getValueAt(i, 2);
                    Integer id = (Integer) imageTable.getModel().getValueAt(i, 4);
                    Path folder = Paths.get(filePath).getParent();

                    var numberOfUnselectedRowsWithId = numberOfUnselectedRowWithId(id);
                    if (folder.equals(selectedFolder) && numberOfUnselectedRowsWithId > 1) {
                        if (!id.equals(selectedId) || (id.equals(selectedId) && numberOfUnselectedRowsWithId > 1)) {
                            tableModel.setValueAt(true, i, 0);
                        }
                    }
                }
            }
        });


        // dentro del constructor después de la creación de selectSameFolderButton
        deselectSameFolderButton = new JButton("Deselect in same folder");
        deselectSameFolderButton.addActionListener(e -> {
            int selectedRow = imageTable.getSelectedRow();
            if (selectedRow >= 0) {
                String selectedFilePath = (String) tableModel.getValueAt(selectedRow, 2);
                Path selectedFolder = Paths.get(selectedFilePath).getParent();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String filePath = (String) tableModel.getValueAt(i, 2);
                    Path folder = Paths.get(filePath).getParent();
                    if (folder.equals(selectedFolder)) {
                        tableModel.setValueAt(false, i, 0);
                    }
                }
            }
        });
        selectAllButton = new JButton("Select All");
        selectAllButton.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(true, i, 0);
            }
        });

        deselectAllButton = new JButton("Deselect All");
        deselectAllButton.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(false, i, 0);
            }
        });

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(selectAllButton);
        southPanel.add(deselectAllButton);
        southPanel.add(deselectSameFolderButton);
        southPanel.add(selectSameFolderButton);
        southPanel.add(deleteButton);


        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

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

    private void addImageFileDataToTable(int id, ImageFileData data, DefaultTableModel model) throws IOException {
        ImageIcon imageIcon = null;
        if (data.isImageFile()) {
            var thumbnailIcon = new ImageIcon(readScaledImage(data.getOriginalFile(), 100 * 3));
            imageIcon = new ImageIcon(thumbnailIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        }

        Object[] rowData = {false, data.getFileName(), data.getAbsolutePath(), imageIcon, id};
        model.addRow(rowData);
    }

    private int numberOfUnselectedRowWithId(int id) {
        int count = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isChecked = (Boolean) tableModel.getValueAt(i, 0);
            Integer rowId = (Integer) tableModel.getValueAt(i, 4);
            if (isChecked != null && !isChecked && rowId.equals(id)) {
                count++;
            }
        }
        return count;
    }
}


