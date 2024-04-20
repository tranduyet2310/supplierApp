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
            return yieldText;
        }
    }
}