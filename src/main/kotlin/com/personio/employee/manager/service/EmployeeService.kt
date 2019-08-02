package com.personio.employee.manager.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.personio.employee.manager.exception.EmployeeServiceException
import com.personio.employee.manager.model.Employee
import com.personio.employee.manager.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap

@Service
class EmployeeService(private val employeeRepository: EmployeeRepository) {

    fun addEmployeeHierarchy(employees: Map<String, String>) {
        val unsortedHierarchy = LinkedMultiValueMap<String, String>()
        val supervisors = mutableListOf<String>()
        val subordinates = mutableListOf<String>()

        employees.forEach {
            unsortedHierarchy.add(it.value, it.key)
            subordinates.add(it.key)
            supervisors.add(it.value)
        }

        val bossName = findTheBoss(supervisors.toSet(), subordinates.toSet())
        println(bossName)

    }


    private fun findTheBoss(supervisors: Set<String>, subordinates: Set<String>): String {
        val bosses = supervisors - subordinates
        return when {
            supervisors.isEmpty() && subordinates.isEmpty() -> throw EmployeeServiceException("Employee hierarchy invalid: empty hierarchy")
            bosses.isEmpty() -> throw EmployeeServiceException("Employee hierarchy invalid: cyclic dependency, hierarchy must have a boss")
            bosses.size > 1 -> throw EmployeeServiceException("Employee hierarchy invalid: multiple bosses, hierarchy must have exactly one boss")
            else -> bosses.first()
        }
    }

}