package app_shared.domain.model.transactor

import java.sql.*

fun ResultSet.getIntOrNull(columnIndex: Int): Int? {
    val result = getInt(columnIndex)
    return if (wasNull()) null else result
}