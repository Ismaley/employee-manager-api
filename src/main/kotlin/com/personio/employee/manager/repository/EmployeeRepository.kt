package com.personio.employee.manager.repository

import com.personio.employee.manager.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface EmployeeRepository : JpaRepository<Employee, Long> {

    fun findByName(name: String): Employee?

    @Query("SELECT e from Employee e LEFT JOIN e.subordinates s WHERE s.name = :employeeName")
    fun findSupervisor(@Param("employeeName") employeeName: String): Employee?

}