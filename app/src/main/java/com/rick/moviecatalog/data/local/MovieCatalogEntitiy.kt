package com.rick.moviecatalog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.moviecatalog.data.model.Result

@Entity
data class MovieCatalogEntitiy(
    @PrimaryKey val id: Int? = null,
    val movieCatalog: List<Result>,
    val hasMore: Boolean
)