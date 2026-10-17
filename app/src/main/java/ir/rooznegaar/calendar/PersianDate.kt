package ir.rooznegaar.calendar

import android.icu.util.PersianCalendar
import java.text.NumberFormat
import java.util.Locale
import java.util.TimeZone

data class PersianDate(val weekday: String, val day: String, val month: String, val year: String, val full: String)

object PersianDateFormatter {
    private val weekdays = arrayOf("یکشنبه","دوشنبه","سه‌شنبه","چهارشنبه","پنجشنبه","جمعه","شنبه")
    private val months = arrayOf("فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند")

    fun now(): PersianDate {
        val cal = PersianCalendar(TimeZone.getTimeZone("Asia/Tehran"), Locale("fa", "IR"))
        cal.timeInMillis = System.currentTimeMillis()
        val weekday = weekdays[(cal.get(PersianCalendar.DAY_OF_WEEK) - 1).coerceIn(0, 6)]
        val nf = NumberFormat.getIntegerInstance(Locale("fa", "IR")).apply { isGroupingUsed = false }
        val day = nf.format(cal.get(PersianCalendar.DAY_OF_MONTH))
        val month = months.getOrElse(cal.get(PersianCalendar.MONTH)) { "" }
        val year = nf.format(cal.get(PersianCalendar.YEAR))
        return PersianDate(weekday, day, month, year, "$weekday $day $month $year")
    }
}
