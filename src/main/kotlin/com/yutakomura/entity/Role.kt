package com.yutakomura.entity

import com.yutakomura.security.SecurityConfiguration
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Table

@Entity(immutable = true)
@Table(name = "role")
data class Role(
    @Column(name = "user_id")
    val userId: Int,
    @Column(name = "role")
    val role: String = SecurityConfiguration.ROLE_NORMAL
)