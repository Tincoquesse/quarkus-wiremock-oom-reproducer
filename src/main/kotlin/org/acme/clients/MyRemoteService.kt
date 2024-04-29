package org.acme.clients

import io.smallrye.mutiny.Uni
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import org.acme.entities.TestEntity
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@Singleton
@RegisterRestClient(configKey = "test-api")
@Path("/api")
interface MyRemoteService {

    @GET
    @Path("/remote")
    fun getById(@QueryParam("id") id: String): Uni<TestEntity>
}
