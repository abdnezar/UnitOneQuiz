package com.abdnezar.unitonequiz.data.models

data class Home(
    val data: Data,
    val errors: List<Any>,
    val message: String,
    val status: Boolean
)