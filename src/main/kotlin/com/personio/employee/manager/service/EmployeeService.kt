package com.personio.employee.manager.service

import com.personio.employee.manager.exception.EmployeeServiceException
import com.personio.employee.manager.exception.ErrorMessages
import com.personio.employee.manager.model.Employee
import com.personio.employee.manager.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap

@Service
class EmployeeService(private val employeeRepository: EmployeeRepository) {

    fun addEmployeeHierarchy(employees: Map<String, String>) : Map<String, Map<String, Any>> {
        val unsortedHierarchy = LinkedMultiValueMap<String, String>()
        val supervisors = mutableListOf<String>()
        val subordinates = mutableListOf<String>()

        validateNonEmpty(employees)
        employees.forEach {
            validateEmployeeSupervisorRelation(it.value)
            unsortedHierarchy.add(it.value, it.key)
            subordinates.add(it.key)
            supervisors.add(it.value)
        }

        val bossName = findTheBossOrFail(supervisors.toSet(), subordinates.toSet())
        val employee = traverseHierarchy(unsortedHierarchy, bossName)

        employeeRepository.deleteAll()

        return employeeRepository.save(employee).asMap()
    }

    private fun traverseHierarchy(hierarchy: Map<String, List<String>>, start: String): Employee {
        return Employee(name = start, subordinates = getEmployees(hierarchy, start))
    }

    private fun getEmployees(hierarchy: Map<String, List<String>>, boss: String): List<Employee> {
        val employees = hierarchy[boss] ?: emptyList()
        return employees.map { traverseHierarchy(hierarchy, it) }
    }

    private fun Employee.asMap(): Map<String, Map<String, Any>> {
        return mapOf(this.name!! to this.subordinates!!.asMap())
    }

    fun List<Employee>.asMap(): Map<String, Map<String, Any>> {
        val myMap: MutableMap<String, Map<String, Any>> = mutableMapOf()

        this.forEach { employee ->
            myMap[employee.name!!] = employee.subordinates!!.asMap()
        }

        return myMap
    }

    private fun validateEmployeeSupervisorRelation(supervisor: String) {
        if(supervisor.isBlank()) {
            throw EmployeeServiceException(ErrorMessages.INVALID_HIERARCHY_MISSING_RELATION)
        }
    }

    private fun validateNonEmpty(employees: Map<String, String>) {
        if(employees.isEmpty()) {
            throw EmployeeServiceException(ErrorMessages.INVALID_HIERARCHY_EMPTY)
        }
    }

    private fun findTheBossOrFail(supervisors: Set<String>, subordinates: Set<String>): String {
        val bosses = supervisors - subordinates
        return when {
            bosses.isEmpty() -> throw EmployeeServiceException(ErrorMessages.INVALID_HIERARCHY_CYCLIC)
            bosses.size > 1 -> throw EmployeeServiceException(ErrorMessages.INVALID_HIERARCHY_MULTIPLE_BOSSES)
            else -> bosses.first()
        }
    }

}