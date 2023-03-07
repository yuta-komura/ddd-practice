package com.yutakomura.domain.role

import com.yutakomura.domain.user.Id
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.jdbc.Result

@Dao
@ConfigAutowireable
interface RoleRepository {

    @Select
    fun selectByUserId(userId: Id): List<Role>

    @Insert
    fun insert(userId: Id, value: Value): Result<Role>

    fun updateByUserId(userId: Id, value: Value): Result<Role>
}