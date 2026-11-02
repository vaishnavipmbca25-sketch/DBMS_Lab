CREATE DATABASE IF NOT EXISTS internship_tracker;
USE internship_tracker;
CREATE TABLE IF NOT EXISTS student_internships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    roll_number VARCHAR(50) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    job_role VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Applied');
INSERT INTO student_internships (student_name, roll_number, company_name, job_role, status) VALUES  ('Vaishnavi', '201', 'Google', 'SDE Intern', 'Selected'),
('Bujji', '202', 'Microsoft', 'Data Analyst Intern', 'Interview Scheduled'),
('Mrudula', '203', 'Amazon', 'Cloud Intern', 'Applied'),
('Wruthambara', '204', 'TCS', 'System Engineer Intern', 'Rejected');
SELECT * FROM student_internships;