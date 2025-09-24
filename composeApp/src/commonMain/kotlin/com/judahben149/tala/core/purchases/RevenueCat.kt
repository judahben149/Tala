package com.judahben149.tala.core.purchases

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.util.isIos
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.models.CustomerInfo

fun initRevenueCat() {
    Purchases.logLevel = LogLevel.DEBUG
    Purchases.configure(
        PurchasesConfiguration.Builder(
            apiKey = if (isIos()) BuildKonfig.REVENUE_CAT_APP_STORE_API_KEY else BuildKonfig.REVENUE_CAT_PLAY_STORE_API_KEY,
        ).build(),
    )
}


fun associateUserWithRevenueCat(
    userId: String,
    onUserAssociated: (CustomerInfo, Boolean) -> Unit,
    onUserAssociationFailed: () -> Unit
) {
    Purchases.sharedInstance.logIn(
        newAppUserID = userId,
        onSuccess = { customerInfo, created ->
            onUserAssociated(customerInfo, created)
        },
        onError = { onUserAssociationFailed() }
    )
}