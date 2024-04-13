package app_shared.domain.model.database

import java.sql.*

fun ResultSet.getIntOrNull(columnIndex: Int): Int? {
    val result = getInt(columnIndex)
    return if (wasNull()) null else result
}