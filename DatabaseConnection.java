/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tracker;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Update "root" and "password" with your MySQL credentials
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/internship_tracker", "root", "Vaish@1202");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}