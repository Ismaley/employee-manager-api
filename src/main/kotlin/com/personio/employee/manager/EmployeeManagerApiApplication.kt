package com.personio.employee.manager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["com.personio.employee.manager.model"])
@EnableJpaRepositories(basePackages = ["com.personio.employee.manager.repository"])
@SpringBootApplication
class EmployeeManagerApiApplication

fun main(args: Array<String>) {
	runApplication<EmployeeManagerApiApplication>(*args)
}
