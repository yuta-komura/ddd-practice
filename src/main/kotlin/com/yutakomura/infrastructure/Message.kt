package com.yutakomura.infrastructure

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class Message {
    companion object {

        private val messageSource = Container.getBean(MessageSource::class.java)

        fun of(id: String, params: Array<String>): String {
            return messageSource.getMessage(id, params, Locale.JAPAN)
        }
    }
}