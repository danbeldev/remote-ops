package com.client.node.server;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerCore {

    @POST("/connection")
    Call<Void> connect(
            @Query("name") String name,
            @Query("host") String host,
            @Query("port") Integer port
    );

    @POST("/disconnect")
    Call<Void> disconnect(@Query("name") String name);
}
