package org.moreno.sortpics.controller.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@Data
@AllArgsConstructor
public class ErrorHandler {
    private File file;
    private String message;
}
