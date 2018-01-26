package com.afzaln.inapppurchases

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber




class MainActivity : AppCompatActivity() {

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(this).setListener { responseCode, purchases ->
            if (responseCode == BillingResponse.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else if (responseCode == BillingResponse.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Timber.d("Error: User cancelled")
            } else {
                // Handle any other error codes.
                Timber.d("Error code: %d", responseCode)
            }
        }.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val flowParams = BillingFlowParams.newBuilder()
                .setSku("iaptest_1")
                .setType(SkuType.INAPP)
                .build()
            val responseCode = billingClient.launchBillingFlow(this, flowParams)
        }

        startBillingConnection(billingClient)
    }

    private fun startBillingConnection(billingClient: BillingClient) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }

            override fun onBillingSetupFinished(responseCode: Int) {
                if (responseCode == BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    queryPurchase()
                }
            }
        })
    }

    /**
     * Check user's purchases
     */
    private fun queryPurchase() {
        billingClient.queryPurchaseHistoryAsync(SkuType.INAPP) { responseCode, purchasesList ->
            if (responseCode == BillingResponse.OK && purchasesList != null) {
                for (purchase in purchasesList) {
                    Timber.d("purchased: %s", purchase.orderId)
                }
            }
        }
    }

    private var premiumUpgradePrice: String? = ""

    private var gasPrice: String? = ""

    private fun queryAvailableSkus() {
        val skuList = listOf("premium_upgrade", "gas")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build(), { responseCode, skuDetailsList ->
            if (responseCode == BillingResponse.OK && skuDetailsList != null) {
                for (skuDetail in skuDetailsList) {
                    val sku = skuDetail.sku
                    val price = skuDetail.price
                    if ("premium_upgrade" == sku) {
                        premiumUpgradePrice = price
                    } else if ("gas" == sku) {
                        gasPrice = price
                    }
                }
            }
        })
    }

    /**
     * Confirm user's purchase
     */
    private fun handlePurchase(purchase: Purchase) {
        Toast.makeText(this, "Purchase made: " + purchase.orderId, Toast.LENGTH_SHORT).show()
    }
}
