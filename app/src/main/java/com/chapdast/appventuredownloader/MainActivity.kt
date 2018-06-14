package com.chapdast.appventuredownloader

import android.Manifest
import android.app.DownloadManager

import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

import java.io.File
import android.R.attr.path
import android.app.Activity
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageInstaller
import android.content.pm.ResolveInfo
import android.graphics.Typeface
import android.nfc.Tag
import android.support.v4.content.FileProvider
import android.os.Build
import android.os.StrictMode
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.squareup.picasso.Picasso
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*
import co.ronash.pushe.Pushe;


class MainActivity : AppCompatActivity() {
    private val WRITE_RQ = 101


    inner class Downloader:AsyncTask<String,String,Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
            pg_dl.visibility = View.VISIBLE
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            var msg = ""
            if(values[0]!!.toInt() >99){
                pg_dl.visibility = View.GONE
                btn_dl.text = applicationContext.resources.getString(R.string.download_btn)
            }
            pg_dl.progress = values[0]!!.toInt()
            btn_dl.text = values[0]!! + "%"

        }
        override fun doInBackground(vararg p0: String?): Boolean {
            var flag = false

            try {
                var file = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "dl", "app" to APP, "ver" to VERSION))
                Log.d(TAG, file.jsonObject.toString())
                if (file.statusCode == 200) {
                    var result = file.jsonObject
                    if (result.getBoolean("result")) {
                        var uri = URL(result.getString("uri"))
                        var c = uri.openConnection()
                        c.doOutput = true
                        c.connect()
                        var PATH: String = "" + Environment.getExternalStorageDirectory() + "/Download/Appventure-Downloader/"
                        var file = File(PATH)
                        Log.d(TAG,File(file,result.getString("name")).toString())
                        if(File(file,result.getString("name")).exists()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (!applicationContext.packageManager.canRequestPackageInstalls()) {
                                    startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
                                } else {
                                    appPath = PATH + result.getString("name")
                                    OpenDLF(PATH + result.getString("name"), 1234)
                                    flag = true
                                }
                            } else {
                                appPath = PATH + result.getString("name")
                                OpenDLF(PATH + result.getString("name"), 1234)
                                flag = true
                            }
                        }
                        else {
                            file.mkdirs()
                            var outFile = File(file, result.getString("name"))
                            if (outFile.exists()) {
                                outFile.delete()
                            }
//                            sToast(applicationContext, applicationContext.resources.getString(R.string.startdl))

                            var fos = FileOutputStream(outFile)
                            var instream = c.getInputStream()
                            var total_size = c.contentLength
                            pg_dl.max = 100
                            val buffer = ByteArray(1024)
                            var len1 = 0
                            var per = 0
                            var downloaded = 0
                            len1 = instream.read(buffer)
                            while (len1 != -1) {
                                fos.write(buffer, 0, len1)
                                downloaded += len1
                                per = downloaded * 100 / total_size
                                publishProgress(per.toString())
                                len1 = instream.read(buffer)
                            }
                            fos.close()
                            instream.close()
                            Thread.sleep(50)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (!applicationContext.packageManager.canRequestPackageInstalls()) {
                                    startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
                                } else {
                                    appPath = PATH + result.getString("name")
                                    OpenDLF(PATH + result.getString("name"), 1234)
                                    flag = true
                                }
                            } else {
                                appPath = PATH + result.getString("name")
                                OpenDLF(PATH + result.getString("name"), 1234)
                                flag = true
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e(TAG, "ERR" + e.message)
                flag = false
            }

            return flag
        }


        override fun onPostExecute(result: Boolean?) {

            Log.d(TAG,"RES" + result)
            btn_dl.isEnabled = true
            super.onPostExecute(result)
            pg_dl.visibility = View.GONE
            if(result!!){
//                sToast(applicationContext,"DONE")
            }else{
                sToast(applicationContext,applicationContext.resources.getString(R.string.tryAgain))
            }
            var appCheck = Intent(applicationContext,AppCheck::class.java)
            appCheck.action = applicationContext.packageName + ".AppCheck"
            startService(appCheck)
            finishAffinity()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (applicationContext.packageManager.canRequestPackageInstalls()) {
                var op = Intent(Intent.ACTION_VIEW)
                op.setDataAndType(Uri.fromFile(File(appPath)), "application/vnd.android.package-archive")
                op.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(op)
                Log.d(TAG,"Rsdalsfskfa")
            }
        } else {
            //give the error
            Log.d(TAG,"HEEEEEEEE")
        }
    }
    fun OpenDLF(loc:String,requestCode: Int){
        Log.d(TAG,"TTT" + appPath)
//        var onComp = object : BroadcastReceiver() {
//            override fun onReceive(p0: Context?, p1: Intent?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    var file = File(loc)
                    var uri = FileProvider.getUriForFile(applicationContext,applicationContext.packageName + ".provider",file)
                    Log.d(TAG,"URI:>>" + uri)
                    var intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri,"application/vnd.android.package-archive")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivityForResult(intent,requestCode)
                }else{
                    var op = Intent(Intent.ACTION_VIEW)
                    op.setDataAndType(Uri.fromFile(File("file://"+loc)), "application/vnd.android.package-archive")
                    op.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivityForResult(op,requestCode)
                }

            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(isNetworkAvailable(applicationContext)) {
            Pushe.initialize(this,true);
            try {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                var back = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "bg", "app" to APP))
                if (back.statusCode == 200) {
                    var res = back.jsonObject
                    if (res.getBoolean("result")) {
                        var bg = res.getString("bg")
                        Picasso.with(applicationContext).load(bg).placeholder(R.mipmap.icon).into(back_dl)
                    }
                }
                pg_dl.visibility = View.GONE
                makeRequest()
                setupPermissions()
                var assetManager = applicationContext.assets
                var iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
                btn_dl.typeface = iransans


                btn_dl.isEnabled = false
                var dlProccess = Downloader().execute()
            }catch (e:SocketTimeoutException){
                Log.e(TAG,applicationContext.resources.getString(R.string.noNet))
            }
        }else{
            sToast(applicationContext,applicationContext.resources.getString(R.string.noNet))
        }



    }

    private fun setupPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false

        }
        return true
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.REQUEST_INSTALL_PACKAGES),
                WRITE_RQ)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            WRITE_RQ -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
