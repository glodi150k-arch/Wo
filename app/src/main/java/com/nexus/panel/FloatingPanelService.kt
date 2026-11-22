package com.nexus.panel

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class FloatingPanelService : Service() {
    private lateinit var windowManager: WindowManager
    private var panel: View? = null
    private var expanded = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (!android.provider.Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showPanel()
    }

    private fun showPanel() {
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 10, 10, 10)
            background = rounded(Color.rgb(13, 7, 10), Color.rgb(255, 35, 55), 3, 18)
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val menu = label("☰", 24f, Color.WHITE).apply {
            setPadding(12, 4, 14, 4)
            setOnClickListener { expanded = !expanded; content.visibility = if (expanded) View.VISIBLE else View.GONE }
        }
        val title = label("PAINEL NEXUS", 17f, Color.rgb(255, 55, 75)).apply {
            typeface = Typeface.DEFAULT_BOLD
        }
        header.addView(menu)
        header.addView(title)
        outer.addView(header)

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 2)
        }
        content.addView(label("HOME  •  Interface demonstrativa", 12f, Color.WHITE))
        content.addView(toggleRow("ESP LINE"))
        content.addView(toggleRow("ESP BOX"))
        content.addView(toggleRow("ESP DISTÂNCIA"))
        content.addView(toggleRow("ESP ESQUELETO"))
        content.addView(label("VPN LOCAL  •  autorização concedida", 11f, Color.LTGRAY))
        content.addView(label("Sem leitura de memória ou dados do jogo.", 10f, Color.LTGRAY))
        outer.addView(content)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 12
            y = 80
        }

        header.setOnTouchListener(object : View.OnTouchListener {
            private var startX = 0
            private var startY = 0
            private var touchX = 0f
            private var touchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = params.x; startY = params.y
                        touchX = event.rawX; touchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = startX + (event.rawX - touchX).toInt()
                        params.y = startY + (event.rawY - touchY).toInt()
                        windowManager.updateViewLayout(outer, params)
                        return true
                    }
                }
                return false
            }
        })

        panel = outer
        windowManager.addView(outer, params)
    }

    private lateinit var content: LinearLayout

    private fun toggleRow(name: String): View {
        var enabled = false
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val text = label(name, 13f, Color.WHITE)
            val state = label("OFF", 12f, Color.rgb(255, 70, 90)).apply {
                typeface = Typeface.DEFAULT_BOLD
                setPadding(16, 6, 8, 6)
            }
            addView(text, LinearLayout.LayoutParams(150, -2))
            addView(state)
            setPadding(4, 7, 4, 7)
            setOnClickListener {
                enabled = !enabled
                state.text = if (enabled) "ON" else "OFF"
                state.setTextColor(if (enabled) Color.GREEN else Color.rgb(255, 70, 90))
            }
        }
    }

    private fun label(value: String, size: Float, color: Int) =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            setPadding(4, 5, 4, 5)
        }

    private fun rounded(fill: Int, stroke: Int, width: Int, radius: Int) =
        GradientDrawable().apply {
            setColor(fill)
            setStroke(width, stroke)
            cornerRadius = radius.toFloat()
        }

    override fun onDestroy() {
        panel?.let { if (::windowManager.isInitialized) windowManager.removeView(it) }
        panel = null
        super.onDestroy()
    }
}
