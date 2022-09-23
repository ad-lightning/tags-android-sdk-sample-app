package com.boltive.integration.example

import android.app.Activity

data class Example(
    val name: String,
    val activity: Class<out Activity>,
) {

    override fun toString(): String {
        return name
    }

}
