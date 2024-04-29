package org.acme.entities

import org.acme.entities.DynamoConstants.ENTITY_ID
import org.acme.entities.DynamoConstants.NESTED_ID
import org.acme.entities.DynamoConstants.NESTED_ID_INDEX
import org.acme.entities.DynamoConstants.SORT_KEY
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbFlatten
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
data class TestEntity(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute(ENTITY_ID)
    var testEntityId: String,

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute(SORT_KEY)
    var sortKey: String,

    @get:DynamoDbFlatten
    var nested: NestedTestEntity,
)

@DynamoDbBean
data class NestedTestEntity(
    @get:DynamoDbAttribute(NESTED_ID)
    @get:DynamoDbSecondaryPartitionKey(indexNames = [NESTED_ID_INDEX])
    var nestedId: String,
)
