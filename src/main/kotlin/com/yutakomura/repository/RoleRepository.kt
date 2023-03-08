package com.yutakomura.repository

import com.yutakomura.entity.Role
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.jdbc.Result

@Dao
@ConfigAutowireable
interface RoleRepository {

    @Select
    fun selectByUserId(userId: Int): List<Role>

    @Insert
    fun insert(role: Role): Result<Role>

    @Update(sqlFile = true)
    fun update(role: Role): Result<Role>
}