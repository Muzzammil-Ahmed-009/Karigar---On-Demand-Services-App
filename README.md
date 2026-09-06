<p align="center">
  <img src="app/src/main/res/drawable/logo_karigar.png" alt="Karigar Logo" width="250"/>
</p>

<h1 align="center">Karigar - On-Demand Services App 🛠️</h1>

> **🚀 Architecture Update:** This project was originally built using **Android XML & Kotlin**. To adopt modern Android development practices, the entire UI has been successfully migrated to **Jetpack Compose**. 
> *To view the legacy XML implementation, check out the [`legacy-xml` branch](https://github.com/Muzzammil-Ahmed-009/Karigar---On-Demand-Services-App/tree/legacy-xml).*

Karigar is a modern, native Android application designed to connect users with professional service providers like Electricians, Plumbers, Painters, and Cleaners. It offers a seamless platform for booking on-demand home services quickly and reliably.

## 📱 Screenshots (Jetpack Compose UI)
<p align="center">
  <img src="screenshots/home_fragment.jpeg" alt="Home Screen" width="22%" />
  &nbsp;&nbsp;
  <img src="screenshots/service_booking_screen_1.jpeg" alt="Services" width="22%" />
  &nbsp;&nbsp;
  <img src="screenshots/profile_fragment.jpeg" alt="Profile" width="22%" />
  &nbsp;&nbsp;
  <img src="screenshots/order_fragment.jpeg" alt="Orders" width="22%" />
</p>

## 🛠️ Tech Stack & Architecture
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Modern Declarative UI) / *Previously XML*
- **Backend:** Firebase (Authentication, Firestore)
- **Navigation:** Compose Navigation
- **Asynchronous Programming:** Kotlin Coroutines & Flows

## 🎯 Project Scope & Current Progress

### ✅ Completed Features (What's Done)
- **Complete UI Overhaul:** Fully functional and responsive UI built entirely with Jetpack Compose.
- **Authentication Flow:** User login and registration screens (connected to Firebase Auth for real OTP verification).
- **Service Browsing:** Dynamic grid of available services with real-time search filtering.
- **Order Management System:** UI for placing requests, viewing bids, and checking active/past order history.
- **Worker Profiles & Favorites:** Ability to view worker details and save them to favorites.

### 🚧 Backend Integration Status (Firebase)
*To demonstrate both frontend architecture and full-stack capabilities, this app uses a mix of real backend integration and mocked data for UI demonstration:*
- **Firebase Authentication:** Real integration for user sign-in via SMS OTP.
- **Cloud Firestore:** Integrated for storing User Profiles and Order History.
- **Mocked Data:** Specific screens like the "Bidding System" currently utilize dummy data to demonstrate the UI/UX flow without requiring complex backend populators.

### 🚀 Future Roadmap (What's Next)
- [ ] Implement robust State Management using Hilt/Dagger for Dependency Injection.
- [ ] Connect the Bidding and Chat system to Firebase Realtime Database.
- [ ] Integrate Google Maps SDK for precise location tracking.
- [ ] Setup Firebase Cloud Messaging (FCM) for push notifications.

## 💡 Why this project?
I built this project to demonstrate my ability to develop complex, production-ready UI layouts. Migrating the codebase from XML to Jetpack Compose allowed me to showcase my adaptability to new technologies and my understanding of the Android UI lifecycle in both imperative and declarative paradigms.
