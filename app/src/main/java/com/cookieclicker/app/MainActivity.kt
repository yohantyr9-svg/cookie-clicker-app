package com.cookieclicker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.security.MessageDigest
import kotlin.math.pow

private val Bg = Color(0xFF0B0D12)
private val Panel = Color(0xFF171B25)
private val Accent = Color(0xFFF59E0B)
private val MainText = Color(0xFFF7F7FA)
private val Muted = Color(0xFF9AA3B2)

private data class Upgrade(val name: String, val description: String, val baseCost: Long, val cps: Long)

private val upgradeTypes = listOf(
    Upgrade("Cursor", "A tiny helper cursor", 15, 0),
    Upgrade("Grandma", "A cookie-baking grandma", 100, 1),
    Upgrade("Cookie Farm", "Grow cookies from the ground", 1_100, 8),
    Upgrade("Cookie Mine", "Mine cookie dough", 12_000, 47),
    Upgrade("Cookie Factory", "Mass-produce cookies", 130_000, 260),
    Upgrade("Cookie Bank", "Make cookie investments", 1_400_000, 1_400),
    Upgrade("Cookie Temple", "Bless the cookie supply", 20_000_000, 7_800),
    Upgrade("Cookie Portal", "Bring cookies from elsewhere", 330_000_000, 44_000),
    Upgrade("Cookie Time Machine", "Borrow cookies from tomorrow", 5_100_000_000, 260_000),
    Upgrade("Cookie Multiverse", "Cookies across realities", 75_000_000_000, 1_600_000)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("cookie_clicker_save", MODE_PRIVATE)
        setContent {
            var cookies by remember { mutableLongStateOf(prefs.getLong("cookies", 0L)) }
            var bought by remember { mutableStateOf((0..9).associateWith { i -> prefs.getInt("upgrade_$i", 0) }) }
            var adminHash by remember { mutableStateOf(prefs.getString("admin_hash", null)) }
            var showAdmin by remember { mutableStateOf(false) }
            var adminUnlocked by remember { mutableStateOf(false) }
            var pinInput by remember { mutableStateOf("") }
            var newPin by remember { mutableStateOf("") }
            var status by remember { mutableStateOf("") }

            fun save() {
                prefs.edit().putLong("cookies", cookies).apply {
                    bought.forEach { (index, count) -> putInt("upgrade_$index", count) }
                }.apply()
            }
            fun hashPin(pin: String): String = MessageDigest.getInstance("SHA-256")
                .digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }

            val cps = upgradeTypes.indices.sumOf { i -> upgradeTypes[i].cps * (bought[i] ?: 0).toLong() }
            MaterialTheme(colorScheme = darkColorScheme(
                background = Bg, surface = Panel, primary = Accent,
                onBackground = MainText, onSurface = MainText
            )) {
                Surface(Modifier.fillMaxSize(), color = Bg) {
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("COOKIE CLICKER", color = Accent, fontSize = 12.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
                                Text("Bake your empire.", color = MainText, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            }
                            TextButton(onClick = {
                                showAdmin = true
                                adminUnlocked = false
                                pinInput = ""
                                status = ""
                            }) { Text("ADMIN") }
                        }

                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("YOUR COOKIES", color = Muted, fontSize = 11.sp, letterSpacing = 2.sp)
                                Text("%,d".format(cookies), color = MainText, fontSize = 36.sp, fontWeight = FontWeight.Black)
                                Text("$cps cookies / second", color = Accent, fontSize = 13.sp)
                                Spacer(Modifier.height(4.dp))
                                Button(
                                    onClick = { cookies += 1L; save() },
                                    modifier = Modifier.size(172.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color(0xFF201300))
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🍪", fontSize = 48.sp)
                                        Text("+1 COOKIE", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                        Text("TAP TO BAKE", fontSize = 9.sp)
                                    }
                                }
                                Text("Every tap gives exactly 1 cookie.", color = Muted, fontSize = 12.sp)
                            }
                        }

                        Text("UPGRADES • ${bought.values.sum()}/100 LEVELS", color = MainText, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("10 upgrade types × 10 levels each = 100 levels.", color = Muted, fontSize = 12.sp)

                        upgradeTypes.forEachIndexed { index, up ->
                            val level = bought[index] ?: 0
                            val cost = (up.baseCost.toDouble() * 1.65.pow(level)).toLong().coerceAtLeast(1L)
                            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
                                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text("${index + 1}. ${up.name}", color = MainText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(up.description, color = Muted, fontSize = 11.sp)
                                        }
                                        Text("$level/10", color = Accent, fontWeight = FontWeight.Bold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("+${up.cps} cookies/s per level", color = Muted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                        Button(
                                            onClick = {
                                                if (level < 10 && cookies >= cost) {
                                                    cookies -= cost
                                                    bought = bought + (index to level + 1)
                                                    save()
                                                }
                                            },
                                            enabled = level < 10 && cookies >= cost
                                        ) {
                                            Text(if (level >= 10) "MAXED" else "BUY ${"% ,d".replace(" ", "").format(cost)}", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                        Text("Offline game • Progress saved on this device", color = Muted, fontSize = 11.sp)
                    }
                }

                if (showAdmin) {
                    AlertDialog(
                        onDismissRequest = { showAdmin = false; adminUnlocked = false },
                        containerColor = Panel,
                        title = { Text(if (adminHash == null) "Set admin PIN" else if (adminUnlocked) "Admin panel" else "Admin login") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                if (adminHash == null) {
                                    Text("Create a PIN to protect the local admin panel. Use at least 4 digits.", color = Muted, fontSize = 12.sp)
                                    OutlinedTextField(value = newPin, onValueChange = { newPin = it.filter(Char::isDigit).take(12) }, label = { Text("New PIN") }, visualTransformation = PasswordVisualTransformation())
                                    if (status.isNotEmpty()) Text(status, color = Accent, fontSize = 12.sp)
                                } else if (!adminUnlocked) {
                                    OutlinedTextField(value = pinInput, onValueChange = { pinInput = it.filter(Char::isDigit).take(12) }, label = { Text("Admin PIN") }, visualTransformation = PasswordVisualTransformation())
                                    if (status.isNotEmpty()) Text(status, color = Accent, fontSize = 12.sp)
                                } else {
                                    Text("Owner tools", color = Accent, fontWeight = FontWeight.Bold)
                                    Text("Cookies: ${"% ,d".replace(" ", "").format(cookies)}", color = MainText)
                                    OutlinedTextField(value = newPin, onValueChange = { newPin = it.filter(Char::isDigit).take(12) }, label = { Text("Set cookies amount") })
                                    Button(onClick = {
                                        val amount = newPin.toLongOrNull()
                                        if (amount != null && amount >= 0L) {
                                            cookies = amount
                                            save()
                                            status = "Cookie amount updated."
                                            newPin = ""
                                        } else status = "Enter a valid non-negative number."
                                    }) { Text("SET COOKIE BALANCE") }
                                    Button(onClick = {
                                        cookies = 0L
                                        bought = (0..9).associateWith { 0 }
                                        save()
                                        status = "Game progress reset."
                                    }) { Text("RESET ALL PROGRESS") }
                                    if (status.isNotEmpty()) Text(status, color = Accent, fontSize = 12.sp)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                when {
                                    adminHash == null -> {
                                        if (newPin.length >= 4) {
                                            adminHash = hashPin(newPin)
                                            prefs.edit().putString("admin_hash", adminHash).apply()
                                            newPin = ""
                                            adminUnlocked = true
                                            status = "PIN created."
                                        } else status = "PIN must be at least 4 digits."
                                    }
                                    !adminUnlocked -> {
                                        if (hashPin(pinInput) == adminHash) {
                                            adminUnlocked = true
                                            pinInput = ""
                                            status = ""
                                        } else status = "Incorrect PIN."
                                    }
                                    else -> { showAdmin = false; adminUnlocked = false; status = ""; newPin = "" }
                                }
                            }) { Text(if (adminHash == null) "CREATE PIN" else if (adminUnlocked) "DONE" else "UNLOCK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAdmin = false; adminUnlocked = false; status = ""; newPin = "" }) { Text("CLOSE") }
                        }
                    )
                }
            }
        }
    }
}
