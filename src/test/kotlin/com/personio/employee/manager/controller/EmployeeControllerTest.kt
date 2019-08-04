package com.personio.employee.manager.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.personio.employee.manager.EmployeeManagerApiApplication
import com.personio.employee.manager.exception.ErrorMessages
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [EmployeeManagerApiApplication::class])
@AutoConfigureMockMvc
class EmployeeControllerTest {

    private val employeesResource = "/api/employees"
    private val mapper = ObjectMapper()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should successfully create an employee hierarchy`() {
        val employees = mapper.writeValueAsString(mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        ))

        val expectedHierarchy = mapOf<String, Map<String, Any>>(
            "Jonas" to mapOf(
                "Sophie" to mapOf<String, Map<String, Any>>(
                    "Nick" to mapOf<String, Map<String, Any>>(
                        "Pete" to emptyMap(),
                        "Barbara" to emptyMap()
                    )
                )
            )
        )

        mockMvc.perform(post(employeesResource)
            .accept(MediaType.APPLICATION_JSON)
            .content(employees)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andExpect(content().string(mapper.writeValueAsString(expectedHierarchy)))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not build a new hierarchy if input contains more than one boss`() {
        val employees = mapper.writeValueAsString(mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas",
            "Mike" to "Alan"
        ))

        mockMvc.perform(post(employeesResource)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(employees))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.reason").value(ErrorMessages.INVALID_HIERARCHY_MULTIPLE_BOSSES.split(":")[0]))
            .andExpect(jsonPath("$.message").value(ErrorMessages.INVALID_HIERARCHY_MULTIPLE_BOSSES.split(":")[1]))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should successfully find an employee's supervisors`() {
        val employees = mapper.writeValueAsString(mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        ))

        val expectedHierarchy = mapper.writeValueAsString(mapOf(
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        ))

        mockMvc.perform(post(employeesResource)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(employees))
            .andExpect(status().isCreated)

        mockMvc.perform(get("$employeesResource/supervisors")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .param("employeeName", "Barbara"))
            .andExpect(status().isOk)
            .andExpect(content().string(expectedHierarchy))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find an employee by name if name is empty`() {
        val employees = mapper.writeValueAsString(mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        ))


        mockMvc.perform(post(employeesResource)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(employees))
            .andExpect(status().isCreated)

        mockMvc.perform(get("$employeesResource/supervisors")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .param("employeeName", ""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.reason").value(ErrorMessages.EMPTY_NAME.split(":")[0]))
            .andExpect(jsonPath("$.message").value(ErrorMessages.EMPTY_NAME.split(":")[1]))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find an employee by name if employee does not exist`() {
        val notFoundEmployeeName = "Mike"
        val employees = mapper.writeValueAsString(mapOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        ))

        mockMvc.perform(post(employeesResource)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(employees))
            .andExpect(status().isCreated)

        mockMvc.perform(get("$employeesResource/supervisors")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .param("employeeName", notFoundEmployeeName))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.reason").value(ErrorMessages.NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ErrorMessages.notFoundError(notFoundEmployeeName).split(":")[1]))
            .andDo(MockMvcResultHandlers.print())
    }
}