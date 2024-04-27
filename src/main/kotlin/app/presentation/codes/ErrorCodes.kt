package app.presentation.codes

fun parseDefaultErrorCode(code: Int): String {
    return when (code) {
        1001 -> "Couldn't load resource."
        1002 -> "Specify schedule fully."
        1003 -> "Couldn't upload resource."
        1101 -> "Room at this location already exists."
        1201 -> "Couldn't delete payed appointment."
        1202 -> "Couldn't delete approved appointment."
        1203 -> "You already have requested an appointment for this doctor."
        1204 -> "Doctor has no schedule to request appointment."
        else -> "Unknown error."
    }
}

fun parseLoginErrorCode(code: Int): String {
    return when (code) {
        1009 -> "Please enter login."
        1010 -> "Please enter password."
        1013 -> "Wrong login or password."
        else -> "Unknown error."
    }
}

fun parseClientRegistrationErrorCode(code: Int): String {
    return when (code) {
        1001 -> "Please enter the name."
        1002 -> "Please enter the surname."
        1003 -> "Please enter the age."
        1004 -> "Age must be a positive integer."
        1005 -> "Please enter the address."
        1006 -> "Please enter the phone."
        1007 -> "Invalid phone number."
        1008 -> "Invalid email."
        1009 -> "Please enter login."
        1010 -> "Please enter password."
        1011 -> "Password is too short."
        1012 -> "This login is occupied already."
        1013 -> "Wrong login or password."
        else -> "Unknown error."
    }
}

fun parseWorkerRegistrationErrorCode(code: Int): String {
    return when (code) {
        1001 -> "Please enter the name."
        1002 -> "Please enter the surname."
        1003 -> "Please enter the age."
        1004 -> "Age must be a positive integer."
        1005 -> "Please enter the address."
        1006 -> "Please enter the phone."
        1007 -> "Invalid phone number."
        1008 -> "Invalid email."
        1009 -> "Please enter login."
        1010 -> "Please enter password."
        1011 -> "Password is too short."
        1012 -> "This login is occupied already."
        1013 -> "Wrong login or password."
        1014 -> "Salary must be a positive number."
        1015 -> "Please enter the position."
        else -> "Unknown error."
    }
}