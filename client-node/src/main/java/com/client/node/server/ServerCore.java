package com.client.node.server;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerCore {

    @POST("management/client-node/connection")
    Call<Void> connect(
            @Query("name") String name,
            @Query("host") String host,
            @Query("port") Integer port
    );

    @POST("management/client-node/disconnect")
    Call<Void> disconnect(@Query("name") String name);
}
