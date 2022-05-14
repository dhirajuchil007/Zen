package com.velocityappsdj.zen.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchTimeDao {

    @Query("Select * from batch_time_entity order by timeStamp")
    fun getBatches(): Flow<List<BatchTimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBatch(batchTimeEntity: BatchTimeEntity)

    @Query("Select * from batch_time_entity where batchTime=:batchTime")
    fun getBatch(batchTime: String): Flow<List<BatchTimeEntity>>

    @Update
    suspend fun updateBatch(batchTimeEntity: BatchTimeEntity)

    @Delete
    suspend fun deleteBatch(batchTimeEntity: BatchTimeEntity)
}