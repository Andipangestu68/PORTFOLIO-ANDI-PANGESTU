package register;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.MD5;
import utils.DBUtil;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/register"})
public class Register extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DBUtil.connect();
        PrintWriter writer = resp.getWriter();
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
                "    <title>Salary Prediction | Register Page</title>");
        writer.println("</head>");
        writer.println("<body class=\"bg-light\">");
        writer.println("<section class=\"d-flex flex-column min-vh-100 justify-content-center align-content-center\">\n" +
                "        <div class=\"container\">\n" +
                "            <div class=\"row\">\n" +
                "                <div class=\"col-md-10 mx-auto rounded shadow bg-white\">\n" +
                "                    <div class=\"row\">\n" +
                "                        <h1 class=\"text-center mt-5\">Salary Prediction</h1>\n" +
                "                        \n" +
                "                        <div class=\"col-md-6\">\n" +
                "                            <div class=\"m-2\">\n" +
                "                                <h2 class=\"mb-2 text-center\">Create Your Account</h2>\n" +
                "                            </div>\n" +
                "                            <div class=\"m-5\">\n" +
                "                                <form action =\"register\" method=\"post\">\n" +
                "                                    <div class=\"form-floating mb-3\">\n" +
                "                                        <input type=\"text\" class=\"form-control\" id=\"username\" name=\"username\" placeholder=\"admin\">\n" +
                "                                        <label for=\"username\">Username</label>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"form-floating mb-3\">\n" +
                "                                         <input type=\"password\" class=\"form-control\" id=\"password\" name=\"password\" placeholder=\"password\">\n" +
                "                                         <label for=\"password\">Password</label>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"form-floating mb-3\">\n" +
                "                                        <input type=\"password\" class=\"form-control\" id=\"password\" name=\"confPassword\" placeholder=\"password\">\n" +
                "                                        <label for=\"password\">Password Confirmation</label>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"row mb-3\">\n" +
                "                                        <div class=\"col-6\">\n" +
                "                                            <div class=\"form-check\">\n" +
                "                                                <input class=\"form-check-input\" type=\"checkbox\" id=\"remember-me\">\n" +
                "                                                <label class=\"form-check-label\" for=\"remember-me\" >Remember Me</label>\n" +
                "                                            </div>\n" +
                "                                        </div>\n" +
                "                                        <div class=\"m-auto mt-3\">\n" +
                "                                            <button type=\"submit\" class=\"form-control btn btn-primary\">Create</button>\n" +
                "\n" +
                "                                        </div>\n" +
                "                                    </div>\n" +
                "                                </form> \n" +
                "                                <p class=\"mt-3 text-center\">Already have an account ? <a href=\"loginPage\"> Login Here</a></p>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                        <div class=\"col-md-6\">\n" +
                "                            <img src=\"https://www.allen.ac.in/apps2324/assets/images/reset-password.jpg\" class=\"img-fluid align-self-center p-5\" alt=\"...\" >\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </section>");
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
        writer.println("</body>");
        writer.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DBUtil.connect();
        PrintWriter writer = resp.getWriter();

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confPassword = req.getParameter("confPassword");
        String passwordMd5 = MD5.getMd5(password);


        writer.println("<html>");
        writer.println("<body>");
        if (username.isEmpty()||password.isEmpty()||confPassword.isEmpty()) {
            writer.println("<h2>Fill the column!</h2>");
            writer.println("<a href =\"register\">Kembali ke Register</a>");
            } else if (confPassword.equals(password)){
                if (DBUtil.register("members", username, passwordMd5)) {
                    resp.sendRedirect("loginPage");
                } else {
                    writer.println(username +" is failed!");
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
                    "                <h1 class=\"text-muted\">Check the password!!</h1>\n" +
                    "                <h4 class=\"text-muted\"><a href=\"register\">Back To Register Page</a></h4>\n" +
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


    }
}
