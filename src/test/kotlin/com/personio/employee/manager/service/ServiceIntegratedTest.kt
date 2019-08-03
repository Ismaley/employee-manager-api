package com.personio.employee.manager.service

import com.personio.employee.manager.EmployeeManagerApiApplication
import com.personio.employee.manager.model.Employee
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import kotlin.streams.toList

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [EmployeeManagerApiApplication::class])
class ServiceIntegratedTest {

    @Autowired
    private lateinit var employeeService: EmployeeService


    @Test
    fun shouldCreateHierarchy() {

        employeeService.addEmployeeHierarchy(mapOf(
            "Pete" to "Nick",
            "Sophie" to "Jonas",
            "Barb" to "Nick",
            "Nick" to "Sophie"
        ))

    }

}
