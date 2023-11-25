package com.example.netdata.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SQLManager(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "netdata.db"
        private const val DB_VERSION = 1

        // Nombre de la tabla y las columnas
        const val TABLE_NAME = "registros"
        const val COL_IR = "idregistro"
        const val COL_FO = "fechaoperacion"
        const val COL_CC = "codigocliente"
        const val COL_ZN = "zonal"
        const val COL_SN = "serialnumber"
        const val COL_RX = "rxdispnuevo"
        const val COL_TX = "txdispnuevo"
        const val COL_TP = "tempdispnuevo"
    }

    // Crear tabla si no existe
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COL_IR INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_FO TEXT, " + // DATETIME
                "$COL_CC INTEGER, " +
                "$COL_ZN TEXT, " +
                "$COL_SN TEXT, " +
                "$COL_RX REAL, " + // DECIMAL
                "$COL_TX REAL, " + // DECIMAL
                "$COL_TP REAL" + // DECIMAL
                ")")
        db.execSQL(createTable)
    }

    // Actualiza la tabla
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    data class Registro(
        val id: Long,
        val fope: String,
        val codc: Int,
        val zone: String,
        val sndn: String,
        val rxdn: Float,
        val txdn: Float,
        val temp: Float
    )

    // Insertar data
    fun insertData(codc: Int, zone: String, sndn: String, rxdn: Float, txdn: Float, temp: Float) {
        val db = this.writableDatabase
        val values = ContentValues()

        // DATETIME
        val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault())
        val fope = dateFormat.format(Date())

        // Valores a las filas
        values.put(COL_FO, fope)
        values.put(COL_CC, codc)
        values.put(COL_ZN, zone)
        values.put(COL_SN, sndn)
        values.put(COL_RX, rxdn)
        values.put(COL_TX, txdn)
        values.put(COL_TP, temp)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun readData(tipoOrden: MainActivity.TIPO_ORDEN) : List<Registro> {
        val dataList = mutableListOf<Registro>()
        val db = this.readableDatabase

        val sortOrder = when (tipoOrden) {
            MainActivity.TIPO_ORDEN.ID -> "$COL_IR ASC"
            MainActivity.TIPO_ORDEN.CODIGO -> "$COL_CC ASC"
            MainActivity.TIPO_ORDEN.ZONA -> "$COL_ZN ASC"
            MainActivity.TIPO_ORDEN.SERIAL -> "$COL_SN ASC"
        }

        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            sortOrder
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_IR))
                val fope = cursor.getString(cursor.getColumnIndexOrThrow(COL_FO))
                val codc = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CC))
                val zone = cursor.getString(cursor.getColumnIndexOrThrow(COL_ZN))
                val sndn = cursor.getString(cursor.getColumnIndexOrThrow(COL_SN))
                val rxdn = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_RX))
                val txdn = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_TX))
                val temp = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_TP))

                val data = Registro(id, fope, codc, zone, sndn, rxdn, txdn, temp)
                dataList.add(data)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dataList
    }

    fun searchData(searchText: String, tipoOrden: MainActivity.TIPO_ORDEN): List<Registro> {
        val datalist = mutableListOf<Registro>()
        val db = this.readableDatabase

        val sortOrder = when (tipoOrden) {
            MainActivity.TIPO_ORDEN.ID -> "$COL_IR ASC"
            MainActivity.TIPO_ORDEN.CODIGO -> "$COL_CC ASC"
            MainActivity.TIPO_ORDEN.ZONA -> "$COL_ZN ASC"
            MainActivity.TIPO_ORDEN.SERIAL -> "$COL_SN ASC"
        }

        val selection = when (tipoOrden) {
            MainActivity.TIPO_ORDEN.ZONA -> "$COL_ZN LIKE ?"
            MainActivity.TIPO_ORDEN.CODIGO -> "$COL_CC LIKE ?"
            MainActivity.TIPO_ORDEN.SERIAL -> "$COL_SN LIKE ?"
            else -> null
        }

        val selectionArgs = when (tipoOrden) {
            MainActivity.TIPO_ORDEN.ZONA, MainActivity.TIPO_ORDEN.CODIGO, MainActivity.TIPO_ORDEN.SERIAL -> arrayOf("%$searchText%")
            else -> null
        }

        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COL_IR))
                val fechaOperacion = getString(getColumnIndexOrThrow(COL_FO))
                val codigoCliente = getInt(getColumnIndexOrThrow(COL_CC))
                val zona = getString(getColumnIndexOrThrow(COL_ZN))
                val serialNumber = getString(getColumnIndexOrThrow(COL_SN))
                val rxDispnuevo = getFloat(getColumnIndexOrThrow(COL_RX))
                val txDispnuevo = getFloat(getColumnIndexOrThrow(COL_TX))
                val tempDispnuevo = getFloat(getColumnIndexOrThrow(COL_TP))

                val registro = Registro(id, fechaOperacion, codigoCliente, zona, serialNumber, rxDispnuevo, txDispnuevo, tempDispnuevo)
                datalist.add(registro)
            }
        }

        cursor.close()
        return datalist
    }
}