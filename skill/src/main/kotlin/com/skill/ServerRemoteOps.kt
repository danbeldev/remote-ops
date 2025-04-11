package com.skill

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface ServerRemoteOps {

    @POST("/remote-ops")
    suspend fun remoteOps(@Query("message") message: String): Response<Unit?>
}