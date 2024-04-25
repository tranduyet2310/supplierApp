package com.example.suppileragrimart.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        fun Long.formatPrice(): String {
            val symbols = DecimalFormatSymbols().apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            }
            val formatter = DecimalFormat("#,###", symbols)
            return formatter.format(this)
        }

        fun formatYield(yield: Double): String {
            val convertValue: Double
            val yieldText: String

            if (yield >= 1000) {
                convertValue = yield / 1000
                yieldText = "${convertValue} " + Constants.TAN_UNIT
            } else if (yield >= 100) {
                convertValue = yield / 100
                yieldText = "${convertValue} " + Constants.TA_UNIT
            } else if (yield >= 10) {
                convertValue = yield / 10
                yieldText = "${convertValue} " + Constants.YEN_UNIT
            } else {
                yieldText = "${yield} " + Constants.KG_UNIT
            }
            return yieldText
        }

        fun getCurrentMonth(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val currentDate = Date()
            return dateFormat.format(currentDate)
        }

        fun getMonth(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.MONTH)
        }

        fun updateMonthInString(month: Int): String {
            val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, month)
            val updatedDate = calendar.time
            return dateFormat.format(updatedDate)
        }
    }
}