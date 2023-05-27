package org.moreno.sortpics.controller;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public class TextFieldChangeListener implements DocumentListener {

    private final Consumer<DocumentEvent> onChange;

    public TextFieldChangeListener(Consumer<DocumentEvent> onChange) {
        this.onChange = onChange;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        onChange.accept(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        onChange.accept(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // Este método no se llama para JTextFields, por lo que puedes dejarlo vacío.
    }
}
