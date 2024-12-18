package com.submission.submissionstoryapp.data.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Parcelize
@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey(autoGenerate = false)
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: Double?,

    @field:SerializedName("lat")
    val lat: Double?
) : Parcelable
