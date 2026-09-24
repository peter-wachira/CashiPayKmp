import SwiftUI

/// Describes the intentionally deferred iOS presentation layer.
struct ContentView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "creditcard.fill")
                .font(.system(size: 56))
                .foregroundStyle(Color.accentColor)
            Text("CashiPay")
                .font(.largeTitle.bold())
            Text("The Android app is the primary implementation. Shared payment logic is ready for a future iOS experience.")
                .multilineTextAlignment(.center)
                .foregroundStyle(.secondary)
        }
        .padding(32)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
