package org.moreno.sortpics.rename;

import java.util.Date;

/**
 * This class is used to store and manage the necessary EXIF information extracted from a JPEG image file.
 * EXIF (Exchangeable Image File Format) data is embedded in the image file by the camera upon capture,
 * and it can include details such as the capture date and the camera model.
 *
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class FileExifrInfo {

    // Capture date of the image
    private Date captureDate = null;

    // Model of the camera that captured the image
    private String modelCamera = null;

    // Number of the camera model
    private String numModelCamera = null;

    // Secondary model of the camera
    private String model2Camera = null;

    // Manufacturer of the camera
    private String make = null;

    /**
     * Checks if the capture date is null.
     *
     * @return true if the capture date is null, false otherwise.
     */
    public boolean captureDateIsNull() {
        return captureDate == null;
    }

    public Date getCaptureDate() {
        return captureDate;
    }

    public String getModelCamera() {
        if (modelCamera != null) {
            return modelCamera.trim();
        }
        return null;
    }

    public String getNumModelCamera() {
        if (numModelCamera != null) {
            return numModelCamera.trim();
        }
        return null;
    }

    public String getAuthorModel() {
        return get_make() + "_" + getModelCamera();
    }

    public String get_model2Camera() {
        return model2Camera;
    }

    public String get_make() {
        if (make != null) {
            return make.trim();
        }
        return null;
    }

    public void setModelCamera(String modelCamera) {
        this.modelCamera = modelCamera;
    }

    public void setModel2Camera(String model2Camera) {
        this.model2Camera = model2Camera;
    }

    public void setNumModelCamera(String numModelCamera) {
        this.numModelCamera = numModelCamera;
    }

    public void setCaptureDate(Date captureDate) {
        this.captureDate = captureDate;
    }

    public void setMakeCamera(String make) {
        this.make = make;
    }

    public boolean isEmty() {
        return captureDateIsNull();
    }

    @Override
    public String toString() {
        return "FileExifrInfo [_captureDate=" + captureDate
                + ", _modelCamera=" + modelCamera + ", _numModelCamera="
                + numModelCamera + ", _model2Camera=" + model2Camera
                + ", _make=" + make + "]";
    }

}
