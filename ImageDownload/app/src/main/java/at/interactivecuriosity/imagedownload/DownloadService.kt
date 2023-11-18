package at.interactivecuriosity.imagedownload

import android.app.IntentService
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import at.interactivecuriosity.imagedownload.MainActivity.Companion.ACTION_DOWNLOAD_ERROR
import at.interactivecuriosity.imagedownload.MainActivity.Companion.ACTION_DOWNLOAD_STARTED
import at.interactivecuriosity.imagedownload.MainActivity.Companion.ACTION_DOWNLOAD_SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadService : IntentService("DownloadService"){
    override fun onHandleIntent(p0: Intent?) {
        Log.v("DownloadService", "onHandleIntent")
        val urlString = p0?.getStringExtra("urlString");
        val fileName = p0?.getStringExtra("fileName");
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sendBroadcast(Intent(ACTION_DOWNLOAD_STARTED))
                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val file = File(getExternalFilesDir(null), fileName)
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                    sendBroadcast(Intent(ACTION_DOWNLOAD_SUCCESS))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                sendBroadcast(Intent(ACTION_DOWNLOAD_ERROR))
            }
        }
    }

}
