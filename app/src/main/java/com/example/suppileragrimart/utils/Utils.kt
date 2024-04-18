package com.example.suppileragrimart.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

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
    }
}