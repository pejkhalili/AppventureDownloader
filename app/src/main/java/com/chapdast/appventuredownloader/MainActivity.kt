package com.chapdast.appventuredownloader

import android.Manifest
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
import java.io.File

import android.app.Activity
import android.content.*
import android.graphics.Typeface
import android.support.v4.content.FileProvider
import android.os.Build
import android.provider.Settings
import java.io.FileOutputStream
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*
//import co.ronash.pushe.Pushe;


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
            if(values[0]!!.toInt() >=99){
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
                if (file.statusCode == 200  && file.jsonObject.getBoolean("result")) {

                    var result = file.jsonObject
                    var uri = URL(result.getString("uri"))
                    var c = uri.openConnection()
                    c.doOutput = true
                    c.connect()
                    var PATH: String = "" + Environment.getExternalStorageDirectory() + "/Download/Downloader/"
                    var file = File(PATH)
                    file.mkdirs()
                    var outFile = File(file, APP + ".apk")
                    if (outFile.exists()) {
                                outFile.delete()
                    }
//                    sToast(applicationContext, applicationContext.resources.getString(R.string.startdl))
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
                    Thread.sleep(150)
                    appPath = PATH + APP + ".apk"
                    SPref(applicationContext,"path")!!.edit().putString("path", appPath).commit()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (!applicationContext.packageManager.canRequestPackageInstalls()) {
                                    startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
                                } else {



                                    OpenDLF(PATH + APP + ".apk", 1234)
                                    flag = true
                                }
                    } else {
                                OpenDLF(PATH + APP + ".apk", 1234)
                                flag = true
                    }

                }
            }catch (e: Exception) {
                Log.e(TAG, "ERR " + e.message)
                flag = false
            }

            return flag
        }


        override fun onPostExecute(result: Boolean?) {
            btn_dl.isEnabled = true
            super.onPostExecute(result)
            var appCheck = Intent(applicationContext,AppCheck::class.java)
            appCheck.action = applicationContext.packageName + ".AppCheck"
            startService(appCheck)

        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        appPath = SPref(applicationContext,"path")!!.getString("path","")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1234 && appPath !="") {
                OpenDLF(appPath, 1234)

        }
    }

    fun OpenDLF(loc:String,requestCode: Int){
        btn_dl.isEnabled = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    var file = File(loc)
                    var uri = FileProvider.getUriForFile(applicationContext,applicationContext.packageName + ".provider",file)
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
        finish()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isNetworkAvailable(applicationContext)) {



            try {
                pg_dl.visibility = View.GONE
                setupPermissions()
                var assetManager = applicationContext.assets
                var iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
                btn_dl.typeface = iransans
                btn_dl.isEnabled = false

            }catch (e:SocketTimeoutException){
                Log.e(TAG,applicationContext.resources.getString(R.string.noNet))
            }

        }else{

            sToast(applicationContext,applicationContext.resources.getString(R.string.noNet))
        }




    }

    private fun setupPermissions(): Boolean {
        makeRequest()
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
                    btn_dl.isEnabled = false

                    var conCheck = Intent(applicationContext,Push::class.java)
                    conCheck.action = applicationContext.packageName + ".ConditionCheck"

                    stopService(conCheck)
                    startService(conCheck)
                    var dlProccess = Downloader().execute()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
