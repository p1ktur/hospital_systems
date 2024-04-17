package app_shared.domain.model.util.numbers

fun Int.asOrdinal(): String {
    return when (this) {
        1 -> "${this}st"
        2 -> "${this}nd"
        3 -> "${this}rd"
        else -> "${this}th"
    }
}