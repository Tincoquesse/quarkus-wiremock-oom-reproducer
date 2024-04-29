package org.acme.config

import org.eclipse.microprofile.config.ConfigProvider
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType.HASH
import software.amazon.awssdk.services.dynamodb.model.KeyType.RANGE
import software.amazon.awssdk.services.dynamodb.model.ProjectionType.ALL
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType.S
import java.net.URI

class DynamoDbTestClientImpl<T>(
    private val partitionKey: String,
    private val tableName: String,
    private val sortKey: String?,
    private val secondaryPartitionKeys: List<Pair<String, String>>,
    clazz: Class<T>,
) : DynamoDbTestClient<T> {
    private val client: DynamoDbClient = initializeClient()
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient =
        DynamoDbEnhancedClient.builder()
            .dynamoDbClient(client)
            .build()

    private val tableSchema = TableSchema.fromClass(clazz)
    private val table: DynamoDbTable<T> = dynamoDbEnhancedClient.table(tableName, tableSchema)

    companion object {
        const val CAPACITY = 10L
        private const val DYNAMODB_ENDPOINT = "quarkus.dynamodb.endpoint-override"
        private const val AWS_REGION = "quarkus.dynamodb.aws.region"
        private const val ACCESS_KEY_ID = "quarkus.dynamodb.aws.credentials.static-provider.access-key-id"
        private const val SECRET_ACCESS_KEY = "quarkus.dynamodb.aws.credentials.static-provider.secret-access-key"

        private fun initializeClient(): DynamoDbClient {
            return DynamoDbClient.builder()
                .endpointOverride(URI.create(getConfigValue(DYNAMODB_ENDPOINT)))
                .region(Region.of(getConfigValue(AWS_REGION)))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            getConfigValue(ACCESS_KEY_ID),
                            getConfigValue(SECRET_ACCESS_KEY)
                        )
                    )
                )
                .build()
        }

        private fun getConfigValue(propertyName: String): String {
            return ConfigProvider.getConfig().getConfigValue(propertyName).rawValue
        }
    }

    @Suppress("SpreadOperator")
    override fun create() {
        client.createTable(
            CreateTableRequest.builder()
                .attributeDefinitions(setAttributeDefinitions())
                .keySchema(setKeySchema())
                .provisionedThroughput { it.readCapacityUnits(CAPACITY).writeCapacityUnits(CAPACITY) }
                .tableName(tableName)
                .applyMutation { if (secondaryPartitionKeys.isNotEmpty()) it.globalSecondaryIndexes(setGlobalSecondaryIndexes()) }
                .build()
        )
    }

    override fun destroy() {
        client.deleteTable { it.tableName(tableName) }
    }

    override fun persist(entity: Map<String, AttributeValue>) {
        client.putItem { it.tableName(tableName).item(entity) }
    }

    override fun persist(entity: T) {
        table.putItem(entity)
    }

    override fun getEntity(partitionKey: String, sortKey: String): T {
        return table.getItem { req -> req.key { it.partitionValue(partitionKey).sortValue(sortKey) } }
    }

    override fun getEntities(indexName: String, indexValue: String): List<T> {
        return table
            .index(indexName)
            .query { req -> req.queryConditional(QueryConditional.keyEqualTo { it.partitionValue(indexValue) }) }
            .flatMap { it.items() }
    }

    private fun setAttributeDefinitions(): Collection<AttributeDefinition> {
        val definitions = mutableListOf<AttributeDefinition>()

        definitions.add(AttributeDefinition.builder().attributeName(partitionKey).attributeType(S).build())
        sortKey?.let {
            definitions.add(AttributeDefinition.builder().attributeName(sortKey).attributeType(S).build())
        }
        secondaryPartitionKeys.map {
            definitions.add(AttributeDefinition.builder().attributeName(it.first).attributeType(S).build())
        }.toTypedArray()

        return definitions
    }

    private fun setKeySchema(): Collection<KeySchemaElement> {
        val keySchemas = mutableListOf<KeySchemaElement>()

        keySchemas.add(KeySchemaElement.builder().attributeName(partitionKey).keyType(HASH).build())
        sortKey?.let {
            keySchemas.add(KeySchemaElement.builder().attributeName(sortKey).keyType(RANGE).build())
        }

        return keySchemas
    }

    private fun setGlobalSecondaryIndexes(): Collection<GlobalSecondaryIndex> {
        val indexes = mutableListOf<GlobalSecondaryIndex>()

        secondaryPartitionKeys.map { pair ->
            indexes.add(
                GlobalSecondaryIndex.builder()
                    .indexName(pair.second)
                    .keySchema({ it.attributeName(pair.first).keyType(HASH) })
                    .projection { it.projectionType(ALL) }
                    .provisionedThroughput { it.readCapacityUnits(CAPACITY).writeCapacityUnits(CAPACITY) }
                    .build()
            )
        }

        return indexes
    }
}
