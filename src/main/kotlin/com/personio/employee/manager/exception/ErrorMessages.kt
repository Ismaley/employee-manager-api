package com.personio.employee.manager.exception

object ErrorMessages {
    const val INVALID_HIERARCHY_MISSING_RELATION = "Employee hierarchy invalid: every employee must have a supervisor associated"
    const val INVALID_HIERARCHY_EMPTY = "Employee hierarchy invalid: empty hierarchy"
    const val INVALID_HIERARCHY_CYCLIC = "Employee hierarchy invalid: cyclic dependency, hierarchy must have a boss"
    const val INVALID_HIERARCHY_MULTIPLE_BOSSES = "Employee hierarchy invalid: multiple bosses, hierarchy must have exactly one boss"
    const val EMPTY_NAME = "Employee name must not be blank"

    fun notFoundError(name: String) = "Employee $name not found"
}