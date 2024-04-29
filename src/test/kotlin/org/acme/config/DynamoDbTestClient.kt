package org.acme.config

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

interface DynamoDbTestClient<T> {
    fun create()

    fun destroy()

    fun persist(entity: T)

    fun persist(entity: Map<String, AttributeValue>)

    fun getEntity(partitionKey: String, sortKey: String): T

    fun getEntities(indexName: String, indexValue: String): List<T>
}
