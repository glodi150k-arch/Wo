package com.nexus.panel

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.VpnService
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val red = Color.rgb(255, 35, 55)
    private val dark = Color.rgb(15, 8, 12)
    private val requestVpn = 4101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 32, 28, 28)
            setBackgroundColor(dark)
        }
        val logo = android.widget.ImageView(this).apply {
            setImageResource(com.nexus.panel.R.drawable.nexus_logo)
            adjustViewBounds = true
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            maxHeight = 250
        }
        status = TextView(this).apply {
            text = "PAINEL NEXUS\\nVPN local: autorização pendente"
            setTextColor(Color.WHITE)
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 22)
        }
        root.addView(logo, LinearLayout.LayoutParams(-1, 250))
        root.addView(status, LinearLayout.LayoutParams(-1, -2))
        root.addView(actionButton("1  •  AUTORIZAR VPN LOCAL") { requestVpnPermission() })
        root.addView(actionButton("2  •  PERMITIR SOBREPOSIÇÃO") { requestOverlayPermission() })
        root.addView(actionButton("3  •  ABRIR FREE FIRE + PAINEL") { openGameAndPanel() })
        root.addView(actionButton("PARAR PAINEL FLUTUANTE") {
            stopService(Intent(this, FloatingPanelService::class.java))
            Toast.makeText(this, "Painel flutuante parado.", Toast.LENGTH_SHORT).show()
        })
        val note = TextView(this).apply {
            text = "Interface demonstrativa. A autorização VpnService não cria um túnel nem lê memória ou dados internos do Free Fire."
            setTextColor(Color.LTGRAY)
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(8, 22, 8, 0)
        }
        root.addView(note)
        setContentView(root)
    }

    private fun actionButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            setTextColor(Color.WHITE)
            setBackgroundColor(red)
            isAllCaps = false
            setOnClickListener { action() }
            val params = LinearLayout.LayoutParams(-1, -2)
            params.setMargins(0, 8, 0, 8)
            layoutParams = params
        }

    private fun requestVpnPermission() {
        val intent = VpnService.prepare(this)
        if (intent == null) {
            status.text = "PAINEL NEXUS\\nVPN: autorização já concedida (somente permissão)"
        } else {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, requestVpn)
        }
    }

    private fun requestOverlayPermission() {
        if (Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Sobreposição já autorizada.", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = android.net.Uri.parse("package:$packageName")
            })
        }
    }

    private fun openGameAndPanel() {
        if (VpnService.prepare(this) != null) {
            Toast.makeText(this, "Autorize a VPN local primeiro.", Toast.LENGTH_LONG).show()
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Autorize a sobreposição primeiro.", Toast.LENGTH_LONG).show()
            requestOverlayPermission()
            return
        }

        val gameIntent = packageManager.getLaunchIntentForPackage("com.dts.freefireth")
            ?: packageManager.getLaunchIntentForPackage("com.dts.freefiremax")

        if (gameIntent == null) {
            Toast.makeText(this, "Free Fire não encontrado. Instale a versão desejada.", Toast.LENGTH_LONG).show()
            return
        }

        startService(Intent(this, FloatingPanelService::class.java))
        startActivity(gameIntent)
    }

    @Deprecated("Platform VPN consent result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestVpn) {
            status.text = if (resultCode == RESULT_OK) {
                "PAINEL NEXUS\\nVPN: autorização concedida (somente permissão)"
            } else {
                "PAINEL NEXUS\\nVPN: autorização não concedida"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized && Settings.canDrawOverlays(this)) {
            status.text = "PAINEL NEXUS\\nSobreposição: autorizada"
        }
    }
}
