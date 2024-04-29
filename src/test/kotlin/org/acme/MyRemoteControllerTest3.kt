package org.acme

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.acme.entities.TestEntity
import org.acme.enums.SourceData.DYNAMO
import org.acme.enums.SourceData.REMOTE
import org.acme.models.TestEntityModel
import org.acme.utils.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@QuarkusTest
class MyRemoteControllerTest3 : BaseTest() {

    @Test
    fun `testCaseForReproducer#first`() {
        val givenId = "firstId"
        val sortKey = TestEntityModel.basic().sortKey
        val givenEntity = TestEntityModel.basic().copy(testEntityId = givenId)

        dynamoTestRepository.persist(givenEntity)

        Given {
            pathParam("test_id", givenId)
            pathParam("sort_key", sortKey)
            queryParam("source", DYNAMO.name)
        } When {
            get("/test/{test_id}/profile/{sort_key}")
        } Then {
            statusCode(200)
        } Extract {
            val result = body().`as`(TestEntity::class.java)
            assertEquals(result, givenEntity)
        }
    }

    @Test
    fun `testCaseForReproducer#sec`() {
        val givenId = "secID"
        val sortKey = TestEntityModel.basic().sortKey
        val givenEntity = TestEntityModel.basic().copy(testEntityId = givenId)
        val givenDto = ObjectMapper().writeValueAsString(givenEntity)

        Given {
            contentType(JSON)
            body(givenDto.trimIndent())
        } When {
            post("/test/profile")
        } Then {
            statusCode(200)
        } Extract {
            val resultFromDb = dynamoTestRepository.getEntity(givenId, sortKey)
            assertEquals(resultFromDb, givenEntity)
        }
    }

    @Test
    fun `testCaseForReproducer#third`() {
        val givenId = "thirdID"
        val sortKey = TestEntityModel.basic().sortKey
        val givenEntity = TestEntityModel.basic().copy(testEntityId = givenId)
        val givenDto = ObjectMapper().writeValueAsString(givenEntity)

        Given {
            wiremock.register(
                get(urlPathMatching("/api/remote"))
                    .withQueryParam("id", equalTo(givenId))
                    .willReturn(okJson(givenDto))
            )
            pathParam("test_id", givenId)
            pathParam("sort_key", sortKey)
            queryParam("source", REMOTE.name)
        } When {
            get("/test/{test_id}/profile/{sort_key}")
        } Then {
            statusCode(200)
        } Extract {
            val result = body().`as`(TestEntity::class.java)
            assertEquals(result, givenEntity)
        }
    }

    @Test
    fun `testCaseForReproducer#fourth`() {
        val givenId = "fourthID"
        val sortKey = TestEntityModel.basic().sortKey
        val givenEntity = TestEntityModel.basic().copy(testEntityId = givenId)

        dynamoTestRepository.persist(givenEntity)

        Given {
            pathParam("test_id", givenId)
            pathParam("sort_key", sortKey)
            queryParam("source", DYNAMO.name)
        } When {
            get("/test/{test_id}/profile/{sort_key}")
        } Then {
            statusCode(200)
        } Extract {
            val result = body().`as`(TestEntity::class.java)
            assertEquals(result, givenEntity)
        }
    }

    @Test
    fun `testCaseForReproducer#fifth`() {
        val givenId = "fifthID"
        val sortKey = TestEntityModel.basic().sortKey
        val givenEntity = TestEntityModel.basic().copy(testEntityId = givenId)
        val givenDto = ObjectMapper().writeValueAsString(givenEntity)

        Given {
            contentType(JSON)
            body(givenDto.trimIndent())
        } When {
            post("/test/profile")
        } Then {
            statusCode(200)
        } Extract {
            val resultFromDb = dynamoTestRepository.getEntity(givenId, sortKey)
            assertEquals(resultFromDb, givenEntity)
        }
    }

    @Test
    fun `testCaseForReproducer#sixth`() {
        val givenId = "sixthID"
        val givenEntity = TestEntityModel.basic().copy(testEntityId = givenId)
        val sortKey = TestEntityModel.basic().sortKey
        val givenDto = ObjectMapper().writeValueAsString(givenEntity)

        Given {
            wiremock.register(
                get(urlPathMatching("/api/remote"))
                    .withQueryParam("id", equalTo(givenId))
                    .willReturn(okJson(givenDto))
            )
            pathParam("test_id", givenId)
            pathParam("sort_key", sortKey)
            queryParam("source", REMOTE.name)
        } When {
            get("/test/{test_id}/profile/{sort_key}")
        } Then {
            statusCode(200)
        } Extract {
            val result = body().`as`(TestEntity::class.java)
            assertEquals(result, givenEntity)
        }
    }
}
