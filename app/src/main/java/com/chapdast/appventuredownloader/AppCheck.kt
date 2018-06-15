package com.chapdast.appventuredownloader

import android.app.IntentService
import android.content.ComponentName
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.File

class AppCheck : IntentService("AppCheck") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if(action == applicationContext.packageName + ".AppCheck"){
                var appPackName = khttp.post(SERVER_ADDRESS,data = mapOf("m" to "packageName", "app" to APP))
                if(appPackName.statusCode == 200 && appPackName.jsonObject.getBoolean("result")){
                    Thread.sleep(TIME_IN_MILLS)
                    isAppInstalled(appPackName.jsonObject.getString("packageName"))
                }
            }
            Log.d(TAG,action.toString())
        }
    }

    fun isAppInstalled(pack:String){
        try{
            applicationContext.packageManager.getPackageInfo(pack,0)
            var pm = applicationContext.packageManager
            var app = ComponentName(applicationContext,com.chapdast.appventuredownloader.Splash::class.java)
            pm.setComponentEnabledSetting(app,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)


            }catch (e: PackageManager.NameNotFoundException){

            var dler = Intent(applicationContext,MainActivity::class.java)
            startActivity(dler)
        }
    }



}
