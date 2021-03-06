package co.brightcog.dynamo.ops

import arrow.core.Either
import arrow.core.FunctionK
import arrow.free.Free
import arrow.free.foldMap
import arrow.free.instances.FreeMonadInstance
import arrow.higherkind
import arrow.typeclasses.Monad
import co.brightcog.dynamo.request.*
import com.amazonaws.services.dynamodbv2.model.*

typealias DynamoOpsIO<T> = Free<DynamoOpsHK, T>


@higherkind
sealed class DynamoOps<out A> : DynamoOpsKind<A> {
    data class Put(val req: DynamoPutRequest) : DynamoOps<PutItemResult>()
    data class ConditionalPut(val req: DynamoPutRequest) : DynamoOps<Either<ConditionalCheckFailedException, PutItemResult>>()
    data class Get(val req: GetItemRequest) : DynamoOps<GetItemResult>()
    data class Delete(val req: DynamoDeleteRequest) : DynamoOps<DeleteItemResult>()
    data class ConditionalDelete(val req: DynamoDeleteRequest) : DynamoOps<Either<ConditionalCheckFailedException, DeleteItemResult>>()
    data class Scan(val req: DynamoScanRequest) : DynamoOps<ScanResult>()
    data class Query(val req: DynamoQueryRequest) : DynamoOps<QueryResult>()
    data class BatchWrite(val req: BatchWriteItemRequest) : DynamoOps<BatchWriteItemResult>()
    data class BatchGet(val req: BatchGetItemRequest) : DynamoOps<BatchGetItemResult>()
    data class Update(val req: DynamoUpdateRequest) : DynamoOps<UpdateItemResult>()
    data class ConditionalUpdate(val req: DynamoUpdateRequest) : DynamoOps<Either<ConditionalCheckFailedException, UpdateItemResult>>()


    companion object : FreeMonadInstance<DynamoOpsHK> {

        fun put(req: DynamoPutRequest): DynamoOpsIO<PutItemResult> = Free.liftF(Put(req))
        fun conditionalPut(req: DynamoPutRequest): DynamoOpsIO<Either<ConditionalCheckFailedException, PutItemResult>> =
                Free.liftF(ConditionalPut(req))

        fun get(req: GetItemRequest): DynamoOpsIO<GetItemResult> = Free.liftF(Get(req))
        fun delete(req: DynamoDeleteRequest): DynamoOpsIO<DeleteItemResult> = Free.liftF(Delete(req))
        fun conditionalDelete(req: DynamoDeleteRequest): DynamoOpsIO<Either<ConditionalCheckFailedException, DeleteItemResult>> =
                Free.liftF(ConditionalDelete(req))

        fun scan(req: DynamoScanRequest): DynamoOpsIO<ScanResult> = Free.liftF(Scan(req))
        fun query(req: DynamoQueryRequest): DynamoOpsIO<QueryResult> = Free.liftF(Query(req))
        fun batchWrite(req: BatchWriteItemRequest): DynamoOpsIO<BatchWriteItemResult> =
                Free.liftF(BatchWrite(req))

        fun batchGet(req: BatchGetItemRequest): DynamoOpsIO<BatchGetItemResult> =
                Free.liftF(BatchGet(req))

        fun update(req: DynamoUpdateRequest): DynamoOpsIO<UpdateItemResult> =
                Free.liftF(Update(req))

        fun conditionalUpdate(req: DynamoUpdateRequest): DynamoOpsIO<Either<ConditionalCheckFailedException, UpdateItemResult>> =
                Free.liftF(ConditionalUpdate(req))

        fun <M, A> run(program: DynamoOpsIO<A>, interpreter: FunctionK<DynamoOpsHK, M>, m: Monad<M>) =
                program.foldMap(interpreter, m)
    }
}
