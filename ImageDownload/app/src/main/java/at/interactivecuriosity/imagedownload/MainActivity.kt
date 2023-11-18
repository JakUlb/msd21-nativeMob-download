package at.interactivecuriosity.imagedownload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.interactivecuriosity.imagedownload.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg" // URL des herunterzuladenden Bildes
    private val fileName = "downloadedImage.jpg"

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_DOWNLOAD_STARTED)
            addAction(ACTION_DOWNLOAD_SUCCESS)
            addAction(ACTION_DOWNLOAD_ERROR)
        }
        registerReceiver(downloadReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(downloadReceiver)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)

        downloadButton.setOnClickListener {
            downloadImage(imageUrl, fileName)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }
    }
    companion object {
        const val ACTION_DOWNLOAD_STARTED = ".DOWNLOAD_STARTED"
        const val ACTION_DOWNLOAD_SUCCESS = ".DOWNLOAD_SUCCESS"
        const val ACTION_DOWNLOAD_ERROR = ".DOWNLOAD_ERROR"
    }

    private fun downloadImage(urlString: String, fileName: String) {
        Intent (this, DownloadService::class.java).also {
            it.putExtra("urlString", urlString)
            it.putExtra("fileName", fileName)
            startService(it)
        }
    }

    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            runOnUiThread {
                imageView.setImageBitmap(null)
                Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_DOWNLOAD_STARTED -> {
                    showToast("Download started")
                }
                ACTION_DOWNLOAD_SUCCESS -> {
                    showToast("Download successful")
                    File (getExternalFilesDir(null), fileName).also {
                        val bitmap = BitmapFactory.decodeFile(it.absolutePath)
                        runOnUiThread {
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                }
                ACTION_DOWNLOAD_ERROR -> showToast("Download error")
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
