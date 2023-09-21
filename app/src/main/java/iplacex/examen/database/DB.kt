package iplacex.examen.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(entities = [Lugares::class], version = 1)
abstract class LugaresDB : RoomDatabase() {
    abstract fun LugaresDao(): LugaresDao

    companion object {
        @Volatile
        private var DATABASE: LugaresDB? = null
        fun getInstance(contexto: Context): LugaresDB {
            return DATABASE ?: synchronized(this) {
                Room.databaseBuilder(
                    contexto.applicationContext, LugaresDB::class.java, "Lugares.db"
                ).fallbackToDestructiveMigration().build().also { DATABASE = it }
            }
        }
    }
}

@Entity
data class Lugares(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var nombre: String,
    val imagen: String?,
    val latitud: Double,
    val longitud: Double,
    val orden: Int,
    val costoAlojamiento: Int,
    val costoTraslados: Int,
    val comentarios: String,
) {
    val imagenUrl: String
        get() {
            return if (!imagen.isNullOrEmpty()) {
                imagen
            } else {
                "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg"
            }
        }
}

@Dao
interface LugaresDao {
    @Query("SELECT * FROM lugares ORDER BY orden")
    fun obtenerTodos(): List<Lugares>

    @Insert
    fun insertar(lugares: Lugares): Long

    @Update
    fun actualizar(lugares: Lugares)

    @Delete
    fun eliminar(lugares: Lugares)
}