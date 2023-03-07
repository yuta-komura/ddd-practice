package com.yutakomura.domain.user

import org.seasar.doma.Domain

@Domain(valueType = Int::class)
data class Id(val value: Int)