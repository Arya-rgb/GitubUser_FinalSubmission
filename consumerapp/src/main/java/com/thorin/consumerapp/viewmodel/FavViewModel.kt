package com.thorin.consumerapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.thorin.consumerapp.database.DatabaseContract
import com.thorin.consumerapp.database.MappingHelper
import com.thorin.consumerapp.model.DataUsers

class FavViewModel(application: Application) : AndroidViewModel(application) {

    private var list = MutableLiveData<ArrayList<DataUsers>>()

    fun setFavorite(context: Context) {
        val cursor = context.contentResolver.query(
            DatabaseContract.FavUserColumns.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        val listConvert = MappingHelper.mapCursorToArrayList(cursor)
        list.postValue(listConvert)
    }

    fun getFavUser(): LiveData<ArrayList<DataUsers>> {
        return list
    }

}