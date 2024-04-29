package org.acme

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.replaceWithUnit
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import org.acme.clients.DynamoDbClientImpl
import org.acme.clients.MyRemoteService
import org.acme.entities.TestEntity
import org.acme.enums.SourceData
import org.acme.enums.SourceData.DYNAMO
import org.acme.enums.SourceData.REMOTE
import org.eclipse.microprofile.rest.client.inject.RestClient

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class MyRemoteController(
    @RestClient private val remoteService: MyRemoteService,
    private val dynamoClient: DynamoDbClientImpl
) {

    @GET
    @Path("/{testEntityId}/profile/{sortKey}")
    fun getProfileByCustomerAccountId(
        @PathParam("testEntityId") testEntityId: String,
        @PathParam("sortKey") sortKey: String,
        @QueryParam("source") source: SourceData
        ): Uni<TestEntity> {

        return when(source){
            DYNAMO -> dynamoClient.getItem {sth -> sth.partitionValue(testEntityId).sortValue(sortKey) }
            REMOTE ->  remoteService.getById(testEntityId)
        }
    }

    @POST
    @Path("/profile")
    fun saveProfileByCustomerAccountId(
        dto: TestEntity
    ): Uni<Unit> {
        return dynamoClient.updateItem(dto)
            .replaceWithUnit()
    }
}