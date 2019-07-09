package com.mariefismi02.messagehiding.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_decrypt.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback
import com.ayush.imagesteganographylibrary.Text.ImageSteganography
import com.ayush.imagesteganographylibrary.Text.TextDecoding
import com.mariefismi02.messagehiding.DetailActivity
import com.mariefismi02.messagehiding.R
import com.mariefismi02.messagehiding.models.StegoData
import com.mariefismi02.messagehiding.utils.FileUtils
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.android.synthetic.main.fragment_decrypt.detailBtn
import kotlinx.android.synthetic.main.fragment_decrypt.imageBtn
import kotlinx.android.synthetic.main.fragment_decrypt.msgET
import kotlinx.android.synthetic.main.fragment_decrypt.stegoIV
import kotlinx.android.synthetic.main.fragment_encrypt.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import se.simbio.encryption.Encryption
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DecryptFragment : Fragment(), TextDecodingCallback{


    private val PICK_PHOTO_FOR_AVATAR : Int = 10

    private lateinit var stegoData : StegoData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_decrypt, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        stegoData = StegoData()

        detailBtn.isEnabled = false

        detailBtn?.setOnClickListener {
            if(stegoData.imageURI!=null)
                startActivity<DetailActivity>("stegoData" to stegoData, "layout" to R.layout.layout_decrypt_detail)
            else
                startActivity<DetailActivity>()
        }

        decryptBtn?.setOnClickListener {

            val dialog = AlertDialog.Builder(activity)

            val passInput = EditText(activity)
            passInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            passInput.transformationMethod = PasswordTransformationMethod.getInstance()

            dialog.setTitle("Password")
            dialog.setView(passInput)

            dialog.setPositiveButton("Pulihkan"){dialog, which ->
                if(passInput.text.isNotEmpty()){
                    decryptImage(passInput.text.toString())
                }
                else
                {
                    toast("password tidak boleh kosong!")
                }
            }

            dialog.setNegativeButton("Batal"){dialog, which ->
                dialog.dismiss()
            }
            dialog.show()

        }

        imageBtn.setOnClickListener {
            pickImage()
        }
    }

    private fun decryptImage(password: String) {
        val filePath = if(stegoData.imageURI!=null) Uri.parse(stegoData.imageURI) else null

        if(stegoData.imageURI!=null){

            //kalkulasi waktu enkripsi
            stegoData.executionTime = System.nanoTime()
            Log.d("timeExec", stegoData.executionTime.toString())

            var bmp = MediaStore.Images.Media.getBitmap(this.activity?.contentResolver, filePath)
            var imgSteganography = ImageSteganography(password, bmp)

            var textDecoding = TextDecoding(activity, this)

            textDecoding.execute(imgSteganography)


        }
        else
        {
            toast("Pilih Gambar Terlebih Dahulu!")
        }

    }

    fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        val dir = Uri.parse(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator + "messagehiding" + File.separator
        ) // the File to save ,

        intent.setDataAndType( dir, "image/*")
        startActivityForResult(Intent.createChooser(intent, "Choose Image"), PICK_PHOTO_FOR_AVATAR)
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

            decryptBtn.isEnabled = true
            detailBtn.isEnabled = false
        }
    }

    override fun onStartTextEncoding() {

    }

    override fun onCompleteTextEncoding(result: ImageSteganography?) {

        if (result != null) {
            if ((!result.isDecoded))
                toast("Pesan tidak ditemukan!")
            else {
                if ((!result.isSecretKeyWrong)) {

                    val msg = result.message

                    if(msg==null)
                        toast("Gagal dipulihkan")
                    else {
                        toast("Pesan dipulihkan!")

                        stegoData.sizeBefore =
                            FileUtils.getRealSizeFromUri(context!!, Uri.parse(stegoData.imageURI)).toInt()

                        stegoData.message = msg

                        val date = SimpleDateFormat("dd/MM/yyyy G 'at' HH:mm:ss")
                        stegoData.executedTime = date.format(Date())

                        //kalkulasi waktu dekrip
                        stegoData.executionTime = System.nanoTime() - stegoData.executionTime
                        Log.d("timeExec", stegoData.executionTime.toString())

                        detailBtn.isEnabled = true

                        msgET.setText(msg)
                    }
                } else {
                    toast("Password salah!")
                }
            }
        } else {
            toast("Pilih Gambar Terlebih Dahulu!")
        }


        decryptBtn.isEnabled = false
    }

}