package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: PlaylistTrackEntity)

    @Query("SELECT * FROM track_in_playlist_table WHERE trackId IN (:ids)")
    fun getTracksByIds(ids: List<Int>): Flow<List<PlaylistTrackEntity>>
}