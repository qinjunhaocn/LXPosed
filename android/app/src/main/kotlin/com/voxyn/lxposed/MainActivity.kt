package com.voxyn.lxposed

import android.os.Build
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.DataOutputStream

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.voxyn.lxposed/root"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "checkRootAndExecute") {
                checkRootAndExecute(result)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun checkRootAndExecute(result: MethodChannel.Result) {
        if (isRootAvailable()) {
            executeCommand()
            result.success(true)
        } else {
            result.success(false)
        }
    }

    private fun isRootAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun executeCommand() {
        try {
            val apiLevel = Build.VERSION.SDK_INT
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            
            if (apiLevel >= 29) {
                os.writeBytes("am broadcast -a android.telephony.action.SECRET_CODE -d android_secret_code://5776733 android\n")
            } else {
                os.writeBytes("am broadcast -a android.provider.Telephony.SECRET_CODE -d android_secret_code://5776733 android\n")
            }
            
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
