package com.mariefismi02.messagehiding.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_encrypt.*
import org.jetbrains.anko.support.v4.startActivity
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback
import com.ayush.imagesteganographylibrary.Text.ImageSteganography
import com.ayush.imagesteganographylibrary.Text.TextEncoding
import org.jetbrains.anko.support.v4.toast
import android.app.AlertDialog
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import com.mariefismi02.messagehiding.DetailActivity
import com.mariefismi02.messagehiding.R
import com.mariefismi02.messagehiding.utils.FileUtils.getRealSizeFromUri
import com.mariefismi02.messagehiding.models.StegoData
import com.mariefismi02.messagehiding.utils.FileUtils
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import se.simbio.encryption.Encryption
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class EncryptFragment : Fragment(), TextEncodingCallback {

    private lateinit var bmp : Bitmap
    private val PICK_PHOTO_FOR_AVATAR : Int = 10
    private var savedUri : String? = null
    private var fileName : String? = ""

    private lateinit var stegoData : StegoData


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_encrypt, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        stegoData = StegoData()

        detailBtn.isEnabled = false

        detailBtn.setOnClickListener {
            if(stegoData.imageURI!=null)
                startActivity<DetailActivity>("stegoData" to stegoData, "layout" to R.layout.layout_encrypt_detail)
            else
                startActivity<DetailActivity>()
        }

        imageBtn.setOnClickListener {
            pickImage()
        }


        encryptBtn.setOnClickListener {
            ActivityCompat.requestPermissions(activity as Activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

            val layout = LinearLayout(it?.context)
            layout.orientation = LinearLayout.VERTICAL

            val dialog = AlertDialog.Builder(activity)

            val nameInput = EditText(activity)
            nameInput.inputType = InputType.TYPE_CLASS_TEXT
            nameInput.hint = "Nama File"

            val passInput = EditText(activity)
            passInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            passInput.hint = "Password"
            passInput.transformationMethod = PasswordTransformationMethod.getInstance()

            layout.addView(nameInput)
            layout.addView(passInput)

            dialog.setTitle("Simpan Gambar")
            dialog.setMessage("Hasil akan disimpan pada /Pictures/messagehiding/")
            dialog.setView(layout)

            dialog.setPositiveButton("Simpan"){ dialog, which ->
                if(nameInput.text.isNotEmpty() && passInput.text.isNotEmpty()) {
                    fileName = nameInput.text.toString()+ "-" + System.currentTimeMillis()
                    encryptImage(passInput.text.toString())
                }
                else
                    toast("nama dan password tidak boleh kosong")
            }
            dialog.setNegativeButton("Batal"){dialog, which ->
                dialog.dismiss()
            }
            dialog.show()
        }

    }

    private fun saveImage(bitmap: Bitmap, fileName: String?) : File {
        val fOut: OutputStream
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/messagehiding/"
        ) // the File to save ,
        dir.mkdirs()

        val file = File(dir, "${fileName}.png")


        toast("menyimpan")

        try {
            fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()

            toast("Tersimpan dengan nama ${fileName}")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file

    }

    private fun encryptImage(password: String) {
        val key = "mariefismi02"
        val salt = "msghd0619"
        val iv = ByteArray(16)
        val encryption = Encryption.getDefault(key, salt, iv)

        stegoData.message = msgET.text.toString()

        if(fileName == null || fileName=="")
            toast("Nama tidak boleh kosong")
        if(stegoData.message == null || stegoData.message=="")
            toast("Isi pesan terlebih dahulu")
        else if(stegoData.imageURI==null)
            toast("Pilih Gambar terlebih dahulu")
        else
        {


            val filePath = Uri.parse(stegoData.imageURI)
            if(filePath!=null)
                bmp = MediaStore.Images.Media.getBitmap(this.activity?.contentResolver, filePath)
            else
            {
                bmp = (stegoIV.drawable as BitmapDrawable).bitmap
            }

            //kalkulasi waktu enkripsi
            stegoData.executionTime = System.nanoTime()

            val encrypted = encryption.encryptOrNull(stegoData.message)

            var imgSteganography = ImageSteganography(encrypted, password, bmp)

            var textEncoding = TextEncoding(this.activity, this)

            textEncoding.execute(imgSteganography)

            encryptBtn.isEnabled = false
        }
    }

    fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return
            }

            stegoData.imageURI = data.data.toString()

            val imageLoader = ImageLoader.getInstance()

            imageLoader.init(ImageLoaderConfiguration.createDefault(context))

            imageLoader.displayImage(stegoData.imageURI, stegoIV)

            detailBtn.isEnabled = false

            encryptBtn.isEnabled = true

        }
    }

    override fun onStartTextEncoding() {

    }

    override fun onCompleteTextEncoding(result: ImageSteganography?) {
        if (result != null && result.isEncoded){

            //encrypted image bitmap is extracted from result object
            var encodedImage = result.encoded_image

            stegoData.sizeBefore = FileUtils.getRealSizeFromUri(context!!, Uri.parse(stegoData.imageURI)).toInt()
            //set text and image to the UI component.
            bmp = encodedImage

            val file = saveImage(bmp, fileName)
            savedUri = FileUtils.fileToContentUri(context!!, file).toString()

            stegoData.sizeAfter = getRealSizeFromUri(context!!, Uri.parse(savedUri)).toInt()

            Log.d("image",stegoData.imageURI!!)
            Log.d("save", savedUri!!)

            stegoData.executionTime = System.nanoTime() - stegoData.executionTime
            Log.d("timeExec", stegoData.executionTime.toString())

            val date = SimpleDateFormat("dd/MM/yyyy G 'at' HH:mm:ss")
            stegoData.executedTime = date.format(Date())

            //toast(encryptTime.toString())

            if(stegoData.imageURI!=null)
                startActivity<DetailActivity>( "stegoData" to stegoData, "layout" to R.layout.layout_encrypt_detail)
            else
                startActivity<DetailActivity>()

            detailBtn.isEnabled = true


        }
    }

}