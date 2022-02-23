package com.wolfheros.wmedia.http

import com.wolfheros.wmedia.WMedia
import com.wolfheros.wmedia.util.Util
import com.wolfheros.wmedia.value.StaticValues
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

lateinit var job : Job
fun startApi(){
    job = GlobalScope.launch {
        embeddedServer(Netty, 8080) {
            routing {
                route("/kind"){
                    get("{kind}") {
                        val kind = call.parameters["kind"]?.let {
                                StaticValues.getKindApi(it.toInt())
                        }
                        Util.logOutput("获取到HttpAPI链接 -> $kind")
                        call.respondText(WMedia.getResult(kind), status = HttpStatusCode.OK)
                    }
                }
            }
        }.start(wait = true)
    }
}

fun restartApi(){
    Util.logOutput("restart Api servers.")
    job.cancel()
    startApi()
}