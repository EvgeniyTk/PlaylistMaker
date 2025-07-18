package com.example.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.data.dao.TrackDao
import com.example.playlistmaker.db.data.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
}