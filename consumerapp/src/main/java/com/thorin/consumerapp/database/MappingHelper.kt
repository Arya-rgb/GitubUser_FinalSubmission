package com.thorin.consumerapp.database

import android.database.Cursor
import com.thorin.consumerapp.model.DataUsers

object MappingHelper {
    fun mapCursorToArrayList(cursor: Cursor?): ArrayList<DataUsers> {
        val list = ArrayList<DataUsers>()
        if (null != cursor) {
            while (cursor.moveToNext()) {
                val id =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.FavUserColumns.ID))
                val username =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavUserColumns.USERNAME))
                val avatarUrl =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavUserColumns.AVATAR_URL))
                list.add(
                    DataUsers(
                        username,
                        id,
                        avatarUrl
                    )
                )
            }
        }
        return list
    }
}