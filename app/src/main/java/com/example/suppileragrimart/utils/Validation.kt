package com.example.suppileragrimart.utils

import android.util.Patterns

class Validation {
    companion object {
        private const val PASSWORD_MIN_LENGTH = 8
        private const val NAME_MIN_LENGTH = 3
        private const val SHOP_NAME_MIN_LENGTH = 8
        private const val PROVINCE_MIN_LENGTH = 6
        private const val PHONE_NUMBER_MIN_LENGHT = 8
        private const val PHONE_NUMBER_MAX_LENGHT = 11
        private const val CCCD_LENGTH = 12
        private const val TAX_MIN_LENGTH = 10
        private const val TAX_MAX_LENGTH = 13

        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidShopName(shopName: String): Boolean {
            return shopName.length >= SHOP_NAME_MIN_LENGTH
        }

        fun isValidPassword(password: String): Boolean {
            return password.length >= PASSWORD_MIN_LENGTH
        }

        fun isValidName(name: String): Boolean {
            return name.length >= NAME_MIN_LENGTH
        }

        fun isValidPhone(phone: String): Boolean {
            return phone.length >= PHONE_NUMBER_MIN_LENGHT && phone.length <= PHONE_NUMBER_MAX_LENGHT
        }

        fun isValidTaxNumber(tax: String): Boolean {
            return tax.length >= TAX_MIN_LENGTH && tax.length <= TAX_MAX_LENGTH
        }

        fun isValidCCCD(cccd: String): Boolean {
            return cccd.length == CCCD_LENGTH
        }

        fun isValidProvince(province: String): Boolean {
            return province.length >= PROVINCE_MIN_LENGTH
        }
    }
}