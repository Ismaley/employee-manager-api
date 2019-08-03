package com.personio.employee.manager.service

import com.personio.employee.manager.exception.EmployeeServiceException
import com.personio.employee.manager.exception.ErrorMessages
import com.personio.employee.manager.exception.NotFoundException
import com.personio.employee.manager.extensions.asMap
import com.personio.employee.manager.model.Employee
import com.personio.employee.manager.repository.EmployeeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class EmployeeServiceTest {

    private val employeeRepository = mockk<EmployeeRepository>()
    private val employeeService = EmployeeService(employeeRepository)

    @Rule
    @JvmField
    var expectedEx: ExpectedException = ExpectedException.none()

    @Test
    fun `should create a new hierarchy with a sorted input`() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        )

        val expectedBoss = buildExpectedBoss()
        val expectedHierarchy = buildExpectedHierarchy()

        every { employeeRepository.save(expectedBoss) } returns expectedBoss
        every { employeeRepository.deleteAll() } answers {}

        val hierarchy = employeeService.createEmployeeHierarchy(employees)

        verify(exactly = 1) { employeeRepository.deleteAll() }
        verify(exactly = 1) { employeeRepository.save(expectedBoss) }
        Assert.assertEquals(1, hierarchy.size)
        Assert.assertEquals(expectedHierarchy, hierarchy)
    }

    @Test
    fun `should create a new hierarchy with an unsorted input`() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Sophie" to "Jonas",
            "Nick" to "Sophie",
            "Barbara" to "Nick"
        )

        val expectedBoss = buildExpectedBoss()
        val expectedHierarchy = buildExpectedHierarchy()

        every { employeeRepository.save(expectedBoss) } returns expectedBoss
        every { employeeRepository.deleteAll() } answers {}

        val hierarchy = employeeService.createEmployeeHierarchy(employees)

        verify(exactly = 1) { employeeRepository.deleteAll() }
        verify(exactly = 1) { employeeRepository.save(expectedBoss) }
        Assert.assertEquals(1, hierarchy.size)
        Assert.assertEquals(expectedHierarchy, hierarchy)
    }

    @Test
    fun `should not build a new hierarchy if input contains a cyclic hierarchy`() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Pete"
        )

        verify(exactly = 0) { employeeRepository.deleteAll() }
        verify(exactly = 0) { employeeRepository.save(Employee()) }

        expectedEx.expect(EmployeeServiceException::class.java)
        expectedEx.expectMessage(ErrorMessages.INVALID_HIERARCHY_CYCLIC)

        employeeService.createEmployeeHierarchy(employees)
    }

    @Test
    fun `should not build a new hierarchy if input contains more than one boss`() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas",
            "Mike" to "Alan"
        )

        verify(exactly = 0) { employeeRepository.deleteAll() }
        verify(exactly = 0) { employeeRepository.save(Employee()) }

        expectedEx.expect(EmployeeServiceException::class.java)
        expectedEx.expectMessage(ErrorMessages.INVALID_HIERARCHY_MULTIPLE_BOSSES)

        employeeService.createEmployeeHierarchy(employees)
    }

    @Test
    fun `should not build a new hierarchy if input is empty`() {
        val employees = mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "",
            "Sophie" to "Jonas"
        )

        verify(exactly = 0) { employeeRepository.deleteAll() }
        verify(exactly = 0) { employeeRepository.save(Employee()) }

        expectedEx.expect(EmployeeServiceException::class.java)
        expectedEx.expectMessage(ErrorMessages.INVALID_HIERARCHY_MISSING_RELATION)

        employeeService.createEmployeeHierarchy(employees)
    }

    @Test
    fun `should not build a new hierarchy if an employee has no supervisor`() {
        val employees = emptyMap<String, String>()

        verify(exactly = 0) { employeeRepository.deleteAll() }
        verify(exactly = 0) { employeeRepository.save(Employee()) }

        expectedEx.expect(EmployeeServiceException::class.java)
        expectedEx.expectMessage(ErrorMessages.INVALID_HIERARCHY_EMPTY)

        employeeService.createEmployeeHierarchy(employees)
    }

    @Test
    fun `should find employee by name`() {
        val employeeName = "Nick"
        val expectedEmployee = buildExpectedEmployee(employeeName)

        every { employeeRepository.findByName(employeeName) } returns expectedEmployee

        val foundEmployee = employeeService.findEmployeeByName(employeeName)

        verify(exactly = 1) { employeeRepository.findByName(employeeName) }

        Assert.assertEquals(expectedEmployee.asMap(), foundEmployee)
    }

    @Test
    fun `should not find employee if it not exists`() {
        val employeeName = "Nick"

        every { employeeRepository.findByName(employeeName) } returns null

        expectedEx.expect(NotFoundException::class.java)
        expectedEx.expectMessage(ErrorMessages.notFoundError(employeeName))

        employeeService.findEmployeeByName(employeeName)
    }

    @Test
    fun `should not find employee if input is empty`() {
        val employeeName = ""

        expectedEx.expect(EmployeeServiceException::class.java)
        expectedEx.expectMessage(ErrorMessages.EMPTY_NAME)

        verify(exactly = 0) { employeeRepository.findByName(any()) }

        employeeService.findEmployeeByName(employeeName)
    }

    private fun buildExpectedEmployee(employeeName: String): Employee =
        Employee(
            name = employeeName, subordinates = listOf(
                Employee(name = "Pete"), Employee(name = "Barbara")
            )
        )


    private fun buildExpectedBoss(): Employee =
        Employee(
            name = "Jonas", subordinates = listOf(
                Employee(
                    name = "Sophie", subordinates = listOf(
                        Employee(
                            name = "Nick", subordinates = listOf(
                                Employee(name = "Pete"), Employee(name = "Barbara")
                            )
                        )
                    )
                )
            )
        )

    private fun buildExpectedHierarchy(): Map<String, Map<String, Any>> =
        mapOf<String, Map<String, Any>>(
            "Jonas" to mapOf(
                "Sophie" to mapOf<String, Map<String, Any>>(
                    "Nick" to mapOf<String, Map<String, Any>>(
                        "Pete" to emptyMap(),
                        "Barbara" to emptyMap()
                    )
                )
            )
        )
}