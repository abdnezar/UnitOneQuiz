package com.abdnezar.unitonequiz.utils

enum class Loading {
    LOADING {
        override fun getLoadingState() = 0
    },
    ERROR {
        override fun getLoadingState() = 1
    },
    SUCCESS {
        override fun getLoadingState() = 2
    };

    abstract fun getLoadingState(): Int
}