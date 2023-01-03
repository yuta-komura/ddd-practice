package com.yutakomura

import org.seasar.doma.Entity
import org.seasar.doma.Id

@Entity(immutable = true)
data class DemoTable(@Id val id: Int, val name: String)