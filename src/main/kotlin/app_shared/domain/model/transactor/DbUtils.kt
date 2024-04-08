package app_shared.domain.model.transactor

import java.sql.*

fun String.parseAsTableWithToIntegers(): Pair<Int, Int>? {
    return try {
        replace("(", "").replace(")", "").split(",").run {
            this[0].toInt() to this[1].toInt()
        }
    } catch (_: Exception) {
        null
    }
}

fun ResultSet.getIntOrNull(columnIndex: Int): Int? {
    val result = getInt(columnIndex)
    return if (wasNull()) null else result
}