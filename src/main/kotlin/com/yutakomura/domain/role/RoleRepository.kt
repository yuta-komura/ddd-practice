package com.yutakomura.domain.role

import com.yutakomura.domain.user.Id
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable

@Dao
@ConfigAutowireable
interface RoleRepository {

    @Select
    fun selectByUserId(userId: Id): List<GivenRole>

    @Insert(sqlFile = true)
    fun insert(userId: Id, value: Value): Int

    @Update(sqlFile = true)
    fun updateByUserId(userId: Id, value: Value): Int
}