package org.acme.clients

import io.smallrye.mutiny.Uni
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

abstract class DynamoDbClientBase<T>(
    dynamoClient: DynamoDbEnhancedAsyncClient,
    tableName: String,
    clazz: Class<T>,
) : DynamoDbClient<T> {
    private val tableSchema = TableSchema.fromClass(clazz)
    private val table: DynamoDbAsyncTable<T> = dynamoClient.table(tableName, tableSchema)

    override fun getItem(keyBuilder: (Key.Builder) -> Unit): Uni<T> {
        return Uni.createFrom().completionStage { table.getItem(Key.builder().apply(keyBuilder).build()) }
    }

    override fun updateItem(item: T): Uni<T> {
        return Uni.createFrom().completionStage { table.updateItem(item) }
    }

    override fun deleteItem(keyBuilder: (Key.Builder) -> Unit): Uni<T?> {
        return Uni.createFrom().completionStage { table.deleteItem(Key.builder().apply(keyBuilder).build()) }
    }


}
