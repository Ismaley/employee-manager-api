package com.personio.employee.manager.exception

class EmployeeServiceException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : RuntimeException(message)