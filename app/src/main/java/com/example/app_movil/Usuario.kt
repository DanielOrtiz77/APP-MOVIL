package com.example.app_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario (
    var nombre: String,
    var email: String,
    var tipo: String,
    var contraseña: Int

) : Parcelable
