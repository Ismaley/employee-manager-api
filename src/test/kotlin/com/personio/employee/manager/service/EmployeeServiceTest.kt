package com.personio.employee.manager.service

import com.personio.employee.manager.repository.EmployeeRepository
import io.mockk.mockkClass
import org.junit.Test

class EmployeeServiceTest {

    private val employeeRepository = mockkClass(EmployeeRepository::class)
    private val employeeService = EmployeeService(employeeRepository)

    @Test
    fun shouldAddNewEmployeesSorted() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        )

        employeeService.addEmployeeHierarchy(employees)
    }

    @Test
    fun shouldAddNewEmployeesUnsorted() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Sophie" to "Jonas",
            "Nick" to "Sophie",
            "Barbara" to "Nick"
        )

        employeeService.addEmployeeHierarchy(employees)
    }

    @Test
    fun shouldTrowExceptionIfHierarchyIsCyclic() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Pete"
        )

        employeeService.addEmployeeHierarchy(employees)
    }

    @Test
    fun shouldTrowExceptionIfHierarchyHasNoRootMember() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas",
            "Mike" to "Alan"
        )

        employeeService.addEmployeeHierarchy(employees)
    }
}