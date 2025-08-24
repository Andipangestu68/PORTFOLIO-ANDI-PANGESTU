package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class EmployeeController implements Initializable {

    @FXML private TextField nipField;
    @FXML private TextField namaField;
    @FXML private TextField jabatanField;
    @FXML private TextArea alamatField;
    @FXML private TextField emailField;
    @FXML private TextField noTelponField;

    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Long> noColumn;
    @FXML private TableColumn<Employee, String> nipColumn;
    @FXML private TableColumn<Employee, String> namaColumn;
    @FXML private TableColumn<Employee, String> jabatanColumn;
    @FXML private TableColumn<Employee, String> alamatColumn;
    @FXML private TableColumn<Employee, String> emailColumn;
    @FXML private TableColumn<Employee, String> noTelponColumn;

    @Autowired
    private EmployeeService employeeService;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private Employee selectedEmployee = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        loadEmployeeData();
        setupTableSelectionListener();
        setupButtons();
    }

    private void initializeTableColumns() {
        noColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nipColumn.setCellValueFactory(new PropertyValueFactory<>("nip"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        jabatanColumn.setCellValueFactory(new PropertyValueFactory<>("jabatan"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory<>("noTelpon"));

        employeeTable.setItems(employeeList);
    }

    private void setupTableSelectionListener() {
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedEmployee = newValue;
                        populateFields(newValue);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        selectedEmployee = null;
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                }
        );
    }

    private void setupButtons() {
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void populateFields(Employee employee) {
        nipField.setText(employee.getNip());
        namaField.setText(employee.getNama());
        jabatanField.setText(employee.getJabatan());
        alamatField.setText(employee.getAlamat());
        emailField.setText(employee.getEmail());
        noTelponField.setText(employee.getNoTelpon());
    }

    private void loadEmployeeData() {
        employeeList.clear();
        employeeList.addAll(employeeService.getAllEmployees());
    }

    @FXML
    private void handleSave() {
        if (validateFields()) {
            String nip = nipField.getText().trim();

            if (employeeService.existsByNip(nip)) {
                showAlert("Error", "NIP sudah ada dalam database!", Alert.AlertType.ERROR);
                return;
            }

            Employee employee = new Employee(
                    nip,
                    namaField.getText().trim(),
                    jabatanField.getText().trim(),
                    alamatField.getText().trim(),
                    emailField.getText().trim(),
                    noTelponField.getText().trim()
            );

            try {
                employeeService.saveEmployee(employee);
                loadEmployeeData();
                clearFields();
                showAlert("Sukses", "Data karyawan berhasil disimpan!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Gagal menyimpan data: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedEmployee != null && validateFields()) {
            String nip = nipField.getText().trim();

            if (employeeService.existsByNipAndNotId(nip, selectedEmployee.getId())) {
                showAlert("Error", "NIP sudah digunakan karyawan lain!", Alert.AlertType.ERROR);
                return;
            }

            selectedEmployee.setNip(nip);
            selectedEmployee.setNama(namaField.getText().trim());
            selectedEmployee.setJabatan(jabatanField.getText().trim());
            selectedEmployee.setAlamat(alamatField.getText().trim());
            selectedEmployee.setEmail(emailField.getText().trim());
            selectedEmployee.setNoTelpon(noTelponField.getText().trim());

            try {
                employeeService.saveEmployee(selectedEmployee);
                loadEmployeeData();
                clearFields();
                showAlert("Sukses", "Data karyawan berhasil diupdate!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Gagal mengupdate data: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEmployee != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Konfirmasi Hapus");
            confirmAlert.setHeaderText("Hapus Data Karyawan");
            confirmAlert.setContentText("Apakah Anda yakin ingin menghapus data karyawan " + selectedEmployee.getNama() + "?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    employeeService.deleteEmployee(selectedEmployee.getId());
                    loadEmployeeData();
                    clearFields();
                    showAlert("Sukses", "Data karyawan berhasil dihapus!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Error", "Gagal menghapus data: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        employeeTable.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        nipField.clear();
        namaField.clear();
        jabatanField.clear();
        alamatField.clear();
        emailField.clear();
        noTelponField.clear();
        selectedEmployee = null;
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (nipField.getText().trim().isEmpty()) {
            errors.append("- NIP tidak boleh kosong\n");
        }
        if (namaField.getText().trim().isEmpty()) {
            errors.append("- Nama tidak boleh kosong\n");
        }
        if (jabatanField.getText().trim().isEmpty()) {
            errors.append("- Jabatan tidak boleh kosong\n");
        }
        if (alamatField.getText().trim().isEmpty()) {
            errors.append("- Alamat tidak boleh kosong\n");
        }
        if (emailField.getText().trim().isEmpty()) {
            errors.append("- Email tidak boleh kosong\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("- Format email tidak valid\n");
        }
        if (noTelponField.getText().trim().isEmpty()) {
            errors.append("- No. Telpon tidak boleh kosong\n");
        }

        if (errors.length() > 0) {
            showAlert("Validasi Error", errors.toString(), Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}