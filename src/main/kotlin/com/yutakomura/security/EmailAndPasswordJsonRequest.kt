package com.yutakomura.security

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class EmailAndPasswordJsonRequest
@JsonCreator constructor(@JsonProperty("email") val email: String, @JsonProperty("password") val password: String)
