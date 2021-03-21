package com.udacity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra(MainActivity.EXTRA_TITLE)
        val status = intent.getIntExtra(MainActivity.EXTRA_STATUS, -1)
        val detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)
        setSupportActionBar(detailBinding.toolbar)
        detailBinding.detailContentId.title.text =
            resources.getString(R.string.detail_title_text, title)
        detailBinding.detailContentId.status.text = when (status) {
            8 -> "Status: Successful"
            2 -> "Status: Running"
            1 -> "Status: Pending"
            4 -> "Status: Paused"
            16 -> "Status: Failed"
            -1 -> "status not found"
            else -> "Status: Unknown"
        }
    }

}
