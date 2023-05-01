package org.moreno.sortpics.gui;

import org.moreno.sortpics.model.ImageFileData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class DuplicatesWindow extends JFrame {
    private JTable imageTable;
    private JButton deleteButton;
    private DefaultTableModel tableModel;

    public DuplicatesWindow(Map<ImageFileData, List<ImageFileData>> duplicates) {
        setTitle("Archivos duplicados");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Column names for our table
        String[] columnNames = {"Seleccionar", "Nombre del archivo", "Ruta del archivo", "Imagen"};

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
            addImageFileDataToTable(entry.getKey(), tableModel);
            for (ImageFileData duplicate : entry.getValue()) {
                addImageFileDataToTable(duplicate, tableModel);
            }
        }

        imageTable = new JTable(tableModel);
        imageTable.setRowHeight(100);
        JScrollPane scrollPane = new JScrollPane(imageTable);

        deleteButton = new JButton("Eliminar seleccionados");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.SOUTH);
    }

    private void addImageFileDataToTable(ImageFileData data, DefaultTableModel model) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(data.getAbsolutePath()).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        Object[] rowData = {false, data.getFileName(), data.getAbsolutePath(), imageIcon};
        model.addRow(rowData);
    }
}


