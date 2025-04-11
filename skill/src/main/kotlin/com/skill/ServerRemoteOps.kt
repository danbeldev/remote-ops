package com.skill

import retrofit2.Response
import retrofit2.http.POST

interface ServerRemoteOps {

    @POST("/remote-ops")
    suspend fun remoteOps(message: String): Response<Unit?>
}