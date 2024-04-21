package app.domain.model.shared.payment

sealed interface Payment {

    val id: Int
    val payedAmount: Float
    val payedAccount: String
    val time: String
    val clientName: String
    val clientLogin: String
    val userClientId: Int

    data class Default(
        override val id: Int,
        override val payedAmount: Float,
        override val payedAccount: String,
        override val time: String,
        override val clientName: String,
        override val clientLogin: String,
        override val userClientId: Int,
        val helpIdType: Int, // 0 -> appointments; 1 -> hospitalizations
        val helpId: Int
    ) : Payment

    data class Sub(
        override val id: Int,
        override val payedAmount: Float,
        override val payedAccount: String,
        override val time: String,
        override val clientName: String,
        override val clientLogin: String,
        override val userClientId: Int,
        val medicalCardId: Int,
        val subject: String,
        val toPayAmount: Float,
    ) : Payment
}