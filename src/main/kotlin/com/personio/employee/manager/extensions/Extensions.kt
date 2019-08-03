package com.personio.employee.manager.extensions

import com.personio.employee.manager.model.Employee

fun Employee.asMap(): Map<String, Map<String, Any>> {
    return mapOf(this.name!! to this.subordinates!!.asMap())
}

fun List<Employee>.asMap(): Map<String, Map<String, Any>> {
    val employees: MutableMap<String, Map<String, Any>> = mutableMapOf()

    this.forEach { employee ->
        employees[employee.name!!] = employee.subordinates!!.asMap()
    }

    return employees
}