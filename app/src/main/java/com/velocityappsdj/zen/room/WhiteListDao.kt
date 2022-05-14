package com.velocityappsdj.zen.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WhiteListDao {

    @Query("select * from white_list")
    fun getAllWhiteListedApps(): Flow<List<WhiteListEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToWhiteList(whiteListEntity: WhiteListEntity)

    @Delete
    suspend fun removeItem(whiteListEntity: WhiteListEntity)

    @Update
    suspend fun updateItem(whiteListEntity: WhiteListEntity)

    @Query("Select * from white_list where appId=:appId")
    suspend fun getItem(appId: String):List<WhiteListEntity>

}
