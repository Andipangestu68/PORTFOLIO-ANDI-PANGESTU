package menu;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

@WebServlet(urlPatterns = {"/updateCSV"})
@MultipartConfig(
        location = "/",
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024, // 1MB
        maxRequestSize = 1024 * 1024 // 1MB
)
public class UpdateCSV extends HttpServlet {
    private static final String SAVE_DIRECTORY = "uploads";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get the file part from the request
        Part filePart = req.getPart("fileToUpdate");

        // Extract file name from the part
        String fileName = getFileName(filePart);
//        System.out.println(fileName);

        // Create the save directory if it doesn't exist
        File saveDir = new File(SAVE_DIRECTORY);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }

        // Get the existing file to be updated
        File existingFile = new File(saveDir, String.valueOf(fileName));

        // If the existing file exists, delete it
        if (existingFile.exists()) {
            existingFile.delete();
        }

        // Save the new file to the server
        filePart.write(saveDir + File.separator + fileName);

        // Redirect or forward to a success page
        resp.sendRedirect("main");



    }
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

}
