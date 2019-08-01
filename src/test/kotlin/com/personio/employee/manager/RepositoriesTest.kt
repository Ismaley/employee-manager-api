package com.personio.employee.manager

import com.personio.employee.manager.model.Employee
import com.personio.employee.manager.repository.EmployeeRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import kotlin.streams.toList

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [EmployeeManagerApiApplication::class])
class RepositoriesTest {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository


    @Test
    fun shouldCreateAnEmployee() {
        val subordinates = employeeRepository.saveAll(listOf(
            Employee(name = "Pete"),
            Employee(name = "Barbara")
        ))

        val nick = employeeRepository.save(Employee(name = "nick", subordinates = subordinates))

        val sophie = employeeRepository.save(Employee(name = "sophie", subordinates = listOf(nick)))

        val jonas = employeeRepository.save(Employee(name = "jonas", subordinates = listOf(sophie)))

        val hierarchy: MutableMap<String, List<String>> = mutableMapOf()


        employeeRepository.findAll().forEach {
            hierarchy[it.name!!] = it.subordinates!!.stream().map { it.name!! }.toList()
        }

        println(hierarchy)
    }

}
