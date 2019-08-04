package com.personio.employee.manager.controller

import com.personio.employee.manager.service.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
@RequestMapping("/api/employees")
class EmployeeController(private val employeeService: EmployeeService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun createHierarchy(@RequestBody employees: Map<String, String>): Map<String, Map<String, Any>> =
        employeeService.createEmployeeHierarchy(employees)

    @GetMapping("/supervisors")
    @ResponseBody
    fun findEmployeeSupervisors(@RequestParam employeeName: String): Map<String, String> =
        employeeService.findEmployeeSupervisors(employeeName)

}