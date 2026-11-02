/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tracker;


public class Internship {
    private String studentName;
    private String rollNumber;
    private String companyName;
    private String jobRole;
    private String status;

    // Constructor
    public Internship(String name, String roll, String company, String role, String status) {
        this.studentName = name;
        this.rollNumber = roll;
        this.companyName = company;
        this.jobRole = role;
        this.status = status;
    }

    // Getters
    public String getStudentName() { return studentName; }
    public String getRollNumber() { return rollNumber; }
    public String getCompanyName() { return companyName; }
    public String getJobRole() { return jobRole; }
    public String getStatus() { return status; }
}