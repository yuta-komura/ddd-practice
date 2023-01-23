package com.yutakomura.repository

import com.yutakomura.entity.User
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.jdbc.Result
import java.util.*

@Dao
@ConfigAutowireable
interface UserRepository {

    @Select
    fun selectByEmail(email: String): Optional<User>

    @Select
    fun selectById(id: Int): Optional<User>

    @Select
    fun selectNum(): Int

    @Insert
    fun insert(user: User): Result<User>
}
