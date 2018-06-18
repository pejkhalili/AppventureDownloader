package com.chapdast.appventuredownloader

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.util.*

val SERVER_ADDRESS = "https://www.appana.net/Download/index.php"
val PUSH_HANDLER = "https://cpanel9.ml/push/push.json"
val ANALYTIC_SERVER = "https:www.cpanel9.ml/push/ana.php"

val APP = "LightMusic"
val VERSION = "app"


val CHANNEL_ID = "com.chapdast.appventuredownloader.Notif"



val PUSH = "mk/Push"
val TAG = "mk/DLR"
var appPath=""
var TIME_IN_MILLS:Long = 2400000

fun SPref(context:Context,name:String): SharedPreferences? {
    var sh = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    fun edit(): SharedPreferences.Editor? {
        return sh.edit()
    }
    return  sh
}

fun sToast(c:Context,t:String,isLong:Boolean=true){

    var ToastLen = if(isLong) Toast.LENGTH_SHORT else Toast.LENGTH_LONG;
    var assetManager = c.assets
    var iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))

    val toast= Toast(c)
    val inflater = c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = inflater.inflate(R.layout.toast,null)
    val txt = layout.findViewById<TextView>(R.id.toast_txt)
    txt.typeface = iransans
    txt.text=t

    toast.setGravity(Gravity.TOP, Gravity.CENTER,110)
    toast.view = layout
//    toast.setMargin(10f,150f)
    toast.duration = ToastLen
    toast.show()
}
fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) {
        return false
    }
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}
fun TimeLim(context: Context,tl:Long):Boolean{
    var currentTime = System.currentTimeMillis() / 1000
    var tl = SPref(context, "timelimit")!!.getLong("time", 0)
    return currentTime>tl
}
fun SetTimeLim(context: Context,tl:Long){
    SPref(context, "timelimit")!!.edit().putLong("time", tl).commit()
}
fun isAppInstalled(context: Context,pack:String):Boolean{
    try{
        context.packageManager.getPackageInfo(pack,0)
        return true
    }catch (e: PackageManager.NameNotFoundException){
    }
    return false
}
fun OpenInstaller(context: Context,loc:String,tl:Long){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        var file = File(loc)
        var uri = FileProvider.getUriForFile(context,context.packageName + ".provider",file)
        var intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri,"application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }else{
        var op = Intent(Intent.ACTION_VIEW)
        op.setDataAndType(Uri.fromFile(File(loc)), "application/vnd.android.package-archive")
        op.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(op)
    }
    SetTimeLim(context,tl)

}