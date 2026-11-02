package com.tracker;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MainApp {

    // Database Credentials - update "password" to your MySQL password
    static final String DB_URL = "jdbc:mysql://localhost:3306/internship_tracker";
    static final String USER = "root";
    static final String PASS = "Vaish@1202"; 

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        // Route for the main dashboard (Create, Read, Delete)
        server.createContext("/", new RootHandler());
        // Route for the edit page (Update)
        server.createContext("/edit", new EditHandler());
        
        server.setExecutor(null); 
        server.start();
        
        System.out.println("Server successfully started with Full CRUD capabilities!");
        System.out.println("Open your browser and visit: http://localhost:8080/");
    }

    // --- HANDLERS ---

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                Map<String, String> formData = parseFormData(br.readLine());
                
                String action = formData.get("action");
                if ("create".equals(action)) {
                    createRecord(formData);
                } else if ("delete".equals(action)) {
                    deleteRecord(formData.get("id"));
                } else if ("update".equals(action)) {
                    updateRecord(formData);
                }
                
                // Redirect to avoid form resubmission
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(303, -1);
                return;
            }

            // GET Request - Show Dashboard
            String response = generateDashboardHTML();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class EditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseFormData(query);
            String id = params.get("id");

            String response = generateEditHTML(id);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // --- DATABASE CRUD OPERATIONS ---

    private static void createRecord(Map<String, String> data) {
        String sql = "INSERT INTO student_internships (student_name, roll_number, company_name, job_role, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.get("name"));
            pstmt.setString(2, data.get("roll"));
            pstmt.setString(3, data.get("company"));
            pstmt.setString(4, data.get("role"));
            pstmt.setString(5, data.get("status"));
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void updateRecord(Map<String, String> data) {
        String sql = "UPDATE student_internships SET student_name=?, roll_number=?, company_name=?, job_role=?, status=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.get("name"));
            pstmt.setString(2, data.get("roll"));
            pstmt.setString(3, data.get("company"));
            pstmt.setString(4, data.get("role"));
            pstmt.setString(5, data.get("status"));
            pstmt.setInt(6, Integer.parseInt(data.get("id")));
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void deleteRecord(String id) {
        String sql = "DELETE FROM student_internships WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- HELPER METHODS ---

    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        if (formData == null || formData.isEmpty()) return map;
        try {
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length > 1) {
                    map.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
                } else if (kv.length == 1) {
                    map.put(kv[0], "");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    private static String getSharedCSS() {
        return "<style>" +
               "body { font-family: 'Poppins', sans-serif; background-color: #f4f7fe; color: #2b3674; margin: 0; padding: 20px; }" +
               ".container { max-width: 1000px; margin: 0 auto; }" +
               ".header { text-align: center; margin-bottom: 30px; }" +
               ".header h1 { color: #4318FF; margin: 0; font-size: 2.5rem; }" +
               ".header p { color: #a3aed1; margin: 5px 0 0 0; }" +
               ".card { background: #ffffff; padding: 30px; border-radius: 20px; box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.05); margin-bottom: 30px; }" +
               ".card h2 { margin-top: 0; color: #2b3674; font-size: 1.5rem; border-bottom: 2px solid #f4f7fe; padding-bottom: 10px; margin-bottom: 20px; }" +
               ".form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 20px; }" +
               ".form-group { display: flex; flex-direction: column; }" +
               "label { font-weight: 500; margin-bottom: 8px; font-size: 0.9rem; color: #2b3674; }" +
               "input, select { padding: 12px 15px; border: 1px solid #e0e5f2; border-radius: 10px; font-size: 1rem; font-family: 'Poppins', sans-serif; background: #fcfcfc; transition: all 0.3s ease; }" +
               "input:focus, select:focus { outline: none; border-color: #4318FF; background: #fff; box-shadow: 0 0 0 3px rgba(67, 24, 255, 0.1); }" +
               "button { background-color: #4318FF; color: white; border: none; padding: 15px 30px; border-radius: 10px; cursor: pointer; font-size: 1rem; font-weight: 600; width: 100%; transition: all 0.3s ease; }" +
               "button:hover { background-color: #3311db; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(67, 24, 255, 0.3); }" +
               "table { width: 100%; border-collapse: collapse; text-align: left; }" +
               "th { background-color: #f4f7fe; color: #a3aed1; font-weight: 600; padding: 15px; border-radius: 10px 10px 0 0; font-size: 0.9rem; text-transform: uppercase; }" +
               "td { padding: 15px; border-bottom: 1px solid #f4f7fe; color: #2b3674; font-weight: 500; }" +
               "tr:hover td { background-color: #f8fafe; }" +
               ".badge { padding: 6px 12px; border-radius: 20px; font-size: 0.85rem; font-weight: 600; }" +
               ".status-applied { background: #e0f2fe; color: #0284c7; }" +
               ".status-interview { background: #fef08a; color: #a16207; }" +
               ".status-selected { background: #dcfce3; color: #166534; }" +
               ".status-rejected { background: #fee2e2; color: #991b1b; }" +
               ".action-forms { display: flex; gap: 8px; }" +
               ".action-forms form { margin: 0; }" +
               ".btn-sm { padding: 8px 15px; font-size: 0.85rem; width: auto; border-radius: 8px; text-decoration: none; display: inline-block; text-align: center; font-weight: 500; box-shadow: none; }" +
               ".btn-edit { background: #f1f5f9; color: #334155; border: 1px solid #cbd5e1; }" +
               ".btn-edit:hover { background: #e2e8f0; transform: translateY(0); box-shadow: none; color: #0f172a; }" +
               ".btn-delete { background: #fee2e2; color: #991b1b; }" +
               ".btn-delete:hover { background: #fecaca; transform: translateY(0); box-shadow: none; }" +
               "</style>";
    }

    // --- HTML GENERATORS ---

    private static String generateDashboardHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>Internship Tracker</title>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<link href='[https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap](https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap)' rel='stylesheet'>");
        html.append(getSharedCSS());
        html.append("</head><body><div class='container'>");
        
        html.append("<div class='header'><h1>Internship Tracker</h1><p>Manage and track your application progress</p></div>");
        
        // CREATE Form
        html.append("<div class='card'><h2>Add New Application</h2>");
        html.append("<form method='POST' action='/'>");
        html.append("<input type='hidden' name='action' value='create'>");
        html.append("<div class='form-grid'>");
        html.append("<div class='form-group'><label>Student Name</label><input type='text' name='name' required></div>");
        html.append("<div class='form-group'><label>Roll Number</label><input type='text' name='roll' required></div>");
        html.append("<div class='form-group'><label>Company Name</label><input type='text' name='company' required></div>");
        html.append("<div class='form-group'><label>Job Role</label><input type='text' name='role' required></div>");
        html.append("<div class='form-group'><label>Status</label><select name='status'>");
        html.append("<option value='Applied'>Applied</option><option value='Interview Scheduled'>Interview Scheduled</option>");
        html.append("<option value='Selected'>Selected</option><option value='Rejected'>Rejected</option>");
        html.append("</select></div>");
        html.append("</div><button type='submit'>Save Application</button></form></div>");

        // READ Table (with UPDATE/DELETE buttons)
        html.append("<div class='card'><h2>Recent Applications</h2><div style='overflow-x:auto;'>");
        html.append("<table><tr><th>Student</th><th>Roll No</th><th>Company</th><th>Role</th><th>Status</th><th>Actions</th></tr>");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student_internships ORDER BY id DESC")) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String status = rs.getString("status");
                String badgeClass = "badge ";
                if(status.equals("Applied")) badgeClass += "status-applied";
                else if(status.equals("Interview Scheduled")) badgeClass += "status-interview";
                else if(status.equals("Selected")) badgeClass += "status-selected";
                else if(status.equals("Rejected")) badgeClass += "status-rejected";

                html.append("<tr>");
                html.append("<td>").append(rs.getString("student_name")).append("</td>");
                html.append("<td>").append(rs.getString("roll_number")).append("</td>");
                html.append("<td>").append(rs.getString("company_name")).append("</td>");
                html.append("<td>").append(rs.getString("job_role")).append("</td>");
                html.append("<td><span class='").append(badgeClass).append("'>").append(status).append("</span></td>");
                
                // Action Buttons (Edit goes to GET /edit, Delete is a POST form)
                html.append("<td><div class='action-forms'>");
                html.append("<a href='/edit?id=").append(id).append("' class='btn-sm btn-edit'>Edit</a>");
                html.append("<form method='POST' action='/'>")
                    .append("<input type='hidden' name='action' value='delete'>")
                    .append("<input type='hidden' name='id' value='").append(id).append("'>")
                    .append("<button type='submit' class='btn-sm btn-delete' onclick=\"return confirm('Are you sure you want to delete this record?');\">Delete</button>")
                    .append("</form>");
                html.append("</div></td></tr>");
            }
        } catch (Exception e) {
            html.append("<tr><td colspan='6' style='color: red;'>Error loading database records.</td></tr>");
        }
        
        html.append("</table></div></div></div></body></html>");
        return html.toString();
    }

    private static String generateEditHTML(String id) {
        Map<String, String> record = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM student_internships WHERE id=?")) {
            pstmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                record.put("name", rs.getString("student_name"));
                record.put("roll", rs.getString("roll_number"));
                record.put("company", rs.getString("company_name"));
                record.put("role", rs.getString("job_role"));
                record.put("status", rs.getString("status"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>Edit Application</title>");
        html.append("<link href='[https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap](https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap)' rel='stylesheet'>");
        html.append(getSharedCSS());
        html.append("</head><body><div class='container'>");
        
        html.append("<div class='header'><h1>Edit Internship</h1><p>Update application details</p></div>");
        html.append("<div class='card'><h2>Update Details for ").append(record.get("name")).append("</h2>");
        
        // UPDATE Form
        html.append("<form method='POST' action='/'>");
        html.append("<input type='hidden' name='action' value='update'>");
        html.append("<input type='hidden' name='id' value='").append(id).append("'>");
        
        html.append("<div class='form-grid'>");
        html.append("<div class='form-group'><label>Student Name</label><input type='text' name='name' value='").append(record.get("name")).append("' required></div>");
        html.append("<div class='form-group'><label>Roll Number</label><input type='text' name='roll' value='").append(record.get("roll")).append("' required></div>");
        html.append("<div class='form-group'><label>Company Name</label><input type='text' name='company' value='").append(record.get("company")).append("' required></div>");
        html.append("<div class='form-group'><label>Job Role</label><input type='text' name='role' value='").append(record.get("role")).append("' required></div>");
        
        html.append("<div class='form-group'><label>Status</label><select name='status'>");
        String[] statuses = {"Applied", "Interview Scheduled", "Selected", "Rejected"};
        for (String s : statuses) {
            String selected = s.equals(record.get("status")) ? "selected" : "";
            html.append("<option value='").append(s).append("' ").append(selected).append(">").append(s).append("</option>");
        }
        html.append("</select></div>");
        
        html.append("</div>");
        html.append("<div style='display:flex; gap:10px; margin-top: 10px;'>");
        html.append("<button type='submit' style='flex:2;'>Save Updates</button>");
        html.append("<a href='/' class='btn-sm btn-edit' style='flex:1; padding: 15px; font-size: 1rem; border-radius: 10px;'>Cancel</a>");
        html.append("</div></form></div></div></body></html>");
        
        return html.toString();
    }
}