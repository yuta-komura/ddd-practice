package com.yutakomura

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {
    @Autowired
    lateinit var demoTableDao: DemoTableDao

    @GetMapping("/")
    fun index(): String = demoTableDao.selectAll().toString()
}