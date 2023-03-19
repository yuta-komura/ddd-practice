package com.yutakomura.domain.user

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable

@Dao
@ConfigAutowireable
interface UserRepository {

    @Select
    fun selectByEmail(email: Email): UniqueUser?

    @Select
    fun selectByAaa(email: String): UniqueUser?

    @Select
    fun selectById(id: Id): UniqueUser?

    @Select
    fun selectNum(): Int

    @Insert(sqlFile = true)
    fun insert(email: Email, encodedPassword: EncodedPassword): Int
}
