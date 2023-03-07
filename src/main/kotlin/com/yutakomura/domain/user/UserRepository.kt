package com.yutakomura.domain.user

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
    fun selectByEmail(email: Email): Optional<UniqueUser>

    @Select
    fun selectById(id: Int): Optional<UniqueUser>

    @Select
    fun selectNum(): Int

    @Insert
    fun insert(id: Id?, email: Email, encodedPassword: EncodedPassword): Result<UniqueUser>
}
