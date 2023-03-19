package com.yutakomura.infrastructure

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
class Container private constructor() : ApplicationContextAware {

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        sApplicationContext = applicationContext
    }

    companion object {
        private var sApplicationContext: ApplicationContext by Delegates.notNull()

        fun <T> getBean(clazz: Class<T>): T {
            return sApplicationContext.getBean(clazz)
        }

        fun beanDefinitionNames(): Array<String> {
            return sApplicationContext.beanDefinitionNames
        }
    }
}