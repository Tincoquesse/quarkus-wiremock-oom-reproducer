package org.acme.utils

import org.acme.config.DynamoDbTestClient
import org.acme.config.DynamoDbTestClientImpl
import org.acme.entities.TestEntity
import org.acme.entities.DynamoConstants.ENTITY_ID
import org.acme.entities.DynamoConstants.NESTED_ID
import org.acme.entities.DynamoConstants.NESTED_ID_INDEX
import org.acme.entities.DynamoConstants.SORT_KEY
import org.eclipse.microprofile.config.ConfigProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach

//@ConnectWireMock
open class BaseTest : WiremockTestResource() {

    companion object {
//        lateinit var wiremock: WireMock
        lateinit var dynamoTestRepository: DynamoDbTestClient<TestEntity>

        @JvmStatic
        @BeforeAll
        fun init() {
            val testTableName = ConfigProvider.getConfig().getConfigValue("gco.dynamodb.table-name").rawValue
            dynamoTestRepository =
                DynamoDbTestClientImpl(
                    partitionKey = ENTITY_ID,
                    tableName = testTableName,
                    sortKey = SORT_KEY,
                    secondaryPartitionKeys = listOf(Pair(NESTED_ID, NESTED_ID_INDEX)),
                    clazz = TestEntity::class.java
                )
        }
    }
    @BeforeEach
    fun setUp() {
        dynamoTestRepository.create()
    }

    @AfterEach
    fun tearDown() {
        dynamoTestRepository.destroy()
        wiremock.resetRequests()
    }
}