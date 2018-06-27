package com.aldieemaulana.take.model.user

import com.google.gson.annotations.SerializedName

/**
 * Created by Al on 27/06/2018 for Cermati
 */

data class Response(
    @SerializedName("total_count")
    val totalCount: Int?,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean?,
    val items: List<User>
)
