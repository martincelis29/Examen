package iplacex.examen.webservice

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DolarService {
    @GET("/api")
    suspend fun buscar(@Query("") terminoDebusqueda: String): DolarResultado
}

object Dolar {
    fun BaseUrl(): String = "https://mindicador.cl"

    fun getService(serviceClass: Class<*>): Any {
        val adapter = KotlinJsonAdapterFactory()
        val moshi = Moshi.Builder().add(adapter).build()
        val converter = MoshiConverterFactory.create(moshi)
        val retrofit = Retrofit.Builder().addConverterFactory(converter).baseUrl(BaseUrl()).build()
        return retrofit.create(serviceClass)
    }

    fun getDolarService(): DolarService {
        return getService(DolarService::class.java) as DolarService
    }
}