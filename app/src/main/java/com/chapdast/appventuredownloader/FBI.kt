package com.chapdast.appventuredownloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_fbi.*
import java.io.InputStream
import java.util.*

class FBI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fbi)
        if(intent!=null){
            var assetManager = applicationContext.assets
            var iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
            val fbi = intent.extras
            val title = fbi.getString("title")
            val body = fbi.getString("body")
            val img = fbi.getString("img")
            Log.d(PUSH,"IMG" + img)
            val timelimit = fbi.getString("timelimit").toLong()
            val link = fbi.getString("link")
            fbi_title.text = title
            fbi_title.typeface = iransans
            fbi_body.typeface = iransans
            fbi_install.typeface = iransans
            fbi_body.text = body
//            GetImg().execute(img)
            fbi_img.setImageURI(Uri.parse(img))
            fbi_install.setOnClickListener {
                OpenInstaller(applicationContext,link,(System.currentTimeMillis()/1000)+timelimit)
            }

        }
    }

    inner class GetImg:AsyncTask<String,Any, Bitmap>(){
        override fun doInBackground(vararg p0: String?):Bitmap {
            var imgUrl = p0[0]
            var img:Bitmap?=null
            try{
                var input:InputStream = java.net.URL(imgUrl).openStream()
                img = BitmapFactory.decodeStream(input)

            }catch (e:Exception){
                Log.d(PUSH,e.message)
            }
        return img!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            fbi_img.setImageBitmap(result)
        }
    }

}
