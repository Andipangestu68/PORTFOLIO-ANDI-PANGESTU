package org.example.repository;


import org.example.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByNip(String nip);
    boolean existsByNip(String nip);
    boolean existsByEmail(String email);
}