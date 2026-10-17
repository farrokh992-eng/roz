package ir.rooznegaar.calendar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun label(text: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        this.text = text; textSize = size; setTextColor(color); gravity = Gravity.CENTER
        if (bold) typeface = android.graphics.Typeface.create("sans-serif-medium", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245,247,244)
        window.navigationBarColor = Color.rgb(245,247,244)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(dp(26), dp(28), dp(26), dp(28)); setBackgroundColor(Color.rgb(245,247,244))
        }
        root.addView(label("روزنگار", 30f, Color.rgb(25,124,112), true), LinearLayout.LayoutParams(-1,-2))
        val sub = label("تاریخ امروز، همیشه همراهت", 14f, Color.rgb(124,137,132))
        val subParams = LinearLayout.LayoutParams(-1,-2); subParams.topMargin = dp(8); subParams.bottomMargin = dp(34)
        root.addView(sub, subParams)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(dp(22),dp(28),dp(22),dp(28)); setBackgroundColor(Color.WHITE); elevation = dp(2).toFloat()
        }
        card.addView(label("امروز به تاریخ شمسی",13f,Color.rgb(124,137,132)))
        val date = label(PersianDateFormatter.now().full,23f,Color.rgb(23,45,41),true)
        val dateParams = LinearLayout.LayoutParams(-1,-2); dateParams.topMargin=dp(18); dateParams.bottomMargin=dp(16)
        card.addView(date,dateParams)
        status = label("اعلان تاریخ فعال است",13f,Color.rgb(25,124,112))
        card.addView(status)
        root.addView(card,LinearLayout.LayoutParams(-1,-2))

        val button = label("فعال‌سازی اعلان تاریخ",16f,Color.WHITE,true).apply {
            setPadding(dp(18),dp(16),dp(18),dp(16)); setBackgroundColor(Color.rgb(25,124,112))
            setOnClickListener { requestOrStart() }
        }
        val bp=LinearLayout.LayoutParams(-1,-2); bp.topMargin=dp(24); root.addView(button,bp)
        val note = label("برای نمایش اعلان، مجوز اعلان را فعال نگه دار.",12f,Color.rgb(124,137,132))
        val np=LinearLayout.LayoutParams(-1,-2); np.topMargin=dp(22); root.addView(note,np)
        setContentView(root)
        requestOrStart()
    }

    private fun requestOrStart() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        } else {
            try {
                val i=Intent(this,DateNotificationService::class.java)
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) startForegroundService(i) else startService(i)
                status.text="اعلان تاریخ فعال است"
                Toast.makeText(this,"اعلان تاریخ فعال شد",Toast.LENGTH_SHORT).show()
            } catch (_: Exception) { status.text="مجوز اعلان را در تنظیمات بررسی کن" }
        }
    }
    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<out String>, grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode==100 && grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) requestOrStart()
        else status.text="مجوز اعلان فعال نیست"
    }
}
