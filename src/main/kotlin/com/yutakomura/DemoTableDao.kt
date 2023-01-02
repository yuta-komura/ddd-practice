package com.yutakomura

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.jdbc.Result


@Dao
@ConfigAutowireable
interface DemoTableDao {
    @Insert
    fun insert(entity: DemoTable): Result<DemoTable>

    @Select
    fun selectAll(): List<DemoTable>
}