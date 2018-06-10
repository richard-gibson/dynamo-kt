package dynamokt.request

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import arrow.core.*

data class DynamoPutRequest(
        val tableName: String,
        val item: AttributeValue,
        val condition: Option<RequestCondition>
)

data class DynamoDeleteRequest(
        val tableName: String,
        val key: Map<String, AttributeValue>,
        val condition: Option<RequestCondition>
)

data class DynamoUpdateRequest(
        val tableName: String,
        val key: Map<String, AttributeValue>,
        val updateExpression: String,
        val attributeNames: Map<String, String>,
        val attributeValues: Map<String, AttributeValue>,
        val condition: Option<RequestCondition>
)

data class DynamoScanRequest(
        val tableName: String,
        val index: Option<String>,
        val options: DynamoQueryOptions
)

data class DynamoQueryRequest(
        val tableName: String,
        val index: Option<String>,
//        val query: Query<_>,
        val query: String,
//        val query: QueryHK, //TODO: HK
        val options: DynamoQueryOptions
)
data class DynamoQueryOptions(
        val consistent: Boolean,
        val limit: Option<Int>,
        val exclusiveStartKey: Option<Map<String, AttributeValue>>,
        val filter: Option<String>){ //TODO: HK
//        val filter: Option<ConditionHK>){
    companion object {
        val default = DynamoQueryOptions(false, None, None, None)
    }
}



data class RequestCondition(
        val expression: String,
        val attributeNames: Map<String, String>,
        val attributeValues: Option<Map<String, AttributeValue>>
)
