package com.yutakomura.domain.user

import org.seasar.doma.Domain

@Domain(valueType = String::class)
data class Password(val value: String)
