package org.moreno.sortpics.rename;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class NameUtils {
    /**
     * Extracts the date and time information from a file name that follows a general pattern.
     * The pattern that is matched is any pattern that include yyyy MM dd hh mm ss .extension.
     * It can have any not digit between them or empty character.
     * The extracted date and time information is returned as an array of integers, where
     * the first element corresponds to the year, the second to the month, the third to the day,
     * the fourth to the hour, the fifth to the minute, and the sixth to the second.
     *
     * @param fileName the name of the file to extract the date and time information from
     * @return an array of integers containing the date and time information, or null if no
     *         date and time information could be extracted
     */
    public static Optional<LocalDateTime> dateTimeOnFileName(String fileName) {

        // Regular expression to match date and time in file name
        String regex = ".*?(\\d{4})(?:\\D|)(\\d{2})(?:\\D|)(\\d{2})(?:\\D|)(\\d{2})(?:\\D|)(\\d{2})(?:\\D|)(\\d{2})(?:\\D|).*";

        // Create a matcher object to match against the file name
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher object to match against the file name
        Matcher matcher = pattern.matcher(fileName);

        // Initialize the digits array to null
        Integer[] digits = null;

        // Check if the pattern matched the file name
        if (matcher.matches()) {
            // Create a new array to store the matched digits
            digits = new Integer[6];

            // Extract the year from the first matched group
            digits[0] = Integer.valueOf(matcher.group(1));

            // Extract the month from the second matched group
            digits[1] = Integer.valueOf(matcher.group(2));

            // Extract the day from the third matched group
            digits[2] = Integer.valueOf(matcher.group(3));

            // Extract the hour from the fourth matched group
            digits[3] = Integer.valueOf(matcher.group(4));

            // Extract the minute from the fifth matched group
            digits[4] = Integer.valueOf(matcher.group(5));

            // Extract the second from the sixth matched group
            digits[5] = Integer.valueOf(matcher.group(6));

        }
        Optional<LocalDateTime> result = Optional.empty();
        if(digits!=null){
            result = Optional.of(LocalDateTime.of(digits[0],digits[1],digits[2],digits[3],digits[4],digits[5]));
        }
        // Return the digits array
        return result;
    }
    
    public static boolean isImage(File imageFile) {
        String fileName = FilenameUtils.getName(imageFile.getAbsolutePath());
        return Commons.getAllowedImageFormats().stream().anyMatch((extension)->{
            return fileName.toLowerCase().endsWith(extension);
        });
    }

    
}
