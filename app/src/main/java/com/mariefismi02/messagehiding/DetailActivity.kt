package com.mariefismi02.messagehiding

import android.database.sqlite.SQLiteConstraintException
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_detail.*
import android.net.Uri
import android.provider.MediaStore
import com.mariefismi02.messagehiding.db.LogData
import com.mariefismi02.messagehiding.db.database
import kotlinx.android.synthetic.main.layout_encrypt_detail.*
import com.mariefismi02.messagehiding.models.StegoData
import com.mariefismi02.messagehiding.utils.UriUtils
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.mariefismi02.messagehiding.utils.FileUtils
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import java.lang.NullPointerException


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail"

        if(intent.hasExtra("layout")) {
            val layout = intent.getIntExtra("layout", R.layout.layout_encrypt_detail)
            detail_content.layoutResource = layout
            detail_content.inflate()
        }


        if(intent.hasExtra("stegoData")) {

            val stegoData = intent.getParcelableExtra<StegoData>("stegoData")
            //Set Image Banner
            var imageURI: Uri? = Uri.parse(stegoData.imageURI)

            if (imageURI != null) {
                val fileuri = FileUtils.getFilePath(this, imageURI)

                val imageLoader = ImageLoader.getInstance()

                imageLoader.init(ImageLoaderConfiguration.createDefault(this))
                imageLoader.displayImage(imageURI.toString(), bannerIV)

                val filepath = UriUtils.UriFilePath(this, imageURI)
                val filename = UriUtils.UriFileName(this, imageURI)
                val fileformat = UriUtils.UriFileFormat(this, imageURI)

                fileDir.text = filepath
                fileName.text = filename
                fileFormat.text = fileformat?.toUpperCase()

            }

            val encryptTime: Long? = stegoData.executionTime
            val executedTime = stegoData.executedTime
            val sizeBefore = stegoData.sizeBefore.div(1024)
            val sizeAfter = stegoData.sizeAfter.div(1024)

            val sec = stegoData.executionTime.div(1000000000 )
            val millis = (stegoData.executionTime % 1000000000) / 1000000

            fileEstimation.text = "${sec},${millis} detik"


            fileTime.text = executedTime
            fileSize1.text = (sizeBefore).toString() + "kb"
            fileSize2?.text = (sizeAfter).toString() + "kb"

            fileMessage.text = "${stegoData.message.length} Karakter"

            val type = if(intent?.getIntExtra("layout", R.layout.layout_encrypt_detail)==R.layout.layout_encrypt_detail)
                "encrypt" else "decrypt"

            if(intent.hasExtra("log")){
                logBtn.isEnabled = false
            }

            logBtn.setOnClickListener {
                addToLogs(type, stegoData)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when(item?.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addToLogs(type: String, stegoData: StegoData){
        try {
            database.use {
                insert(LogData.TABLE_NAME,
                    LogData.TYPE to type,
                    LogData.URI to stegoData.imageURI,
                    LogData.EXECUTION_TIME to stegoData.executionTime,
                    LogData.EXECUTED_TIME to stegoData.executedTime,
                    LogData.MESSAGE to stegoData.message,
                    LogData.SIZE_BEFORE to stegoData.sizeBefore,
                    LogData.SIZE_AFTER to stegoData.sizeAfter
                )
            }
            toast("Ditambahkan ke Log")
            logBtn.isEnabled = false
        } catch (e: SQLiteConstraintException){
            toast(e.localizedMessage)
        }
    }

}
