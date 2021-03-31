package com.thorin.consumerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataUsers(
        var login: String? = "",
        var id: Int = 0,
        var avatar_url: String? = "",
): Parcelable