package com.udacity

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 101
    private val arrayPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var IdDownload: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val permissions = Permissions()
        permissions.validarPermissoes(arrayPermissions, this, REQUEST_CODE)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentId.customButton.setOnClickListener {
            clickButton()
        }
        createChannel(CHANNEL_ID, getString(R.string.channel_name))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for(permissao: Int in grantResults) {
            if(permissao == PackageManager.PERMISSION_DENIED) {
                alertPermissions()
            } else if (permissao == PackageManager.PERMISSION_GRANTED){}
        }
    }

    private fun alertPermissions() {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permissions Denied")
        builder.setMessage("This app required your permissions")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirm", DialogInterface.OnClickListener { dialog, which ->
            finish()
        })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun clickButton() {
        when (binding.contentId.radioGroup.checkedRadioButtonId) {
            View.NO_ID -> {
                Toast.makeText(
                    this,
                    getString(R.string.message),
                    Toast.LENGTH_LONG
                ).show()
            }
            R.id.glide_radio -> download(
                resources.getString(R.string.glide_url),
                resources.getString(R.string.radio_glide)
            )
            R.id.udacity_radio -> download(
                resources.getString(R.string.project_url),
                resources.getString(R.string.radio_project)
            )
            R.id.retrofit_radio -> download(
                resources.getString(R.string.retrofit_url),
                resources.getString(R.string.radio_retrofit)
            )
        }
        if (binding.contentId.radioGroup.checkedRadioButtonId != View.NO_ID) {
            binding.contentId.customButton.setNewButtonState(ButtonState.Loading)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            val downloadManager = getSystemService(DownloadManager::class.java) as DownloadManager
            val query = DownloadManager.Query().setFilterById(IdDownload)
            val cursor = downloadManager.query(query)
            var status = -1
            var title = ""
            if (cursor != null){
                if (cursor.moveToLast()){
                    val statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val titleIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                    status = cursor.getInt(statusIdx)
                    title = cursor.getString(titleIdx)
                }
            }

            notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.sendNotification("$title finished Downloading",title, status, applicationContext)
            val customButton = findViewById<LoadingButton>(R.id.custom_button)
            customButton.setNewButtonState(ButtonState.Completed)
        }
    }

    private fun download(url : String, title: String) {
        val dir = File(getExternalFilesDir(null), "/repos")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val request =
            DownloadManager.Request(Uri.parse("$url/archive/master.zip"))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/repos/repository.zip" )
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        IdDownload =
            downloadManager.enqueue(request)

    }

    private fun createChannel(channelId: String, channelName: String) {
        // declare a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply{
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download complete"
            val notificMan = getSystemService(
                NotificationManager::class.java
            ) as NotificationManager
            notificMan.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        const val CHANNEL_ID = "channelId"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_STATUS = "extra_status"
    }

}
