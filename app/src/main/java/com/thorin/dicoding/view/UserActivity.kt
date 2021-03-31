package com.thorin.dicoding.view


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorin.dicoding.R
import com.thorin.dicoding.databinding.ActivityUserBinding
import com.thorin.dicoding.model.DataUsers
import com.thorin.dicoding.sharedpref.SharedPrefNightMode
import com.thorin.dicoding.view.detail.UserDetailActivity
import com.thorin.dicoding.viewmodel.UserListAdapter
import com.thorin.dicoding.viewmodel.UserViewModel
import java.util.*

class UserActivity : AppCompatActivity() {


    private lateinit var sharedpref: SharedPrefNightMode
    private lateinit var adapter: UserListAdapter
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = UserListAdapter()
        adapter.notifyDataSetChanged()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(UserViewModel::class.java)

        adapter.setOnItemClickCallBack(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataUsers) {
                Intent(this@UserActivity, UserDetailActivity::class.java).also {
                    it.putExtra(UserDetailActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(UserDetailActivity.EXTRA_ID, data.id)
                    it.putExtra(UserDetailActivity.EXTRA_URL, data.avatar_url)
                    startActivity(it)
                }
            }
        })

        binding.rvUser.layoutManager = LinearLayoutManager(this@UserActivity)
        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.adapter = adapter
        isNightMode()

        viewModel.status.observe(this, {
            it?.let(fun(_: Boolean) {
                when (it) {
                    false -> {
                        onDataError()
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.onstartimage.visibility = View.INVISIBLE
                        binding.rvUser.visibility = View.INVISIBLE
                        binding.onConnectionerror.visibility = View.VISIBLE
                    }
                }
            })
        })

        viewModel.getSearchQuery().observe(this, fun(it: ArrayList<DataUsers>) {
            showLoading(false)
            adapter.setList(it)
            binding.rvUser.visibility = View.VISIBLE
            binding.onstartimage.visibility = View.INVISIBLE
            binding.onConnectionerror.visibility = View.INVISIBLE
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun isNightMode() {
        sharedpref = SharedPrefNightMode(this)
        if (sharedpref.loadNightModeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showLoading(true)
                viewModel.setSearchQuery(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.move_set -> {
                val intent = Intent(this@UserActivity, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                true
            }
            R.id.favorite_menu -> {
                val intent = Intent(this@UserActivity, FavoriteActivity::class.java)
                startActivity(intent)
                overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                true
            }
            else -> return true
        }
    }

    private fun onDataError() {
        val builder = AlertDialog.Builder(this@UserActivity)
        builder.setTitle(resources.getString(R.string.alert_message_title))
        builder.setMessage(resources.getString(R.string.alert_message_body))
        builder.setNegativeButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

}