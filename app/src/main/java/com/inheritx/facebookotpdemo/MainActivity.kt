package com.inheritx.facebookotpdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.accountkit.AccountKitLoginResult
import com.facebook.accountkit.ui.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var uiManager: UIManager

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        printHashKey()

        uiManager = SkinManager(SkinManager.Skin.TRANSLUCENT, ContextCompat.getColor(this, R.color.colorPrimary))


        btn_otp.setOnClickListener { startLoginPage(LoginType.PHONE) }

        btn_email.setOnClickListener { startLoginPage(LoginType.EMAIL) }
    }

    private fun startLoginPage(loginType: LoginType) {
        if (loginType == LoginType.EMAIL) {
            val intent = Intent(this, AccountKitActivity::class.java)
            val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                    LoginType.EMAIL,
                    AccountKitActivity.ResponseType.CODE) // Use token when 'Enable client Access Token Flow' is YES
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build())
            startActivityForResult(intent, REQUEST_CODE)


        } else if (loginType == LoginType.PHONE) {
            val intent = Intent(this, AccountKitActivity::class.java)
            val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                    LoginType.PHONE,
                    AccountKitActivity.ResponseType.CODE) // Use token when 'Enable client Access Token Flow' is YES
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build())
            // Add these two lines of code
            configurationBuilder.setReadPhoneStateEnabled(true)
            configurationBuilder.setUIManager(uiManager)
            startActivityForResult(intent, REQUEST_CODE)

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) { // confirm that this response matches your request
            val loginResult = data!!.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            val toastMessage: String
            if (loginResult!!.error != null) {
                toastMessage = loginResult.error!!.errorType.message
                return

            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled"
                return
            } else {
                if (loginResult.accessToken != null) {
                    toastMessage = "Success "
                } else {
                    toastMessage = String.format("Success ")
                }
            }
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
        }
    }



    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = Base64.encodeToString(md.digest(), 0)

                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        }  catch (e: Exception) {
        }

    }

    companion object {

        val TAG = "OTP Activity"
        var REQUEST_CODE = 999
    }
}




