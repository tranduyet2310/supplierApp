package com.example.suppileragrimart.utils

import android.content.Context
import com.example.suppileragrimart.model.Supplier
import java.io.InputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        lateinit var certsInputStream: InputStream
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

        fun formatCurrentMonth(): String {
            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val currentDate = Date()
            return dateFormat.format(currentDate)
        }

        fun getCurrentMonth(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val currentDate = Date()
            return dateFormat.format(currentDate)
        }

        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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

        fun decryptData(supplier: Supplier?, secretKey: String, iv: String): Supplier {
            val aes = AES.getInstance()
            aes.initFromString(secretKey, iv)
            val decryptedData = Supplier()

            if (supplier != null) {
                decryptedData.id = supplier.id
                if (supplier.contactName != null)
                    decryptedData.contactName = aes.decrypt(supplier.contactName)
                if (supplier.shopName != null)
                    decryptedData.shopName = aes.decrypt(supplier.shopName)
                if (supplier.email != null)
                    decryptedData.email = aes.decrypt(supplier.email)
                if (supplier.phone != null)
                    decryptedData.phone = aes.decrypt(supplier.phone)
                if (supplier.cccd != null)
                    decryptedData.cccd = aes.decrypt(supplier.cccd)
                if (supplier.tax_number != null)
                    decryptedData.tax_number = aes.decrypt(supplier.tax_number)
                if (supplier.address != null)
                    decryptedData.address = aes.decrypt(supplier.address)
                if (supplier.province != null)
                    decryptedData.province = aes.decrypt(supplier.province)
                if (supplier.password != null)
                    decryptedData.password = aes.decrypt(supplier.password)
                if (supplier.sellerType != null)
                    decryptedData.sellerType = aes.decrypt(supplier.sellerType)
                if (supplier.bankAccountNumber != null)
                    decryptedData.bankAccountNumber = aes.decrypt(supplier.bankAccountNumber)
                if (supplier.bankAccountOwner != null)
                    decryptedData.bankAccountOwner = aes.decrypt(supplier.bankAccountOwner)
                if (supplier.bankName != null)
                    decryptedData.bankName = aes.decrypt(supplier.bankName)
                if (supplier.bankBranchName != null)
                    decryptedData.bankBranchName = aes.decrypt(supplier.bankBranchName)
                if (supplier.avatar != null)
                    decryptedData.avatar = supplier.avatar
            }
            return decryptedData
        }

        fun readRawResource(context: Context, resId: Int) {
            certsInputStream = context.resources.openRawResource(resId)
        }

    }
}