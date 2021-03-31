package com.thorin.dicoding.view.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.thorin.dicoding.R
import com.thorin.dicoding.databinding.ActivityUserDetailBinding
import com.thorin.dicoding.viewmodel.DetailUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_URL = "extra_url"
    }

    private lateinit var viewModel: DetailUserViewModel
    private lateinit var binding: ActivityUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userNameExtra = intent.getStringExtra(EXTRA_USERNAME)
        val id = intent.getIntExtra(EXTRA_ID, 0)
        val avatarUrl = intent.getStringExtra(EXTRA_URL)

        val bundle = Bundle()
        bundle.putString(EXTRA_USERNAME, userNameExtra)

        viewModel = ViewModelProvider(
            this
        ).get(DetailUserViewModel::class.java)

        if (null != userNameExtra) {
            showLoading(true)
            viewModel.setUserDetail(userNameExtra)
        }
        val textLink = "https://github.com/"

        val tabs = findViewById<TabLayout>(R.id.tabs)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)

        viewModel.status.observe(this, fun(status: Boolean?) {
            status?.let {
                when (status) {
                    false -> {
                        alertError()
                        binding.progressDetail.visibility = View.GONE
                        binding.layoutdetail.visibility = View.INVISIBLE
                        binding.ondataerror.visibility = View.VISIBLE
                        binding.tabs.visibility = View.INVISIBLE
                        binding.viewPager.visibility = View.INVISIBLE
                    }
                }
            }
        })

        viewModel.getUserDetail().observe(this, {

            if (null != it) {
                binding.apply {
                    name.text = StringBuilder(textLink).append("${it.login}")
                    username.text = it.name
                    follower.text =
                        StringBuilder(resources.getString(R.string.Follower)).append(" ${it.followers}")
                    following.text =
                        StringBuilder(resources.getString(R.string.Following)).append(" ${it.following}")
                    company.text =
                        StringBuilder(resources.getString(R.string.Company)).append(" ${it.company}")
                    location.text =
                        StringBuilder(resources.getString(R.string.Location)).append(" ${it.location}")
                    repository.text =
                        StringBuilder(resources.getString(R.string.Repository)).append(" ${it.public_repos}")
                    profil.text =
                        StringBuilder(resources.getString(R.string.Profile)).append(" ${it.name}")

                    Glide.with(this@UserDetailActivity)
                        .load(it.avatar_url)
                        .placeholder(R.drawable.ic_default)
                        .error(R.drawable.ic_connect_error)
                        .into(avatar)

                    showLoading(false)
                }
            }
        })

        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager, bundle)
        binding.apply {
            viewPager.adapter = sectionPagerAdapter
            tabs.setupWithViewPager(viewPager)
            tabs.isHorizontalScrollBarEnabled = true
        }

        var isFavorite = false
        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.checkUser(id)
            withContext(Dispatchers.Main) {
                when {
                    null != count -> if (count > 0) {
                        binding.toggleFavorite.isChecked = true
                        isFavorite = true
                    } else {
                        binding.toggleFavorite.isChecked = false
                        isFavorite = false
                    }
                }
            }
        }

        binding.toggleFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                userNameExtra?.let { it1 -> viewModel.addToFav(it1, id, avatarUrl!!) }
            } else {
                viewModel.userRemoveFromFav(id)
            }
            binding.toggleFavorite.isChecked = isFavorite
        }

        binding.showprofile.setOnClickListener {
            val url = binding.name.text.toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.fabShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody =
                "${resources.getString(R.string.share_body1)} ${
                    binding.name.text
                }, ${resources.getString(R.string.share_body2)} ${binding.username.text}, ${
                    resources.getString(
                        R.string.share_body3
                    )
                }"
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, findViewById<TextView>(R.id.username).text)
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, resources.getString(R.string.share_title)))
        }
    }

    private fun alertError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert_message_title))
        builder.setMessage(resources.getString(R.string.alert_message_body))
        builder.setNegativeButton("OK") { dialog, _ -> // Do nothing
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressDetail.visibility = View.VISIBLE
        } else {
            binding.progressDetail.visibility = View.GONE
        }
    }

}