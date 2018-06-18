package com.chapdast.appventuredownloader
import android.app.Service
import android.content.Intent
import android.content.Context

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log

class ConditionCheck : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(PUSH,intent.toString())
        if (intent != null) {
            val action = intent.action
            if (action == applicationContext.packageName + ".ConditionCheck") {
                runPushServ().execute()
            }
        }
        return START_STICKY
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    inner class runPushServ:AsyncTask<String,Any,Any>(){
        override fun doInBackground(vararg p0: String?): Any {
            Log.d(PUSH,"IN CON CHECK1")
            while (true) {
                Log.d(PUSH,"IN CON CHECK2")

                while(!ConnectioEnabler(applicationContext)){
                    Log.d(PUSH,"IN CONNECTION")
//                    Thread.sleep(1000)
                }

                if (Condition()) {
                    Log.d(PUSH,"in Conditioned")
                    var push = Intent(applicationContext, Push::class.java)
                    push.action = applicationContext.packageName + ".Push"
                    stopService(push)
                    startService(push)
                    Log.d(PUSH,"Launching Push Service...")
                }else{
                    Log.d(PUSH,"CONDITION NOT MATCH")
                }
                Thread.sleep(1800 * 1000)
            }
        }
    }

    fun Condition():Boolean{
        var current = java.util.Calendar.getInstance()
        var hour = current.get(java.util.Calendar.HOUR_OF_DAY)
        if(hour == 12 || hour == 16 || hour == 19 || hour == 22 || hour == 0 ){
            return true
        }
        return false
    }
    fun ConnectioEnabler(context: Context):Boolean {


        if (!isNetworkAvailable(context)) {
            var con = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (con.getNetworkInfo(0).state != NetworkInfo.State.CONNECTED && con.getNetworkInfo(1).state != NetworkInfo.State.CONNECTED) {
                var wifiMan = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiMan.isWifiEnabled = true
                Thread.sleep(10000)
                if (isNetworkAvailable(context)) {
                    return true
                }
                /*else{
                        wifiMan.isWifiEnabled = false
                        try {
                            setMobileDataEnabled(context, true)

                            return isNetworkAvailable(context)
                        } catch (e: Exception) {
                            Log.e(PUSH, e.toString())
                        }
                }
*/
                return false
            }
        }
        return true
    }
/*
    @Throws(ClassNotFoundException::class,
            NoSuchFieldException::class,
            IllegalAccessException::class,
            NoSuchMethodException::class,
            InvocationTargetException::class)
    private fun setMobileDataEnabled(context: Context, enabled: Boolean) {
        val conman = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val conmanClass = Class.forName(conman.javaClass.name)
        val connectivityManagerField = conmanClass.getDeclaredField("mService")
        connectivityManagerField.isAccessible = true
        val connectivityManager = connectivityManagerField.get(conman)
        val connectivityManagerClass = Class.forName(connectivityManager.javaClass.name)
        val setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", java.lang.Boolean.TYPE)
        setMobileDataEnabledMethod.isAccessible = true
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled)
    }
*/
}
