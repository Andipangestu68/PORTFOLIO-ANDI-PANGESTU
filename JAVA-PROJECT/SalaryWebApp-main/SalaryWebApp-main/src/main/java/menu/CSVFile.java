package menu;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.LoginPage;
import utils.DBUtil;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/exportCSV"})
public class CSVFile extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DBUtil.connect();
//        PrintWriter writer = resp.getWriter();
        ResultSet rs = null;
        if (LoginPage.isValid) {
            try {
                resp.setContentType("text/csv");
                resp.setHeader("Content-Disposition", "attachment; filename=\"SalaryData.csv\"");

                rs = DBUtil.selectAll("salary_view");
                ServletOutputStream outputStream = resp.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Write CSV headers
                writer.println("age,gender,education_level,years_of_experience,job_title,salary");

                // Write CSV data rows
                while (rs.next()) {
                    int age = rs.getInt("age");
                    String gender = rs.getString("gender");
                    String education_level = rs.getString("education_level");
                    float years_of_experience = rs.getFloat("years_of_experience");
                    String job_title = rs.getString("job_title");
                    int salary = rs.getInt("salary");
                    writer.println(age + "," + gender + "," + education_level + "," + years_of_experience + "," + job_title + "," + salary);
                }

                writer.flush();
                writer.close();

                outputStream.flush();
                outputStream.close();

                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
