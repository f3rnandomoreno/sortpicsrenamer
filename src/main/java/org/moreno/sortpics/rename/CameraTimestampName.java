package org.moreno.sortpics.rename;

import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides utility methods to manipulate and retrieve information from file names
 * according to the CameraTimestamp naming convention. It includes methods to replace special
 * characters in a file name, format a new name for a file, retrieve data from a file, and
 * get the last modified date of a file.
 *
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class CameraTimestampName {

    /**
     * Replaces special characters in a given string to avoid conflicts.
     *
     * @param data The string from which special characters should be replaced.
     * @return The string with special characters replaced.
     */
    static public String replaceSpecialCharacters(String data) {
        data = data.replaceAll("\\(", "{");
        data = data.replaceAll("\\[", "#");
        data = data.replaceAll("\\)", "}");
        data = data.replaceAll("\\]", "#");
        data = data.replaceAll("\\-", "_");
        data = data.replaceAll("\\\\", "_");
        data = data.replaceAll("\\/", "_");
        return data;
    }

    /**
     * Formats and returns a new file name given several pieces of information.
     *
     * @param date          The date to be included in the file name.
     * @param author        The author to be included in the file name.
     * @param countProgress The progress count to be included in the file name.
     * @param data          The data to be included in the file name.
     * @param extension     The extension to be added to the file name.
     * @return The formatted file name.
     */
    static public String getName(String date, String author, int countProgress, String data, String extension) {
        return date + "-" + CameraTimestampName.replaceSpecialCharacters(author) + "[" + countProgress + "]" + "(" + data + ")" + FilenameUtils.EXTENSION_SEPARATOR + extension;
    }

    /**
     * Extracts and returns a file name from a path, using the CameraTimestamp naming convention.
     * If the file name does not follow the convention, the method generates a new one.
     *
     * @param path          The path of the file to retrieve or generate a name for.
     * @param countProgress The progress count to be included in the file name if a new one is generated.
     * @return The file name following the CameraTimestamp naming convention.
     * @throws ImageReadException If there's an error reading the image.
     * @throws IOException        If there's an error reading the file.
     * @throws ParseException     If there's an error parsing the date.
     */
    public static String getName(String path, int countProgress) throws ImageReadException, IOException, ParseException {
        String fileName = FilenameUtils.getName(path);
        if (!JpegFileMetadata.isCameraTimestampNaming(fileName)) {
            String dateString = "";
            String author = "";
            String data = "";
            Optional<LocalDateTime> optionalDate = NameUtils.dateTimeOnFileName(fileName);
            if (optionalDate.isPresent()) {
                dateString = optionalDate.get().format(DateTimeFormatter.ofPattern("(yyyy-MM-dd)'['HH.mm.ss']'"));
                author = "FromFileName";
            }
            FileExifrInfo exifrInfo = JpegFileMetadata.getExifrInfo(path);
            if (exifrInfo.captureDateIsNull()) {
                if (!optionalDate.isPresent()) {
                    author = "LastModifiedDate";
                }
                if (dateString.isEmpty()) {
                    dateString = JpegFileMetadata.getFerFormatDate(getLastModifiedDate(path));
                }
            } else {
                author = exifrInfo.getAuthorModel();
                if (optionalDate.isPresent()) {
                    author = author + "_FFN";
                }
                if (dateString.isEmpty()) {
                    dateString = JpegFileMetadata.getFerFormatDate(exifrInfo.getCaptureDate());
                }
            }


            data = FilenameUtils.getBaseName(replaceSpecialCharacters(fileName));
            return getName(dateString, author, countProgress, data, FilenameUtils.getExtension(fileName));
        } else {
            return fileName;
        }
    }


    /**
     * @param path The path of the file to get data from.
     * @return The extracted data as a String.
     * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
     * This method retrieves specific data from the file at the provided path. The type of data depends on
     * the file's naming format and whether it's in the new Fer format.
     */
    static public String getData(String path) {

        String data = "";

        if (JpegFileMetadata.isCameraTimestampNaming(path)) {

            if (JpegFileMetadata.isNewFerFormat(path)) {
                data = JpegFileMetadata.getInfoData(path);
            }

        } else {

            data = FilenameUtils.getBaseName(path);

            // for avoding conflics of format we replace special characters
            data = CameraTimestampName.replaceSpecialCharacters(data);

        }
        return data;
    }


    /**
     * @param filePath The path of the file to get the last modified date from.
     * @return A Date object representing the last modified date of the file.
     * @throws IOException If an I/O error occurs while accessing the file.
     * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
     * This method retrieves the last modified date of the file at the given file path.
     */
    private static Date getLastModifiedDate(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        var fileTime = Files.getLastModifiedTime(path);
        return new Date(fileTime.toMillis());
    }

    public static String renameWithDateFromData(String input) {
        String data = getData(input);
        // Patrón de búsqueda para las fechas
        String fechaPattern = "\\d{4}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(fechaPattern);

        // Buscar la fecha en los datos
        Matcher matcher = pattern.matcher(data);
        // Obtener la primera fecha encontrada
        String fechaParentesis = "";
        if (matcher.find()) {
            fechaParentesis = matcher.group(0);
        }

        // Convertir la fecha al formato YYYY-MM-DD
        String fechaFormateada = fechaParentesis.substring(0, 4) + "-" + fechaParentesis.substring(4, 6) + "-" + fechaParentesis.substring(6, 8);

        // Reemplazar la fecha dentro del primer paréntesis con la fecha formateada
        String output = input.replaceFirst("\\((.*?)\\)", "(" + fechaFormateada + ")");
        return output;
    }
}
