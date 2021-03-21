package com.udacity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notification: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notification.cancelAll()

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

        detailBinding.buttonGoBack.setOnClickListener {
            val intentGoBack = Intent(this, MainActivity::class.java)
            startActivity(intentGoBack)
        }

    }

}
