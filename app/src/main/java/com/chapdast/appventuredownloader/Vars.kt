package com.chapdast.appventuredownloader

import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.*

/**
 * Created by pejman on 6/12/18.
 */
val SERVER_ADDRESS = "https://www.chap-dast.com/dl/index.php"
val APP = "hellogram"
val VERSION = "1"
val TAG = "DLR"
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