import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        IOSHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
