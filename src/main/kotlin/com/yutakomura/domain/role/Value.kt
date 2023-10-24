package com.yutakomura.domain.role

import org.seasar.doma.Domain

@Domain(valueType = String::class)
data class Value(val value: String = "free")