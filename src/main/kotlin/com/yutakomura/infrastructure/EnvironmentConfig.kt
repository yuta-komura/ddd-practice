package com.yutakomura.infrastructure

import org.springframework.core.env.Environment

class EnvironmentConfig private constructor() {
    companion object {
        private val environment = Container.getBean(Environment::class.java)
        fun from(key: String): String {
            return environment.getProperty(key)
                ?: throw IllegalStateException("設定の値が存在しません。")
        }
    }
}
