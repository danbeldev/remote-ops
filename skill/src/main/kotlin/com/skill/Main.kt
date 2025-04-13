package com.skill

import com.github.alice.ktx.dispatch
import com.github.alice.ktx.handlers.impl.message
import com.github.alice.ktx.handlers.impl.newSession
import com.github.alice.ktx.models.response.response
import com.github.alice.ktx.skill
import com.github.alice.ktx.webhook.impl.ktorWebhookServer
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object SkillConfig {
    private const val DEFAULT_SERVER_URL = "http://localhost:8184/server-core/api/v1/"

    val SERVER_BASE_URL = System.getenv("SERVER_BASE_URL") ?: DEFAULT_SERVER_URL
    const val WEBHOOK_PORT = 8080
    const val WEBHOOK_PATH = "/remote-ops"
    const val WELCOME_MESSAGE = "Привет! Я могу выполнять удаленные команды."
    const val SUCCESS_RESPONSE = "Команда успешно выполнена"
    const val ERROR_RESPONSE = "Не удалось выполнить команду"
}

class RemoteOpsService(private val api: ServerRemoteOps) {
    suspend fun executeCommand(message: String): Boolean {
        return try {
            val response = api.remoteOps(RemoteOpsRequest(message = message))
            response.isSuccessful
        } catch (e: HttpException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}

fun createRetrofitClient(baseUrl: String): ServerRemoteOps {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()
}

fun main() {
    val remoteOpsService = RemoteOpsService(
        createRetrofitClient(SkillConfig.SERVER_BASE_URL)
    )

    skill {
        webhookServer = ktorWebhookServer {
            port = SkillConfig.WEBHOOK_PORT
            path = SkillConfig.WEBHOOK_PATH
        }

        dispatch {
            newSession {
                response {
                    text = SkillConfig.WELCOME_MESSAGE
                }
            }

            message {
                val isSuccess = remoteOpsService.executeCommand(messageText)
                val responseText = if (isSuccess) {
                    SkillConfig.SUCCESS_RESPONSE
                } else {
                    SkillConfig.ERROR_RESPONSE
                }

                response { text = responseText }
            }
        }
    }.run()
}