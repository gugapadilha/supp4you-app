package com.guga.supp4youapp.domain.model

enum class AllowContinueType(val plusTime: Long) {
    During(0),
    After(0),
    After12Hour(12),
    After24Hour(24);

    companion object {
        fun mapValue(value: String): AllowContinueType {
            return when {
                value.contains("12") -> After12Hour
                value.contains("24") -> After24Hour
                value.contains("after", ignoreCase = true) -> After
                else -> During
            }
        }
    }
}