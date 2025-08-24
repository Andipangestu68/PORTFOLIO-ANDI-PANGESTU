package menu;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import jdk.jpackage.internal.Log;
import jakarta.servlet.http.HttpSession;
import login.LoginPage;
import utils.DBUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/main"})
public class Main extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DBUtil.connect();
        PrintWriter writer = resp.getWriter();
        ResultSet rs = null;
        if (LoginPage.isValid) {
            try {
                rs = DBUtil.selectAll("salary_view");
                writer.println("<html>");
                writer.println("  <head>\n" +
                        "    <!-- Required meta tags -->\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "\n" +
                        "    <link rel=\"icon\" type=\"image/png\" href=\"https://lh3.googleusercontent.com/pw/AJFCJaX6FIwtFds-OdncRF5Eh4NpiaWx9y5nUmMn_KI2YHqRBY-rhoEmGjXsieFX5V3bhcPAEg-SIlGvKLeMjiSxyeCsQ2zhMfdYuaChvvgSOjIggEivvoXooPOzbyLRGe2WhfjeVswUVey-x_OVNAHnJDA=w190-h185-s-no?authuser=0\">\n" +
                        "    <!-- Bootstrap CSS -->\n" +
                        "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC\" crossorigin=\"anonymous\">\n" +
                        "    <link rel=\"stylesheet\" href=\"https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css\">\n" +
                        "    <title>Salary Prediction | Main</title>\n" +
                        "  </head>");
                writer.println("<body>");
                writer.println("    <header class=\"py-3 mb-3 bg-dark text-white\">\n" +
                        "        <div class=\"container-fluid d-grid gap-3 align-items-center\" style=\"grid-template-columns: 1fr 2fr;\">\n" +
                        "            <ul class=\"text-center m-auto\">\n" +
                        "                <li>\n" +
                        "                    <h4 class=\"m-1\">Salary Prediction</h4>\n" +
                        "                </li>\n" +
                        "            </ul>\n" +
                        "          <div class=\"d-flex justify-content-end\">    \n" +
                        "            <div class=\"flex-shrink-0 dropdown\">\n" +
                        "              <a href=\"#\" class=\"d-block link-light text-decoration-none dropdown-toggle\" id=\"dropdownUser2\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">\n" +
                        "                <img src=\"https://cdn-icons-png.flaticon.com/512/3135/3135715.png\" alt=\"mdo\" width=\"32\" height=\"32\" class=\"rounded-circle\">\n" +
                        "              </a>\n" +
                        "              <ul class=\"dropdown-menu text-small shadow\" aria-labelledby=\"dropdownUser2\" style=\"\">\n" +
                        "                <li><a class=\"dropdown-item\" href=\"logout\">Sign out</a></li>\n" +
                        "              </ul>\n" +
                        "            </div>\n" +
                        "          </div>\n" +
                        "        </div>\n" +
                        "    </header>");
                writer.println("    <div class=\"container-fluid\">\n" +
                        "      <div class=\"row\">\n" +
                        "        <nav id=\"sidebarMenu\" class=\"col-md-3 col-lg-2 d-md-block bg-light sidebar collapse\">\n" +
                        "          <div class=\"position-sticky pt-3\">\n" +
                        "            <ul class=\"nav flex-column\">\n" +
                        "              <li class=\"nav-item\">\n" +
                        "                <a class=\"nav-link active\" aria-current=\"page\" href=\"main\" style=\"color: black;\">\n" +
                        "                  View Data\n" +
                        "                </a>\n" +
                        "              </li>\n");
                if (LoginPage.isAdmin) {
                    writer.println("<li class=\"nav-item\">\n" +
                            "                <button class=\"btn btn-toggle align-items-center rounded\" data-bs-toggle=\"collapse\" data-bs-target=\"#create-collapse\" aria-expanded=\"false\">\n" +
                            "                  Create Data\n" +
                            "                </button>\n" +
                            "                <div class=\"collapse\" id=\"create-collapse\" style=\"\">\n" +
                            "                  <ul class=\"btn-toggle-nav list-unstyled fw-normal pb-1 small\">\n" +
                            "                    <li><a href=\"createDataUser\" class=\"link-dark rounded m-4\">User</a></li>\n" +
                            "                    <li><a href=\"createDataJob\" class=\"link-dark rounded m-4\">Job</a></li>\n" +
                            "                    <li><a href=\"createDataRelation\" class=\"link-dark rounded m-4\">Relation</a></li>\n" +
                            "                  </ul>\n" +
                            "                </div>" +
                            "              </li>\n" +
                            "              <li class=\"nav-item\">\n" +
                            "                <button class=\"btn btn-toggle align-items-center rounded\" data-bs-toggle=\"collapse\" data-bs-target=\"#update-collapse\" aria-expanded=\"false\">\n" +
                            "                  Update Data\n" +
                            "                </button>\n" +
                            "                <div class=\"collapse\" id=\"update-collapse\" style=\"\">\n" +
                            "                  <ul class=\"btn-toggle-nav list-unstyled fw-normal pb-1 small\">\n" +
                            "                    <li><a href=\"getDataUser\" class=\"link-dark rounded m-4\">User</a></li>\n" +
                            "                    <li><a href=\"getDataJob\" class=\"link-dark rounded m-4\">Job</a></li>\n" +
                            "                    <li><a href=\"getDataRelation\" class=\"link-dark rounded m-4\">Relation</a></li>\n" +
                            "                  </ul>\n" +
                            "                </div>" +
                            "              </li>\n" +
                            "              <li class=\"nav-item\">\n" +
                            "                <button class=\"btn btn-toggle align-items-center rounded\" data-bs-toggle=\"collapse\" data-bs-target=\"#delete-collapse\" aria-expanded=\"false\">\n" +
                            "                  Delete Data\n" +
                            "                </button>\n" +
                            "                <div class=\"collapse\" id=\"delete-collapse\" style=\"\">\n" +
                            "                  <ul class=\"btn-toggle-nav list-unstyled fw-normal pb-1 small\">\n" +
                            "                    <li><a href=\"deleteDataUser\" class=\"link-dark rounded m-4\">User</a></li>\n" +
                            "                    <li><a href=\"deleteDataJob\" class=\"link-dark rounded m-4\">Job</a></li>\n" +
                            "                    <li><a href=\"deleteDataRelation\" class=\"link-dark rounded m-4\">Relation</a></li>\n" +
                            "                  </ul>\n" +
                            "                </div>" +
                            "              </li>\n");
                }
                writer.println("              <li class=\"nav-item\">\n" +
                        "                <a class=\"nav-link\" href=\"predictData\" style=\"color: black;\">\n" +
                        "                  Predict Salary\n" +
                        "                </a>\n" +
                        "              </li>\n" +
                        "            </ul>\n" +
                        "          </div>\n" +
                        "        </nav>\n" +
                        "        <main class=\"col-md-9 ms-sm-auto col-lg-10 px-md-4\">\n" +
                        "          <div class=\"d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom\">\n" +
                        "            <h1 class=\"h2\">Salary Dataset</h1>\n" +
                        "            <div class=\"btn-toolbar mb-2 mb-md-0\">\n" +
                        "              <div class=\"btn-group me-2\">\n" +
//                        "                 <form action=\"updateCSV\" method=\"post\" enctype=\"multipart/form-data\">"+
////                        "                <button type=\"submit\" class=\"btn btn-sm btn-outline-secondary\">Export</a>\n" +
//                        "                  <button class=\"btn btn-sm btn-outline-secondary\" type=\"button\" data-bs-toggle=\"modal\" data-bs-target=\"#staticBackdrop\">Update</button>\n" +
//                        "               <!-- Modal -->\n" +
//                        "                  <div class=\"modal fade\" id=\"staticBackdrop\" data-bs-backdrop=\"static\" data-bs-keyboard=\"false\" tabindex=\"-1\" aria-labelledby=\"staticBackdropLabel\" aria-hidden=\"true\">\n" +
//                        "                    <div class=\"modal-dialog modal-dialog-centered\">\n" +
//                        "                      <div class=\"modal-content\">\n" +
//                        "                        <div class=\"modal-header\">\n" +
//                        "                          <h5 class=\"modal-title\" id=\"staticBackdropLabel\">Confirmation</h5>\n" +
//                        "                          <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>\n" +
//                        "                        </div>\n" +
//                        "                        <div class=\"modal-body\">\n" +
//                        "                          <input type=\"file\" name=\"fileToUpdate\">\n" +
//                        "                        </div>\n" +
//                        "                        <div class=\"modal-footer\">\n" +
//                        "                          <button type=\"button\" class=\"btn btn-danger\" data-bs-dismiss=\"modal\">Close</button>\n" +
//                        "                          <button type=\"submit\" class=\"btn btn-primary\">Update</button>\n" +
//                        "                        </div>\n" +
//                        "                      </div>\n" +
//                        "                    </div>\n" +
//                        "                  </div>\n"+
//                        "                 </form>"+
                        "                 <form action=\"exportCSV\" method=\"post\">"+
                        "                <button type=\"submit\" class=\"btn btn-sm btn-outline-secondary\">Export</a>\n" +
                        "                 </form>"+
                        "              </div>\n" +
                        "            </div>\n" +
                        "          </div>\n");
                writer.println("<table id=\"example\" class=\"table table-striped\" style=\"width:100%\">\n" +
                        "                <thead>\n" +
                        "                    <tr>\n" +
                        "                        <th>ID</th>\n" +
                        "                        <th>Age</th>\n" +
                        "                        <th>Gender</th>\n" +
                        "                        <th>Education Level</th>\n" +
                        "                        <th>Years Of Experience</th>\n" +
                        "                        <th>Job Title</th>\n" +
                        "                        <th>Salary</th>\n" +
                        "                    </tr>\n" +
                        "                </thead>\n" +
                        "                <tbody>");
                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    Integer age = rs.getInt("age");
                    String gender = rs.getString("gender");
                    String education_level = rs.getString("education_level");
                    Float years_experience = rs.getFloat("years_of_experience");
                    String job_title = rs.getString("job_title");
                    Integer salary = rs.getInt("salary");
                    writer.println("\n" +
                            "                <tr>\n" +
                            "                  <td>"+ id +"</td>\n" +
                            "                  <td>"+ age +"</td>\n" +
                            "                  <td>"+ gender +"</td>\n" +
                            "                  <td>"+ education_level +"</td>\n" +
                            "                  <td>"+ years_experience +"</td>\n" +
                            "                  <td>"+ job_title +"</td>\n" +
                            "                  <td>"+ salary +"</td>\n" +
                            "                </tr>\n" );

                }
                writer.println("        </tbody>\n" +
                        "                <tfoot>\n" +
                        "                    <tr>\n" +
                        "                        <th>ID</th>\n" +
                        "                        <th>Age</th>\n" +
                        "                        <th>Gender</th>\n" +
                        "                        <th>Education Level</th>\n" +
                        "                        <th>Years Of Experience</th>\n" +
                        "                        <th>Job Title</th>\n" +
                        "                        <th>Salary</th>\n" +
                        "                    </tr>\n" +
                        "                </tfoot>\n" +
                        "            </table>\n" +
                        "        </main> \n" +
                        "      </div>\n" +
                        "    </div>");
                writer.println("</body>");
                writer.println(" <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM\" crossorigin=\"anonymous\"></script>\n" +
                        "        <script src=\"https://code.jquery.com/jquery-3.5.1.js\"></script>\n" +
                        "        <script src=\"https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js\"></script>\n" +
                        "        <script src=\"https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js\"></script>\n");
                writer.println("<script>\n" +
                        "            $(document).ready(function () {\n" +
                        "            $('#example').DataTable();\n" +
                        "            });\n" +
                        "        </script>");
                writer.println("</html>");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            LoginPage.unAuthorizedAccess(resp);
        }
    }
}
