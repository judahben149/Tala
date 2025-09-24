//
// Created by Judah Ben on 11/08/2025.
//

import Foundation
import UIKit
import ComposeApp
import GoogleSignIn

class AppDelegate: NSObject, UIApplicationDelegate {

    // This handles app launch - this is what initializes Koin
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        print("AppDelegate: didFinishLaunchingWithOptions - Calling KMP init.")
        
        // Call your kmp initializer kotlin code!
        KMPInitializerKt.onDidFinishLaunchingWithOptions()
        
        // Configure Google Sign-In if needed
        guard let path = Bundle.main.path(forResource: "Info", ofType: "plist"),
              let plist = NSDictionary(contentsOfFile: path),
              let clientId = plist["CLIENT_ID"] as? String else {
            print("GoogleService-Info.plist not found, continuing without Google Sign-In config")
            return true
        }
        
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientId)
        
        return true
    }
    
    func application(
              _ app: UIApplication,
              open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]
            ) -> Bool {
              var handled: Bool

              handled = GIDSignIn.sharedInstance.handle(url)
              if handled {
                return true
              }

              // Handle other custom URL types.

              // If not handled by this app, return false.
              return false
        }
}
