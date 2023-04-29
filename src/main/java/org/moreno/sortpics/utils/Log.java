/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.moreno.sortpics.utils;


public class Log {
    static boolean DEBUG = true;

    public static void debug(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }
}
