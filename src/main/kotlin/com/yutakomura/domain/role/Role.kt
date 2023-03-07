package com.yutakomura.domain.role

import com.yutakomura.domain.user.Id
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Table
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable

@Entity(immutable = true)
@Table(name = "role")
@Configurable(preConstruction = true)
class Role(
    @Column(name = "userid")
    val userid: Id,
    @Column(name = "value")
    val value: Value
) {
    @Autowired
    private lateinit var roleRepository: RoleRepository

    fun register(): Role {
        return roleRepository.insert(userid, value).entity
    }

}