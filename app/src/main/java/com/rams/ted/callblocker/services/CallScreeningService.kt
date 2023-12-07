package com.rams.ted.callblocker.services

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.rams.ted.callblocker.model.toBlockList

@RequiresApi(api = Build.VERSION_CODES.N)
class CallScreeningService : CallScreeningService() {

    private var shouldSilentCall: Boolean = false
    private var shouldRejectCall: Boolean = false

    override fun onScreenCall(callDetails: Call.Details) {

        val sp = getSharedPreferences("prefs", MODE_PRIVATE)
        val blockList = sp?.getString("blockList", null)?.toBlockList()

        shouldRejectCall = sp.getBoolean("blockAllCalls", shouldRejectCall)

        var number = callDetails.handle.schemeSpecificPart

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            when (callDetails.callerNumberVerificationStatus) {

                Connection.VERIFICATION_STATUS_FAILED -> {
                    shouldSilentCall = sp.getBoolean("muteBotCalls", shouldSilentCall)
                    Toast.makeText(
                        this,
                        "Bot call detected, call might be spam",
                        Toast.LENGTH_LONG
                    ).show()
                }

                Connection.VERIFICATION_STATUS_NOT_VERIFIED -> {
                }

                Connection.VERIFICATION_STATUS_PASSED -> {

                }
            }
            when (callDetails.callDirection) {

                Call.Details.DIRECTION_INCOMING -> {
                    Log.d("Call Screening", "onScreenCall: incoming call detected from $number")
                }

                Call.Details.DIRECTION_OUTGOING -> {
                    Log.d("Call Screening", "onScreenCall: outgoing call detected from $number")
                }

                Call.Details.DIRECTION_UNKNOWN -> {
                    Log.d("Call Screening", "onScreenCall: unknown call from $number")
                }
            }

        }

        if (sp.getBoolean("blockCalls", true))
            blockList?.let {
                if (it.numbers.contains(number.toString())) {
                    shouldRejectCall = true
                } else if(number.length>10){
                    number = number.toString().substring(number.length - 10)
                    Log.d("Call Screening", "onScreenCall: number trimmed = $number")
                    if (number.length > 10 && it.numbers.contains(number)) {
                        shouldRejectCall = true
                    }
                }
            }

        val response = CallResponse.Builder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (shouldRejectCall) {
                response.setRejectCall(true).setDisallowCall(true)
            } else if (shouldSilentCall)
                response.setSilenceCall(true)
        }
        respondToCall(callDetails, response.build())
    }
}