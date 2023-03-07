package com.yutakomura.infrastructure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class Message {
    companion object {
        @Autowired
        private lateinit var messageSource: MessageSource
        fun of(id: String, params: Array<String>): String {
            return messageSource.getMessage(id, params, Locale.JAPAN)
        }
    }
}