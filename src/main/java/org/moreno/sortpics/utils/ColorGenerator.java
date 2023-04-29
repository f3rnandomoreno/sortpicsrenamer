/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.utils;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ColorGenerator {

    public static String getColorFromDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = sdf.parse(dateString);
            long seed = date.getTime();
            Random random = new Random(seed);
            float hue = random.nextFloat();
            float saturation = 0.1f + random.nextFloat() * 0.3f; // 0.1 to 0.4
            float brightness = 0.8f + random.nextFloat() * 0.2f; // 0.8 to 1.0
            Color color = Color.getHSBColor(hue, saturation, brightness);
            return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        } catch (ParseException e) {
            e.printStackTrace();
            return "#000000"; // Devuelve un color negro en caso de error al analizar la fecha
        }
    }
}
