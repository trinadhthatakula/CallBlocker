package com.rams.ted.callblocker.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.ADJUST_MUTE
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import java.lang.reflect.Method

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val number = intent?.extras?.getString("incoming_number")

        val blockList = context?.getSharedPreferences("prefs", MODE_PRIVATE)
            ?.getString("blockList", null)?.toBlockList()

        if (intent?.action?.equals("android.intent.action.NEW_OUTGOING_CALL") == true) {
            Log.d("PhoneCallReceiver", "onCallStateChanged: outgoing to $number")
        }

        val telephony: TelephonyManager =
            context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val customPhoneListener = PhoneCallStateListener(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephony.registerTelephonyCallback(
                context.mainExecutor,
                object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                    override fun onCallStateChanged(state: Int) {
                        if (!number.isNullOrBlank() && number.isNotEmpty())
                            blockCall(telephony, audioManager)
                        when (state) {
                            TelephonyManager.CALL_STATE_IDLE -> {
                                Log.d("PhoneCallReceiver", "onCallStateChanged: call state idle $number")
                                blockList?.let {
                                    if (it.numbers.contains(number)) {
                                        blockCall(telephony, audioManager)
                                    }
                                }
                            }

                            TelephonyManager.CALL_STATE_OFFHOOK -> {
                                Log.d("PhoneCallReceiver", "onCallStateChanged: call state offHook $number")
                                blockList?.let {
                                    if (it.numbers.contains(number)) {
                                        blockCall(telephony, audioManager)
                                    }
                                }
                            }

                            TelephonyManager.CALL_STATE_RINGING -> {
                                Log.d("PhoneCallReceiver", "onCallStateChanged: call state ringing $number")
                                blockList?.let {
                                    if (it.numbers.contains(number)) {
                                        blockCall(telephony, audioManager)
                                    }
                                }
                            }
                        }
                    }
                })
        } else
            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun blockCall(telephonyManager: TelephonyManager, audioManager: AudioManager) {
        try {
            Log.d("PhoneReceiver", "blockCall: started")
            val clazz = Class.forName(telephonyManager.javaClass.name)
            val method: Method = clazz.getDeclaredMethod("getITelephony")
            method.isAccessible = true
            val telephonyService: ITelephony = method.invoke(telephonyManager) as ITelephony
            telephonyService.silenceRinger()
            telephonyService.endCall()

            if (Build.VERSION.SDK_INT >= 23)
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, ADJUST_MUTE, 0)
            else
                audioManager.setStreamMute(AudioManager.STREAM_RING, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class PhoneCallStateListener(private val context: Context) : PhoneStateListener() {

    @Deprecated("Deprecated in Java")
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        val telephony: TelephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        super.onCallStateChanged(state, phoneNumber)
        val blockList = context.getSharedPreferences("prefs", MODE_PRIVATE)
            .getString("blockList", null)?.toBlockList()

        when (state) {
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.d("PhoneCallReceiver", "onCallStateChanged: call state idle $phoneNumber")
                blockList?.let {
                    if (it.numbers.contains(phoneNumber)) {
                        blockCall(telephony, audioManager)
                    }
                }
            }

            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.d("PhoneCallReceiver", "onCallStateChanged: call state offHook $phoneNumber")
                blockList?.let {
                    if (it.numbers.contains(phoneNumber)) {
                        blockCall(telephony, audioManager)
                    }
                }
            }

            TelephonyManager.CALL_STATE_RINGING -> {
                Log.d("PhoneCallReceiver", "onCallStateChanged: call state ringing $phoneNumber")
                blockList?.let {
                    if (it.numbers.contains(phoneNumber)) {
                        blockCall(telephony, audioManager)
                    }
                }
            }
        }
    }

    private fun blockCall(telephonyManager: TelephonyManager, audioManager: AudioManager) {
        try {
            Log.d("PhoneReceiver", "blockCall: started")
            val clazz = Class.forName(telephonyManager.javaClass.name)
            val method: Method = clazz.getDeclaredMethod("getITelephony")
            method.isAccessible = true
            val telephonyService: ITelephony = method.invoke(telephonyManager) as ITelephony
            telephonyService.silenceRinger()
            telephonyService.endCall()

            if (Build.VERSION.SDK_INT >= 23)
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, ADJUST_MUTE, 0)
            else
                audioManager.setStreamMute(AudioManager.STREAM_RING, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}