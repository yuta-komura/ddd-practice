package com.yutakomura.domain.role


import com.yutakomura.domain.user.Id
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Table

@Entity(immutable = true)
@Table(name = "role")
data class GivenRole(
    @Column(name = "user_id")
    val userId: Id,
    @Column(name = "value")
    val value: Value
)