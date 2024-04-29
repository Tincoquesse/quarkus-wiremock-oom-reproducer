package org.acme.clients

import io.smallrye.mutiny.Uni
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException

interface DynamoDbClient<T> {
    @Throws(DynamoDbException::class)
    fun getItem(keyBuilder: (Key.Builder) -> Unit): Uni<T>

    @Throws(DynamoDbException::class)
    fun updateItem(item: T): Uni<T>

    @Throws(DynamoDbException::class)
    fun deleteItem(keyBuilder: (Key.Builder) -> Unit): Uni<T?>
}
