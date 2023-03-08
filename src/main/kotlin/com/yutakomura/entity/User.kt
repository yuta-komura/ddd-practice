package com.yutakomura.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,
    @Column(name = "email")
    val email: String,
    @Column(name = "password")
    val password: String
)
