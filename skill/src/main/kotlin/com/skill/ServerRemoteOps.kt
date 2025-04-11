package com.skill

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RemoteOpsRequest(val message: String)

interface ServerRemoteOps {

    @POST("/remote-ops")
    suspend fun remoteOps(@Body body: RemoteOpsRequest): Response<Unit?>
}