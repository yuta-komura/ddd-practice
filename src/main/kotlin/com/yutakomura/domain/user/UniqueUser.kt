package com.yutakomura.domain.user

import org.seasar.doma.*
import org.springframework.beans.factory.annotation.Configurable

@Entity(immutable = true)
@Table(name = "user")
@Configurable(preConstruction = true)
data class UniqueUser(
    @org.seasar.doma.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Id,
    @Column(name = "email")
    val email: Email,
    @Column(name = "password")
    val password: Password
)
