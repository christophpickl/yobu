package yobu.christophpickl.github.com.yobu.logic.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import yobu.christophpickl.github.com.yobu.logic.QuestionStatistic
import yobu.christophpickl.github.com.yobu.logic.QuestionStatisticsRepository
import yobu.christophpickl.github.com.yobu.common.*
import java.util.*


/**
 * Repository impl.
 */
class QuestionStatisticsSqliteRepository(context: Context) : QuestionStatisticsRepository {
    companion object {

        private val LOG = LOG(QuestionStatisticsSqliteRepository::class.java)
    }
/*
A resource was acquired at attached stack trace but never released. See java.io.Closeable for information on avoiding resource leaks.
                                                                  java.lang.Throwable: Explicit termination method 'close' not called
                                                                      at dalvik.system.CloseGuard.open(CloseGuard.java:184)
                                                                      at android.database.sqlite.SQLiteConnectionPool.open(SQLiteConnectionPool.java:190)
                                                                      at android.database.sqlite.SQLiteConnectionPool.open(SQLiteConnectionPool.java:177)
                                                                      at android.database.sqlite.SQLiteDatabase.openInner(SQLiteDatabase.java:804)
                                                                      at android.database.sqlite.SQLiteDatabase.open(SQLiteDatabase.java:789)
                                                                      at android.database.sqlite.SQLiteDatabase.openDatabase(SQLiteDatabase.java:694)
 */
    private val sqlOpen = QuestionStatisticsOpenHelper(context)

    override fun read(id: String): QuestionStatistic? {
        LOG.d { "read($id)" }

        val cursor = sqlOpen.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME WHERE ${Column.ID.key} = ?", arrayOf(id))

        if (cursor.count == 0) {
            return null
        }
        cursor.moveToFirst()
        val statistic = cursor.readQuestionStatistic()
        cursor.close()
        return statistic
    }

    override fun readAll(): List<QuestionStatistic> {
        LOG.d("readAll()")
        // new String[] { "(SELECT max(column1) FROM table1) AS max" }
        // http://stackoverflow.com/questions/10600670/sqlitedatabase-query-method

        val cursor = sqlOpen.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY ${Column.ID.key}", null)
        try {
            if (cursor.count == 0) {
                return emptyList()
            }

            val statistics = mutableListOf<QuestionStatistic>()
            cursor.moveToFirst()
            do {
                statistics += cursor.readQuestionStatistic()
            } while (cursor.moveToNext())
            return statistics
        } finally {
            cursor.close()
        }
    }

    private fun Cursor.readQuestionStatistic() = QuestionStatistic(
            id = readString(Column.ID),
            countRight = readInt(Column.COUNT_RIGHT),
            countWrong = readInt(Column.COUNT_WRONG),
            lastRight = readDate(Column.LAST_RIGHT),
            lastWrong = readDate(Column.LAST_WRONG)
    )



    override fun insertOrUpdate(statistic: QuestionStatistic) {
        LOG.d { "insertOrUpdate($statistic)" }
        val count = sqlOpen.readCount("SELECT ${Column.ID.key} FROM $TABLE_NAME WHERE ${Column.ID.key} = ?", arrayOf(statistic.id))
        val shouldInsert = count == 0

        val props = Column.mapAllToSqlProp(statistic)
        if (shouldInsert) {
            sqlOpen.insert(props)
        } else {
            sqlOpen.update(statistic.id, props)
        }
    }

}


private val TABLE_NAME = "tbl_question_statistics"

private enum class Column(
        val key: String,
        val type: String,
        val isPrimary: Boolean = false
) {

    ID("id", "TEXT", isPrimary = true) {
        override fun toSqlProp(statistic: QuestionStatistic) = SqlProp.SqlPropString(this, statistic.id)
    },
    COUNT_RIGHT("count_right", "INTEGER") {
        override fun toSqlProp(statistic: QuestionStatistic) = SqlProp.SqlPropInt(this, statistic.countRight)
    },
    COUNT_WRONG("count_wrong", "INTEGER") {
        override fun toSqlProp(statistic: QuestionStatistic) = SqlProp.SqlPropInt(this, statistic.countWrong)
    },
    LAST_RIGHT("last_right", "TEXT") {
        override fun toSqlProp(statistic: QuestionStatistic) = SqlProp.SqlPropDate(this, statistic.lastRight)
    },
    LAST_WRONG("last_wrong", "TEXT") {
        override fun toSqlProp(statistic: QuestionStatistic) = SqlProp.SqlPropDate(this, statistic.lastWrong)
    },
    ;

    companion object {
        fun mapAllToSqlProp(statistic: QuestionStatistic) = values().map { it.toSqlProp(statistic) }
    }

    fun toCreateSql() = "$key $type ${if (isPrimary) " PRIMARY KEY" else ""}"
    abstract fun toSqlProp(statistic: QuestionStatistic): SqlProp

}
private fun Cursor.readString(column: Column) = readString(column.key)
private fun Cursor.readInt(column: Column) = readInt(column.key)
private fun Cursor.readDate(column: Column) = readNullableDate(column.key)

private val ALL_COLUMNS: Array<String> = Column.values().map { it.key }.toTypedArray()

/**
 * Database opener.
 */
private class QuestionStatisticsOpenHelper internal constructor(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val LOG = LOG(QuestionStatisticsOpenHelper::class.java)

        private val DATABASE_NAME = "db_gadsu"
        private val DATABASE_VERSION = 1

        private val SQL_TABLE_CREATE =
                """
                CREATE TABLE $TABLE_NAME (
                    ${Column.values().map(Column::toCreateSql).joinToString(", ")}
                );
                """
    }

    fun insert(props: List<SqlProp>) {
        transactional {
            insert(TABLE_NAME, null, SqlProp.build(props))
        }
    }

    fun update(id: String, props: List<SqlProp>) {
        transactional {
            update(TABLE_NAME, SqlProp.build(props), "${Column.ID.key} = ?", arrayOf(id))
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        LOG.i("onCreate(db=$db)")
        db.execSQL(SQL_TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        LOG.i("onUpgrade(db=$db, oldVersion=$oldVersion, newVersion=$newVersion)")
    }

}

/**
 * Generic SQL infrastructure class.
 */
private sealed class SqlProp(val column: Column) {

    companion object {
        fun build(props: List<SqlProp>) = ContentValues().apply {
            props.forEach { prop ->
                prop.putYourselfTo(this)
            }
        }
    }

    protected abstract fun putYourselfTo(values: ContentValues)

    class SqlPropString(column: Column, val value: String) : SqlProp(column) {
        override fun putYourselfTo(values: ContentValues) {
            values.put(column.key, value)
        }
    }

    class SqlPropInt(column: Column, val value: Int) : SqlProp(column) {
        override fun putYourselfTo(values: ContentValues) {
            values.put(column.key, value)
        }
    }

    class SqlPropDate(column: Column, val value: Date?) : SqlProp(column) {
        override fun putYourselfTo(values: ContentValues) {
            if (value != null) {
                // ContentValues do not support any date type! so store as formatted string ;)
                values.put(column.key, value.formatDateTime())
            }
        }
    }

}

