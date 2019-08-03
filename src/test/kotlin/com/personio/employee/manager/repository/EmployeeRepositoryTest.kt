package com.personio.employee.manager.repository

import com.personio.employee.manager.EmployeeManagerApiApplication
import com.personio.employee.manager.model.Employee
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [EmployeeManagerApiApplication::class])
class EmployeeRepositoryTest {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Test
    fun `should persist employees and their subordinates`() {
        val pete = Employee(name = "Pete")
        val barbara = Employee(name = "Barbara")

        val nick = employeeRepository.save(Employee(name = "Nick", subordinates = listOf(pete, barbara)))

        Assert.assertNotNull(nick.id)
        Assert.assertEquals("Nick", nick.name)
        Assert.assertEquals(2, nick.subordinates!!.size)
        Assert.assertNotNull(nick.subordinates!![0].id)
        Assert.assertEquals("Pete", nick.subordinates!![0].name)
        Assert.assertEquals(2, nick.subordinates!!.size)
        Assert.assertNotNull(nick.subordinates!![1].id)
        Assert.assertEquals("Barbara", nick.subordinates!![1].name)
    }

    @Test
    fun `should find supervisor`() {

         employeeRepository.save(Employee(name = "Jonas", subordinates = listOf(
            Employee(name = "Sophie", subordinates = listOf()))))

        val supervisor = employeeRepository.findSupervisor("Sophie")

        Assert.assertNotNull(supervisor)
        Assert.assertEquals("Jonas", supervisor!!.name)
    }

    @Test
    fun `should not find supervisor if it's the boss`() {

        employeeRepository.save(Employee(name = "Jonas", subordinates = listOf(
            Employee(name = "Sophie", subordinates = listOf()))))

        val supervisor = employeeRepository.findSupervisor("Jonas")

        Assert.assertNull(supervisor)
    }
}
