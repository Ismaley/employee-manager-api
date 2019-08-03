package com.personio.employee.manager.exception

object ErrorMessages {
    private const val INVALID_HIERARCHY = "Invalid employee hierarchy"
    const val NOT_FOUND = "Invalid employee hierarchy"
    const val INVALID_HIERARCHY_MISSING_RELATION = "$INVALID_HIERARCHY: Every employee in the hierarchy must have a supervisor associated."
    const val INVALID_HIERARCHY_EMPTY = "$INVALID_HIERARCHY: Employee hierarchy is empty."
    const val INVALID_HIERARCHY_CYCLIC = "$INVALID_HIERARCHY: Employee hierarchy contains a cyclic dependency, hierarchy must have a boss."
    const val INVALID_HIERARCHY_MULTIPLE_BOSSES = "$INVALID_HIERARCHY: Employee hierarchy contains multiple bosses, it must have exactly one boss."
    const val EMPTY_NAME = "Invalid search param: Employee name must not be blank."

    fun notFoundError(name: String) = "$NOT_FOUND: Employee $name not found"
}