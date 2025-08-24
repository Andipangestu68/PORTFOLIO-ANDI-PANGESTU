package org.example;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        Application.launch(JavaFxApplication.class, args);
    }
}