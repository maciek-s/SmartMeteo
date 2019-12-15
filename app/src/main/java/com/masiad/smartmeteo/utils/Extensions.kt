package com.masiad.smartmeteo.utils

fun Float.format(digits: Int) = "%.${digits}f".format(this)