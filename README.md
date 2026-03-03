<div align="center">

# 📱 UPI Offline Assistant

**Scan UPI QR codes and pay via USSD (\*99#) — no internet required.**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-orange.svg)](https://developer.android.com)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](#)

</div>

---

## 📖 About

**UPI Offline Assistant** is an open-source Android application that allows users to scan UPI QR codes offline and initiate telecom-based USSD (\*99#) payments — without any internet connection.

It does **not** process payments itself. Instead, it extracts UPI details from QR codes and guides you through the USSD payment flow available on all Indian telecom networks via the `*99#` service.

> ⚠️ This app does **not** replace official UPI apps. USSD service availability depends on your telecom operator and bank support.

---

## ✨ Features

| Feature | Details |
|---------|---------|
| 📷 **Offline QR Scanning** | Powered by ML Kit (bundled, no network calls) |
| 🔍 **UPI ID Extraction** | Parses `upi://pay?` format — extracts payee, VPA, amount |
| 📋 **Clipboard Copy** | UPI ID auto-copied for easy USSD entry |
| 📞 **Dialer Launch** | Auto-dials `*99#` USSD for payment initiation |
| 🧭 **Step-by-Step Guide** | On-screen USSD payment instructions |
| 🔒 **No Internet Permission** | Zero network access — confirmed in manifest |
| 🚫 **No Tracking** | No analytics, no crash reporting, no telemetry |
| 🗄️ **No Data Storage** | UPI PIN and transaction data are never stored |
| 🌙 **Dark Theme** | Material 3 dark UI throughout |

---

## 📸 Screens

| Home | QR Scanner | Confirmation | USSD Steps | About |
|------|-----------|--------------|------------|-------|
| Scan button + offline badge | Live camera scanner | UPI ID + amount entry | 4-step USSD guide | App info + links |

---

## 🔁 App Flow

```
Home Screen
    └── Tap "Scan QR (Offline UPI)"
            └── Camera scans UPI QR code
                    └── Validation (upi://pay? format)
                            └── Confirmation Screen
                                    └── [Optional] Enter amount
                                            └── Tap "Pay via USSD (*99#)"
                                                    ├── UPI ID copied to clipboard
                                                    ├── Dialer opens with *99#
                                                    └── USSD Instruction Screen
                                                            └── Follow 4 steps to complete payment
```

---

## 🛠️ Tech Stack

| Component | Library / Tool |
|-----------|---------------|
| Language | Kotlin |
| Architecture | MVVM (ViewModel + LiveData) |
| Camera | CameraX |
| QR Scanning | ML Kit Barcode Scanner (bundled) |
| UI | Material Components 3 |
| View Binding | Android ViewBinding |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 34 (Android 14) |

---

## 📁 Project Structure

```
app/src/main/
├── java/com/offlineupi/app/
│   ├── camera/
│   │   └── QrCodeAnalyzer.kt          # CameraX image analysis
│   ├── model/
│   │   └── UpiPaymentData.kt          # Data model
│   ├── repository/
│   │   └── UpiQrParser.kt             # QR parsing logic
│   ├── ui/
│   │   ├── MainActivity.kt            # Home screen
│   │   ├── ScanQrActivity.kt          # Camera + scanning
│   │   ├── ConfirmationActivity.kt    # Payment details
│   │   ├── UssdInstructionActivity.kt # USSD guide
│   │   └── AboutActivity.kt           # About screen
│   └── viewmodel/
│       ├── ScanQrViewModel.kt
│       └── ConfirmationViewModel.kt
└── res/
    ├── layout/                        # XML layouts
    ├── drawable/                      # Vector icons
    └── values/                        # Strings, colors, themes
```

---

## 🔐 Permissions

| Permission | Why |
|------------|-----|
| `CAMERA` | Required for QR code scanning |
| `CALL_PHONE` | Required to auto-dial `*99#` USSD |

> ❌ **No `INTERNET` permission.** The app is fully offline.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 34
- A device or emulator running Android 8.0+

### Build & Run

```bash
git clone https://github.com/darsaliq00/NoNet-UPI.git
cd NoNet-UPI
./gradlew assembleDebug
```

Install the APK:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🌐 Community & Links

- 💻 **GitHub:** [github.com/darsaliq00/NoNet-UPI](https://github.com/darsaliq00/NoNet-UPI)
- 💬 **Telegram:** [t.me/CYPHER_222](https://t.me/CYPHER_222)

---

## 📄 License

```
Copyright 2024 darsaliq00

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

<div align="center">
Made with ❤️ for privacy-first offline payments in India
</div>
