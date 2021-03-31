package com.thorin.consumerapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorin.consumerapp.R
import com.thorin.consumerapp.databinding.ActivityMainBinding
import com.thorin.consumerapp.viewmodel.FavViewModel
import com.thorin.consumerapp.viewmodel.UserListAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: UserListAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FavViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.Favorite_user)

        adapter = UserListAdapter()
        adapter.notifyDataSetChanged()

        viewModel = ViewModelProvider(this).get(FavViewModel::class.java)

        binding.apply {
            rvUserFav.setHasFixedSize(true)
            rvUserFav.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUserFav.adapter = adapter
        }

        viewModel.setFavorite(this)

        viewModel.getFavUser().observe(this, {
            when {
                it != null -> {
                    adapter.setList(it)
                }
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}