package com.judahben149.tala.util

/**
 * Utility for opening URLs in the default browser
 */
expect object BrowserUtil {
    /**
     * Opens the given URL in the device's default browser
     * @param url The URL to open
     */
    fun openUrl(url: String)
}

/**
 * Common URLs used in the app
 */
object AppUrls {
    const val HELP_AND_SUPPORT = "https://unmarred-physician-03d.notion.site/Tala-Speak-Support-26c0dc00fb2a80c4a72acb27cab20246?pvs=74"
    const val SEND_FEEDBACK = "https://forms.gle/UbhV4ERB34N7oB7a7"
    const val TERMS_OF_SERVICE = "https://hilarious-crisp-483da1.netlify.app/terms"
    const val PRIVACY_POLICY = "https://hilarious-crisp-483da1.netlify.app/privacy"
}