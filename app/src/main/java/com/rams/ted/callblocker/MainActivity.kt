package com.rams.ted.callblocker

import android.app.role.RoleManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rams.ted.callblocker.model.BlockList
import com.rams.ted.callblocker.model.getAsString
import com.rams.ted.callblocker.model.toBlockList
import com.rams.ted.callblocker.ui.theme.CallBlockerTheme
import com.rams.ted.callblocker.ui.theme.satoshiFont

class MainActivity : ComponentActivity() {

    private val callerIdScreeningLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
                    if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                        roleChangeListener?.invoke(true)
                    } else {
                        roleChangeListener?.invoke(false)
                    }
                }
            } else {
                this.recreate()
            }
        }
    }

    private val permLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsValue(false)) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var roleChangeListener: ((Boolean) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callerIdScreeningLauncher
        permLauncher

        val sp = getSharedPreferences("prefs", MODE_PRIVATE)

        var permissions = arrayOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.CALL_PHONE,
        )

        if (Build.VERSION.SDK_INT >= 26) {
            /*permissions += android.Manifest.permission.ANSWER_PHONE_CALLS*/
            permissions += android.Manifest.permission.READ_PHONE_NUMBERS
        }

        permLauncher.launch(permissions)

        setContent {
            CallBlockerTheme {

                val blockList = sp?.getString("blockList", null)?.toBlockList() ?: BlockList()

                var blockListNumbers by remember {
                    mutableStateOf(
                        (blockList).numbers
                    )
                }

                var isBlocking by remember {
                    mutableStateOf(
                        sp.getBoolean("blockAllCalls", false) && canBlockCalls()
                    )
                }

                roleChangeListener = {
                    if (it) {
                        isBlocking = true
                        sp.edit().putBoolean("blockAllCalls", true).apply()
                    } else {
                        Toast.makeText(
                            this,
                            "Can't Block calls without being set as default Spam & Caller ID Application",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CallBlockerUI(
                        blockListNumbers,
                        isBlocking,
                        blockAllCalls = {
                            if (canBlockCalls()) {
                                isBlocking = it
                                sp.edit().putBoolean("blockAllCalls", it).apply()
                            } else {
                                requestCallScreeningPermission()
                            }
                        },
                        blockNumber = {
                            if (blockList.numbers.contains(it)) {
                                Toast.makeText(
                                    this,
                                    "Number already blocked",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val tempList = blockList.numbers.toMutableList()
                                tempList.add(it)
                                blockListNumbers = tempList.toList()
                                sp.edit().putString("blockList", blockList.getAsString()).apply()
                                Toast.makeText(
                                    this,
                                    "Number blocked",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }

    private fun canBlockCalls(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager =
                getSystemService(ROLE_SERVICE) as RoleManager
            return roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        } else {
            true
        }
    }

    private fun requestCallScreeningPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager =
                getSystemService(ROLE_SERVICE) as RoleManager
            if (!roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                Toast.makeText(this, "Call screening not available", Toast.LENGTH_SHORT).show()
                return
            }
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            callerIdScreeningLauncher.launch(intent)
        }
    }

}

@Composable
fun CallBlockerUI(
    blockListNumbers: List<String>,
    isBlocking: Boolean,
    blockAllCalls: (Boolean) -> Unit = {},
    blockNumber: (String) -> Unit = {}
) {
    var number by remember {
        mutableStateOf("")
    }
    var numberError by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Call Blocker",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = satoshiFont
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Block all calls",
                modifier = Modifier
                    .padding(10.dp),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = satoshiFont
            )
            Switch(
                checked = isBlocking,
                onCheckedChange = {
                    blockAllCalls(it)
                }
            )
        }

        Text(
            "Note: This app will not work if it is not set as default Spam & Caller ID Application",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            text = "Block a number",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = number,
            onValueChange = {

                number = it },
            label = { Text("Enter number to Block", fontFamily = satoshiFont) },
            isError = numberError.isNotEmpty(),
            suffix = {
                Text(
                    text = numberError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = satoshiFont
                )
            },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                if (number.isEmpty() || number.isBlank()) {
                    numberError = "Please enter a valid Mobile Number"
                } else {
                    blockNumber(number)
                    numberError = ""
                    number = ""
                }
            },
            modifier = Modifier
                .padding(10.dp)
                .widthIn(min = 200.dp)
        ) {
            Text(text = "Block Number", fontFamily = satoshiFont)
        }

        Spacer(modifier = Modifier.padding(10.dp))



        if (blockListNumbers.isNotEmpty()) {
            Text(
                "Blocked Numbers",
                fontFamily = satoshiFont,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge
            )
            LazyColumn {
                itemsIndexed(blockListNumbers) { index, numberTxt ->
                    Text(
                        text = "${index + 1}. $numberTxt",
                        fontFamily = satoshiFont,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

    }
}
