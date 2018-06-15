package com.chapdast.appventuredownloader

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.*

val SERVER_ADDRESS = "https://www.appana.net/Download/index.php"
val APP = "LightMusic"
val VERSION = "LightMusic13"



val TAG = "DLR"
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