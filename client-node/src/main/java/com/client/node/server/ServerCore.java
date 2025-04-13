package com.client.node.server;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerCore {

    @POST("client-nodes")
    Call<Void> registerNode(
            @Query("name") String name,
            @Query("host") String host,
            @Query("port") Integer port
    );

    @DELETE("client-nodes/{name}")
    Call<Void> unregisterNode(@Path("name") String name);
}
