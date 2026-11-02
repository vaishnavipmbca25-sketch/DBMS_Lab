/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InternshipDAO {
    
    public boolean addInternship(Internship intern) {
        boolean success = false;
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO student_internships (student_name, roll_number, company_name, job_role, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, intern.getStudentName());
            pst.setString(2, intern.getRollNumber());
            pst.setString(3, intern.getCompanyName());
            pst.setString(4, intern.getJobRole());
            pst.setString(5, intern.getStatus());
            
            int rows = pst.executeUpdate();
            if (rows > 0) success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public List<Internship> getAllInternships() {
        List<Internship> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM student_internships";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Internship intern = new Internship(
                    rs.getString("student_name"),
                    rs.getString("roll_number"),
                    rs.getString("company_name"),
                    rs.getString("job_role"),
                    rs.getString("status")
                );
                list.add(intern);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
