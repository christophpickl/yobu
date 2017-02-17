package yobu.christophpickl.github.com.yobu.common

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*


inline fun SQLiteOpenHelper.transactional(code: SQLiteDatabase.() -> Unit) {
    writableDatabase.transactional(code)
}

inline fun SQLiteDatabase.transactional(code: SQLiteDatabase.() -> Unit) {
    beginTransaction()
    try {
        code()
        setTransactionSuccessful()
    } finally {
        endTransaction()
    }
}

fun SQLiteOpenHelper.readCount(sql: String, args: Array<String>): Int {
    readableDatabase.rawQuery(sql, args).use { cursor ->
        val count = cursor.count
        return count
    }
}

fun Cursor.readString(column: String): String {
    val index = getColumnIndexOrThrow(column)
    return getString(index)
}

fun Cursor.readInt(column: String): Int {
    val index = getColumnIndexOrThrow(column)
    return getInt(index)
}

fun Cursor.readNullableDate(column: String): Date? {
    val index = getColumnIndexOrThrow(column)
    return if (isNull(index)) null else getString(index).parseDateTime()
}
