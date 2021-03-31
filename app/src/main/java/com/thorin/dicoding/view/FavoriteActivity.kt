package com.thorin.dicoding.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorin.dicoding.R
import com.thorin.dicoding.databinding.ActivityFavoriteBinding
import com.thorin.dicoding.model.DataUsers
import com.thorin.dicoding.model.FavoriteUser
import com.thorin.dicoding.view.detail.UserDetailActivity
import com.thorin.dicoding.viewmodel.FavViewModel
import com.thorin.dicoding.viewmodel.UserListAdapter

class FavoriteActivity : AppCompatActivity() {

    private lateinit var adapter: UserListAdapter
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var viewModel: FavViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.Favorite_user)

        adapter = UserListAdapter()
        adapter.notifyDataSetChanged()

        viewModel = ViewModelProvider(this).get(FavViewModel::class.java)

        adapter.setOnItemClickCallBack(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataUsers) {
                Intent(this@FavoriteActivity, UserDetailActivity::class.java).also {
                    it.putExtra(UserDetailActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(UserDetailActivity.EXTRA_ID, data.id)
                    it.putExtra(UserDetailActivity.EXTRA_URL, data.avatar_url)
                    startActivity(it)
                }
            }
        })


        binding.apply {
            rvUserFav.setHasFixedSize(true)
            rvUserFav.layoutManager = LinearLayoutManager(this@FavoriteActivity)
            rvUserFav.adapter = adapter
        }

        viewModel.getFavUser()?.observe(this, {
            if (null != it) {
                val list = mapList(it)
                adapter.setList(list)
            }
        })

    }

    private fun mapList(users: List<FavoriteUser>): ArrayList<DataUsers> {
        val listUsers = ArrayList<DataUsers>()
        for (user in users) {
            val userMapped = DataUsers(
                user.login,
                user.id,
                user.avatar_url

            )
            listUsers.add(userMapped)
        }
        return listUsers
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}