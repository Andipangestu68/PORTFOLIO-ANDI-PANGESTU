package login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.DBUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/loginPage"})


public class LoginPage extends HttpServlet {
    public static boolean isValid = false;
    public static boolean isAdmin = false;

    public static void unAuthorizedAccess(HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC\" crossorigin=\"anonymous\">\n" +
                "\n" +
                "    <title>Salary Prediction | Unauthorized Access</title>");
        writer.println("</head>");
        writer.println("<body class=\"text-center\" style=\"background-color: #E0E0E0\">\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"row  d-flex justify-content-center align-items-center min-vh-100\">\n" +
                "            <div class=\"col-12\">\n" +
                "                <h1 class=\"text-muted\">Cannot be accessed</h1>\n" +
                "                <h4 class=\"text-muted\"><a href=\"index.jsp\">Back To Login Page</a></h4>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>");
        writer.println("<!-- Optional JavaScript; choose one of the two! -->\n" +
                "\n" +
                "    <!-- Option 1: Bootstrap Bundle with Popper -->\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM\" crossorigin=\"anonymous\"></script>\n" +
                "\n" +
                "    <!-- Option 2: Separate Popper and Bootstrap JS -->\n" +
                "    <!--\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js\" integrity=\"sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js\" integrity=\"sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF\" crossorigin=\"anonymous\"></script>\n" +
                "    -->");
        writer.println("</html>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DBUtil.connect();
        PrintWriter writer = resp.getWriter();
        writer.println("<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"https://lh3.googleusercontent.com/pw/AJFCJaX6FIwtFds-OdncRF5Eh4NpiaWx9y5nUmMn_KI2YHqRBY-rhoEmGjXsieFX5V3bhcPAEg-SIlGvKLeMjiSxyeCsQ2zhMfdYuaChvvgSOjIggEivvoXooPOzbyLRGe2WhfjeVswUVey-x_OVNAHnJDA=w190-h185-s-no?authuser=0\">\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC\" crossorigin=\"anonymous\">\n" +
                "\n" +
                "    <title>Salary Prediction | Login Page</title>\n" +
                "  </head>");
        writer.println("<body class=\"bg-light\">\n" +
                "    <section class=\"d-flex flex-column min-vh-100 justify-content-center align-content-center\">\n" +
                "            <div class=\"container\">\n" +
                "                <div class=\"row\">\n" +
                "                    <div class=\"col-md-10 mx-auto rounded shadow bg-white\">\n" +
                "                        <div class=\"row\">\n" +
                "                            <h1 class=\"text-center mt-5\">Salary Prediction</h1>\n" +
                "                            <div class=\"col-md-6\">\n" +
                "                                <img src=\"https://img.freepik.com/free-vector/mobile-login-concept-illustration_114360-83.jpg?w=2000\" class=\"img-fluid align-self-center p-5\" alt=\"...\" >\n" +
                "                            </div>\n" +
                "                            <div class=\"col-md-6\">\n" +
                "                                <div class=\"m-5\">\n" +
                "                                    <h1 class=\"mb-5 text-center\">Welcome !</h1>\n" +
                "                                </div>\n" +
                "                                <div class=\"m-5\">\n" +
                "                                    <form action =\"loginPage\" method=\"post\">\n" +
                "                                        <div class=\"form-floating mb-3\">\n" +
                "                                            <input type=\"text\" class=\"form-control\" id=\"username\" name=\"username\" placeholder=\"admin\">\n" +
                "                                            <label for=\"username\">Username</label>\n" +
                "                                        </div>\n" +
                "                                        <div class=\"form-floating mb-3\">\n" +
                "                                             <input type=\"password\" class=\"form-control\" id=\"password\" name=\"password\" placeholder=\"password\">\n" +
                "                                             <label for=\"password\">Password</label>\n" +
                "                                        </div>\n" +
                "                                        <div class=\"row mb-3\">\n" +
                "                                            <div class=\"col-6\">\n" +
                "                                                <div class=\"form-check\">\n" +
                "                                                    <input class=\"form-check-input\" type=\"checkbox\" id=\"remember-me\">\n" +
                "                                                    <label class=\"form-check-label\" for=\"remember-me\" >Remember Me</label>\n" +
                "                                                </div>\n" +
                "                                            </div>\n" +
                "                                            <div class=\"m-auto mt-3\">\n" +
                "                                                <button type=\"submit\" class=\"form-control btn btn-primary\">Login</button>\n" +
                "\n" +
                "                                            </div>\n" +
                "                                        </div>\n" +
                "                                    </form>\n" +
                "                                    <p class=\"mt-3 text-center\">Don't have an account ? <a href=\"register\"> Register Here</a></p>\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </section>");
        writer.println("<!-- Optional JavaScript; choose one of the two! -->\n" +
                "\n" +
                "    <!-- Option 1: Bootstrap Bundle with Popper -->\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM\" crossorigin=\"anonymous\"></script>\n" +
                "\n" +
                "    <!-- Option 2: Separate Popper and Bootstrap JS -->\n" +
                "    <!--\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js\" integrity=\"sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js\" integrity=\"sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF\" crossorigin=\"anonymous\"></script>\n" +
                "    -->\n" +
                "  </body>\n" +
                "</html>");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DBUtil.connect();
        ResultSet resultSet = null;
        PrintWriter writer = resp.getWriter();

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String passwordMD5 = MD5.getMd5(password);

        writer.println("<html><body>");
        try {
            resultSet=DBUtil.selectUser(username, passwordMD5);
            if(resultSet.next()) {
                resp.sendRedirect("main");
                isValid = true;
                String role = resultSet.getString("role");
                if (role.equals("admin")) {
                    isAdmin = true;
                } else {
                    isAdmin = false;
                }
            } else {
                writer.println("<html>");
                writer.println("<head>");
                writer.println("<!-- Required meta tags -->\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "\n" +
                        "    <link rel=\"icon\" type=\"image/png\" href=\"https://lh3.googleusercontent.com/pw/AJFCJaX6FIwtFds-OdncRF5Eh4NpiaWx9y5nUmMn_KI2YHqRBY-rhoEmGjXsieFX5V3bhcPAEg-SIlGvKLeMjiSxyeCsQ2zhMfdYuaChvvgSOjIggEivvoXooPOzbyLRGe2WhfjeVswUVey-x_OVNAHnJDA=w190-h185-s-no?authuser=0\">\n" +
                        "    <!-- Bootstrap CSS -->\n" +
                        "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC\" crossorigin=\"anonymous\">\n" +
                        "\n" +
                        "    <title>Salary Prediction</title>");
                writer.println("</head>");
                writer.println("<body class=\"text-center\" style=\"background-color: #E0E0E0\">\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"row  d-flex justify-content-center align-items-center min-vh-100\">\n" +
                        "            <div class=\"col-12\">\n" +
                        "                <h1 class=\"text-muted\">Username/password invalid</h1>\n" +
                        "                <h4 class=\"text-muted\"><a href=\"loginPage\">Back To Login Page</a></h4>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>");
                writer.println("<!-- Optional JavaScript; choose one of the two! -->\n" +
                        "\n" +
                        "    <!-- Option 1: Bootstrap Bundle with Popper -->\n" +
                        "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM\" crossorigin=\"anonymous\"></script>\n" +
                        "\n" +
                        "    <!-- Option 2: Separate Popper and Bootstrap JS -->\n" +
                        "    <!--\n" +
                        "    <script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js\" integrity=\"sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p\" crossorigin=\"anonymous\"></script>\n" +
                        "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js\" integrity=\"sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF\" crossorigin=\"anonymous\"></script>\n" +
                        "    -->");
                writer.println("</html>");
                isValid = false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        writer.println("</body></html>");
        writer.flush();

    }
}
