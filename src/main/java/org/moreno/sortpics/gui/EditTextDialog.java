package org.moreno.sortpics.gui;

import javax.swing.*;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class EditTextDialog {


    public static String showEditTextDialog(JFrame parent, String initialText) {
        JTextArea textArea = new JTextArea(initialText);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(300, 200));

        int result = JOptionPane.showConfirmDialog(parent, scrollPane, "Editar nombre de fichero", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return textArea.getText();
        } else {
            return initialText;
        }
    }
}
