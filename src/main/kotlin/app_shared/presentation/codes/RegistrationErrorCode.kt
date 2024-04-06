package app_shared.presentation.codes

fun parseRegistrationErrorCode(code: Int): String {
    return when (code) {
        1001 -> "Please enter the name."
        1002 -> "Please enter the surname."
        1003 -> "Please enter the age."
        1004 -> "Age must be a positive number."
        1005 -> "Please enter the address."
        1006 -> "Please enter the phone."
        1007 -> "Invalid phone number."
        1008 -> "Invalid email."
        1009 -> "Please enter login."
        1010 -> "Please enter password."
        1011 -> "Password is too short."
        1012 -> "This login is occupied already."
        else -> "Unknown error."
    }
}