package org.acme.clients

import jakarta.inject.Singleton
import org.acme.entities.TestEntity
import org.eclipse.microprofile.config.inject.ConfigProperty
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient

@Singleton
class DynamoDbClientImpl(
    client: DynamoDbEnhancedAsyncClient,
    @ConfigProperty(name = "gco.dynamodb.table-name") tableName: String,
) : DynamoDbClientBase<TestEntity>(
        dynamoClient = client,
        tableName = tableName,
        clazz = TestEntity::class.java
    )
