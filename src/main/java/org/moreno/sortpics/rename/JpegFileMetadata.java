/*   Copyright 2010 Fernando Moreno Ruiz All Rights Reserved.
 *
 * 	 This file is part of FotoMixDate.
 *
 *   FotoMixDate is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   FotoMixDate is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with FotoMixDate.  If not, see <http://www.gnu.org/licenses/>.
 * */
package org.moreno.sortpics.rename;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;


/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class JpegFileMetadata {

    static final int TAG_DATE = 36868;
    static DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss"); //set the date format from metada
    static public DateFormat formatterTimeDate = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    static public DateFormat formatterOnlyDate = new SimpleDateFormat("yyyy-MM-dd");
    static public DateFormat formatterOnlyMonth = new SimpleDateFormat("yyyy-MM");
    static public DateFormat formatterOnlyYear = new SimpleDateFormat("yyyy-MM");
    static public DateFormat formatterOnlyTime = new SimpleDateFormat("HH.mm.ss");
    static public DateFormat formatterFileName = new SimpleDateFormat("(yyyy-MM-dd)[HH.mm.ss]");
    Date date;

    /**
     * @throws ParseException
     * @throws IOException
     * @throws ImageReadException
     * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
     * <b>get date from jpg file.<b>
     */
    public JpegFileMetadata(String path) throws ImageReadException, IOException, ParseException {
        date = getDate(path);
    }

    static public Date getDate(String path) throws ImageReadException, IOException, ParseException {
        //variables
        IImageMetadata jpgMetadata; //store generic jpg metadata file
        TiffImageMetadata tiffMetadata; //store tiff(exif) metadata from jpg file
        TiffField date;    //store date information from metadata
        //begin
        try {
            jpgMetadata = Sanselan.getMetadata(new File(path));
        } catch (org.apache.sanselan.ImageReadException e) {
            return null;
        }
        if (jpgMetadata instanceof JpegImageMetadata) {
            tiffMetadata = ((JpegImageMetadata) jpgMetadata).getExif();
            try {
                date = tiffMetadata.findField(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            } catch (java.lang.NullPointerException e) {
                return null;
            }
            if (date == null) {
                return null;
            }
            try {
                return formatter.parse(date.getStringValue());
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    static public FileExifrInfo getExifrInfo(String path) throws ImageReadException, IOException, ParseException {
        //variables
        IImageMetadata jpgMetadata; //store generic jpg metadata file
        TiffImageMetadata tiffMetadata; //store tiff(exif) metadata from jpg file
        TiffField date;    //store date information from metadata
        FileExifrInfo fileExifrInfo = new FileExifrInfo(); // file exifr information that is going to be got from file
        //begin
        try {
            jpgMetadata = Sanselan.getMetadata(new File(path));
        } catch (Exception e) {
            return fileExifrInfo;
        }
        if (jpgMetadata instanceof JpegImageMetadata) {
            tiffMetadata = ((JpegImageMetadata) jpgMetadata).getExif();
            try {
                date = tiffMetadata.findField(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                try {
                    fileExifrInfo.setCaptureDate(formatter.parse(date.getStringValue()));
                } catch (ParseException e) {
                    return fileExifrInfo;
                }
                fileExifrInfo.setModelCamera(tiffMetadata.findField(TiffConstants.EXIF_TAG_MODEL).getStringValue());
                fileExifrInfo.setMakeCamera(tiffMetadata.findField(TiffConstants.EXIF_TAG_MAKE).getStringValue());
            } catch (java.lang.NullPointerException e) {
                return fileExifrInfo;
            }
        } else {
            // do nothing
        }
        return fileExifrInfo;
    }

    static public Date getLastModifiedDate(File file) {
        Date date = new Date();
        date.setTime(file.lastModified());
        return date;
    }

    static public Date getDateCreated(File file) {
        Date date = new Date();
        Path p = Paths.get(file.getPath());
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(p, BasicFileAttributes.class);
        } catch (IOException e) {
            return null;
        }
        date.setTime(attr.creationTime().toMillis());
        return date;
    }

    /**
     * @author Fernando Moreno Ruiz
     * @version 0.01
     * <b>set date from jpg file.<b>
     */
    public static void setDate(String path, Date date) throws ImageReadException, IOException, ClassCastException, ImageWriteException {
        //variables
        File source = new File(path); //file source that are going to be changed.
        File temp = null; //temp file to create the new File
        OutputStream os = null;    //Stream used to update exif metadata
        String stringDate = formatter.format(date);    //date to String type for using
        TiffOutputSet propiertiesSet = new TiffOutputSet();    //All tags from exif
        TiffOutputDirectory exifDirectory;    //Directory exif
        //begin
        propiertiesSet = ((JpegImageMetadata) Sanselan.getMetadata(source)).getExif().getOutputSet();
        propiertiesSet.removeField(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        TiffOutputField newfieldDate = new TiffOutputField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, TiffFieldTypeConstants.FIELD_TYPE_ASCII, stringDate.length(), stringDate.getBytes());
        exifDirectory = propiertiesSet.getOrCreateExifDirectory();
        exifDirectory.add(newfieldDate);
        temp = File.createTempFile("temp-" + System.currentTimeMillis(), ".jpg");
        os = new FileOutputStream(temp);
        os = new BufferedOutputStream(os);
        ExifRewriter rewrite = new ExifRewriter();
        rewrite.updateExifMetadataLossless(source, os, propiertiesSet);
        os.close();
        //rewrite original file with new file
        FileUtils.copyFile(temp, source);
    }

    public Date getDate() {
        return date;
    }

    public static String getFerFormatDate(Date d) {
        return formatterFileName.format(d);
    }

    public String getTimeDate() {
        return formatterTimeDate.format(date);
    }

    public String getOnlyDate() {
        return formatterOnlyDate.format(date);
    }

    public String getOnlyTime() {
        return formatterOnlyTime.format(date);
    }

    public String getFileNameFormat() {
        return formatterFileName.format(date);
    }

    public static String getFileNameFormat(Date d) {
        return formatterFileName.format(d);
    }

    public static String getOnlyDate(Date d) {
        return formatterOnlyDate.format(d);
    }

    public static String getOnlyMonth(DateOperable d) {
        return formatterOnlyMonth.format(d);
    }

    public static String getOnlyYear(DateOperable d) {
        return formatterOnlyYear.format(d);
    }

    public static String getAuthorFromFileName(String file) throws IndexOutOfBoundsException {
        String author;
        int begin = file.lastIndexOf("-");
        int end = file.lastIndexOf("[");
        author = file.substring(begin + 1, end);
        return author;
    }

    // fileName is FilenameUtils.getBaseName
    public static DateOperable getDateFromFileName(String fileName) {
        try {
            String date;
            date = fileName.substring(0, 22);
            DateOperable result = new DateOperable(formatterFileName.parse(date));
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static DateOperable getDateFromPath(String path) {
        return getDateFromFileName(FilenameUtils.getBaseName(path));
    }

    public static boolean isCameraTimestampNaming(String path) {

        // Obtener el nombre del archivo sin la extensión
        String baseName = FilenameUtils.getBaseName(path);

        // Comprobar que el nombre del archivo tenga el formato "(yyyy-MM-dd)[HH.mm.ss]"
        if (!baseName.matches("\\(\\d{4}-\\d{2}-\\d{2}\\)\\[\\d{2}\\.\\d{2}\\.\\d{2}\\]-.*\\[\\d+].*")) {
            return false;
        }

        return true;
    }

    public static boolean isNewFerFormat(String path) {
        DateOperable result = JpegFileMetadata.getDateFromFileName(FilenameUtils.getName(path));
        String data = JpegFileMetadata.getInfoData(path);
        return result != null && data != null;
    }

    public static String getInfoData(String path) {
        String name = FilenameUtils.getBaseName(path);
        int indexStartData = name.lastIndexOf("(");
        int indexFinishData = name.lastIndexOf(")");
        if (name.length() - 1 != indexFinishData) {
            return null;
        }
        try {
            String data = name.substring(indexStartData + 1, indexFinishData);
            return data;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    public static LocalDateTime getCaptureDateTimeExifr(String imagePath) {
        try {
            File jpegFile = new File(imagePath);
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (directory != null && directory.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) {
                return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else {
                System.err.println("No se encontró la fecha de captura en los metadatos.");
                return null;
            }

        } catch (ImageProcessingException | IOException e) {
            System.err.println("Error al procesar la imagen: " + e.getMessage());
            return null;
        }
    }

}
