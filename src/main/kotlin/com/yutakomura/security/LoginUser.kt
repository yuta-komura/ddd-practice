package com.yutakomura.security

import org.springframework.security.core.authority.SimpleGrantedAuthority

data class LoginUser(val id: Int, val authorities: List<SimpleGrantedAuthority>)
