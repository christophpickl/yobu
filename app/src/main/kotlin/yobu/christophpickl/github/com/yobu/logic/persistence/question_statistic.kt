package yobu.christophpickl.github.com.yobu.logic.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import yobu.christophpickl.github.com.yobu.misc.LOG
import android.content.ContentValues
import android.database.Cursor
import yobu.christophpickl.github.com.yobu.logic.QuestionStatistic
import yobu.christophpickl.github.com.yobu.logic.QuestionStatisticsRepository


/**
 * Repository impl.
 */
class QuestionStatisticsSqliteRepository(context: Context) : QuestionStatisticsRepository {
    companion object {
        private val LOG = LOG(QuestionStatisticsSqliteRepository::class.java)
    }

    private val sqlOpen = QuestionStatisticsOpenHelper(context)

    override fun insertOrUpdate(statistic: QuestionStatistic) {
        println("insert $statistic")
        LOG.d { "insertOrUpdate($statistic)" }
        val cursor = sqlOpen.readableDatabase.rawQuery("SELECT ${Column.ID.key} FROM $TABLE_NAME WHERE ${Column.ID.key} = ?", arrayOf(statistic.id))
        val shouldInsert = cursor.count == 0
        cursor.close()
        val props = Column.mapAllToSqlProp(statistic)
        if (shouldInsert) {
            sqlOpen.transactionSaveInsert(props)
        } else {
            sqlOpen.update(statistic.id, props)
        }
    }


    override fun readAll(): List<QuestionStatistic> {
        LOG.d("readAll()")
        // new String[] { "(SELECT max(column1) FROM table1) AS max" }
        // http://stackoverflow.com/questions/10600670/sqlitedatabase-query-method
//        val whereClause = ""
//        val whereArgs = emptyArray<String>()
//        val cursor = sqlOpen.readableDatabase.query(TABLE_NAME, null, whereClause, whereArgs, null, null, null)

        val cursor = sqlOpen.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY ${Column.ID.key}", null)

        if (cursor.count == 0) {
            return emptyList()
        }

        val statistics = mutableListOf<QuestionStatistic>()
        cursor.moveToFirst()
        do {
            statistics += QuestionStatistic(
                    id = cursor.readString(Column.ID),
                    countCorrect = cursor.readInt(Column.COUNT_CORRECT)
            )
                    .apply { println("read: $this") }
        } while(cursor.moveToNext())
        cursor.close()
        return statistics
    }

}

private fun Cursor.readString(column: Column): String {
    val index = getColumnIndex(column.key)
    return getString(index)
}
private fun Cursor.readInt(column: Column): Int {
    val index = getColumnIndex(column.key)
    return getInt(index)
}


private val TABLE_NAME = "tbl_question_statistics"

private enum class Column(val key: String, val type: String, val isPrimary: Boolean = false) {

    ID("id", "TEXT", isPrimary = true) {
        override fun toSqlProp(statistic: QuestionStatistic) = SqlProp.SqlPropString(this, statistic.id)
    },
    COUNT_CORRECT("count_correct", "INTEGER") {
        override fun toSqlProp(statistic: QuestionStatistic) = SqlProp.SqlPropInt(this, statistic.countCorrect)
    }
    ;

    companion object {
        fun mapAllToSqlProp(statistic: QuestionStatistic) = values().map { it.toSqlProp(statistic) }
    }

    fun toCreateSql() = "$key $type ${if (isPrimary) " PRIMARY KEY" else ""}"
    abstract fun toSqlProp(statistic: QuestionStatistic): SqlProp

}
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
                """ // countX INTEGER
    }

    fun transactionSaveInsert(props: List<SqlProp>) {
        val contentValues = SqlProp.build(props)

        withTransaction {
            insert(TABLE_NAME, null, contentValues)
        }
    }

    fun update(id: String, props: List<SqlProp>) {
        val contentValues = SqlProp.build(props)

        withTransaction {
            writableDatabase.update(TABLE_NAME, contentValues, "id = ?", arrayOf(id))
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        println("create DB $SQL_TABLE_CREATE")
        LOG.i("onCreate(db=$db) SQL: SQL_TABLE_CREATE")
        db.execSQL(SQL_TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        LOG.i("onUpgrade(db=$db, oldVersion=$oldVersion, newVersion=$newVersion)")
    }

    private fun withTransaction(code: SQLiteDatabase.() -> Unit) {
        with(writableDatabase) {
            beginTransaction()
            try {
                code(this)
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
        }
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
}
