package com.thorin.dicoding.widget

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Binder
import android.provider.BaseColumns
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.thorin.dicoding.R
import com.thorin.dicoding.model.DataUsers
import java.util.*


internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val databaseAuthority = "com.thorin.dicoding"
    private val userTableName = "favorite_user"
    private val databaseScheme = "content"
    private val databaseContentUri = "$databaseScheme://$databaseAuthority"
    private val userContentUri = "$databaseContentUri/$userTableName"

    private var list: List<DataUsers> = listOf()

    private var cursor: Cursor? = null

    internal class FavUserColumns : BaseColumns {
        companion object {
            const val ID = "id"
            const val USERNAME = "login"
            const val AVATAR_URL = "avatar_url"
        }
    }

    override fun onCreate() {}

    private fun Cursor.mapCursorToArrayList(): ArrayList<DataUsers> {
        val list = ArrayList<DataUsers>()
        while (moveToNext()) {
            val id =
                getInt(getColumnIndexOrThrow(FavUserColumns.ID))
            val username =
                getString(getColumnIndexOrThrow(FavUserColumns.USERNAME))
            val avatarUrl =
                getString(getColumnIndexOrThrow(FavUserColumns.AVATAR_URL))
            list.add(
                DataUsers(
                    username,
                    id,
                    avatarUrl
                )
            )
        }
        return list
    }

    override fun onDataSetChanged() {

        cursor?.close()
        val identifyToken = Binder.clearCallingIdentity()

        cursor = mContext.contentResolver?.query(userContentUri.toUri(), null, null, null, null)
        cursor?.let {
            list = it.mapCursorToArrayList()
        }

        Binder.restoreCallingIdentity(identifyToken)

    }

    override fun onDestroy() {

        cursor?.close()
        list = listOf()

    }

    override fun getCount(): Int = list.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)

        if (!list.isNullOrEmpty()) {
            // Set item stack view
            rv.apply {
                list[position].apply {
                    setImageViewBitmap(
                        R.id.widget_image_view, avatar_url?.toBitmap(mContext)
                    )
                    setTextViewText(
                        R.id.widget_username, login ?: login
                    )
                    setTextViewText(
                        R.id.widget_user_id, id.toString()
                    )
                }
            }
        }
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    private fun String?.toBitmap(context: Context): Bitmap {
        var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_default)

        val option = RequestOptions()
            .error(R.drawable.ic_default)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        try {
            Glide.with(context)
                .setDefaultRequestOptions(option)
                .asBitmap()
                .load(this)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmap = resource
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

}


