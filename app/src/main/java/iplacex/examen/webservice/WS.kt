package iplacex.examen.webservice

import com.squareup.moshi.Json

data class DolarResultado(
    @Json(name = "dolar") val dolar: DolarObjeto
)

data class DolarObjeto(
    @Json(name = "valor") val valor: Double
)