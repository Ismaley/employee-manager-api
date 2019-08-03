package com.personio.employee.manager.model

import org.hibernate.annotations.Cascade
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinTable
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String? = null,

    @JoinTable(name="subordinates")
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    val subordinates: List<Employee>? = emptyList()

//    @JoinTable(name = "supervisor")
//    @ManyToOne(fetch = FetchType.EAGER)
//    val supervisor: Employee? = null
)