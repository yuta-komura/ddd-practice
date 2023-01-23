package com.yutakomura.security

import com.fasterxml.jackson.annotation.JsonCreator

data class EmailJsonRequest
@JsonCreator constructor(val email: String)
