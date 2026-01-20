package io.paysky.paybutton.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TokenizedCardPaymentParameters(
    val TokenCardId: Int,
    val cvv: String
) : Parcelable
