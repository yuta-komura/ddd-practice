//package com.yutakomura.controller
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.redis.core.StringRedisTemplate
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RequestMethod
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//@RequestMapping(value = ["/hoge-string"])
//class HogeStringController {
//
//    @Autowired
//    private lateinit var redisTemplate: StringRedisTemplate
//
//    @RequestMapping(method = [RequestMethod.PUT])
//    @Throws(Exception::class)
//    fun put(@RequestBody value: Hoge) {
//        redisTemplate.opsForValue()["hoge-string:string"] = value.string.toString()
//        redisTemplate.delete("hoge-string:list")
//        redisTemplate.opsForList().rightPushAll("hoge-string:list", value.list!!)
//        redisTemplate.delete("hoge-string:map")
//        redisTemplate.opsForHash<Any, Any>().putAll("hoge-string:map", value.map)
//    }
//
//    @RequestMapping(method = [RequestMethod.GET])
//    @Throws(Exception::class)
//    fun get(): Hoge {
//        val hoge = Hoge(
//            redisTemplate.opsForValue()["hoge-string:string"],
//            redisTemplate.opsForList().range("hoge-string:list", 0, -1),
//            redisTemplate.opsForHash<String, String>().entries("hoge-string:map")
//        )
//        return hoge
//    }
//}