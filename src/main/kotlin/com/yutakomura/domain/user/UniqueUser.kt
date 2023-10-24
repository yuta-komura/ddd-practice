package com.yutakomura.domain.user

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "user")
data class UniqueUser(
    @org.seasar.doma.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Id,
    @Column(name = "email")
    val email: Email,
    @Column(name = "password")
    val encodedPassword: EncodedPassword
)
