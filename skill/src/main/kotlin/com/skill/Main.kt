package com.skill

import com.github.alice.ktx.dispatch
import com.github.alice.ktx.handlers.impl.message
import com.github.alice.ktx.handlers.impl.newSession
import com.github.alice.ktx.models.response.response
import com.github.alice.ktx.skill
import com.github.alice.ktx.webhook.impl.ktorWebhookServer
import retrofit2.Retrofit
import retrofit2.create

private const val SERVER_REMOTE_OPS_BASE_URL = "https://skill.matstart.ru:30/remote-ops/server-core/"

fun main() {
    val serverRemoteOps = Retrofit.Builder()
        .baseUrl(SERVER_REMOTE_OPS_BASE_URL)
        .build()
        .create<ServerRemoteOps>()

    skill {
        webhookServer = ktorWebhookServer {
            port = 8080
            path = "/remote-ops"
        }
        dispatch {
            newSession {
                response {
                    text = "Привет"
                }
            }
            message {
                val response = serverRemoteOps.remoteOps(RemoteOpsRequest(message = messageText))
                println(response.code())
                println(response.message())

                response {
                    text = if (response.isSuccessful)
                        "Команда была успешно выполнена"
                    else
                        "Не удалось выполнить команду"
                }
            }
        }
    }.run()
}