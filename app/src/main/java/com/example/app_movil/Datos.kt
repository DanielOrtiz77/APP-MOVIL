package com.example.app_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize




@Parcelize
data class Datos (

    var estado: String,
    var fecha: String,
    var hora: String,
    var numero: Int,
    var usuario: String

) : Parcelable {
    override fun toString(): String {
        return "\nEstado: $estado\nFecha: $fecha\nHora: $hora\nUsuario: $usuario\n"
    }
}
