package com.yutakomura.domain.user

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import java.util.*

@Dao
@ConfigAutowireable
interface UserRepository {

    @Select
    fun selectByEmail(email: Email): Optional<UniqueUser>

    @Select
    fun selectByAaa(email: String): Optional<UniqueUser>

    @Select
    fun selectById(id: Id): Optional<UniqueUser>

    @Select
    fun selectNum(): Int

    @Insert(sqlFile = true)
    fun insert(email: Email, encodedPassword: EncodedPassword): Int
}
