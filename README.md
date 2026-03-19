# Karigar - On-Demand Services App 🛠️

Karigar is a native Android application designed to connect users with professional service providers like Electricians, Plumbers, Painter, Cleaner, Security, AC Repair technicians, and more. It aims to offer a seamless platform for booking on-demand home services quickly and reliably.

## 🚀 Features (Currently Implemented)

### 👤 For Customers (Users)
- **Authentication System:** Secure login interface with mobile OTP verification scaffolding.
- **Service Browsing & Search:** View a comprehensive grid of available services. Includes a dynamic, real-time search filter to quickly find specific services (e.g., Laundry, AC Repair).
- **Order Management & Bidding:** 
  - Place customized service requests.
  - **Bidding System:** View and interact with quotes/bids from various service providers.
  - **Active & Pending Orders:** Track the live status of ongoing requests.
  - **Order History:** Access detailed logs of past completed bookings.
- **In-App Wallet:** Dedicated digital wallet module for managing funds and transactions seamlessly.
- **Favorite Workers:** Ability to "Heart" or save preferred workers/professionals to quickly re-hire them for future tasks.
- **In-App Chat System:** Real-time messaging interface allowing customers and workers to communicate directly about the task details.
- **Promotions & Discounts:** Dedicated section to apply promo codes and avail discount offers on services.
- **Notifications:** In-app notification system to keep users updated on order status, new bids, and system messages.
- **Profile Management:**
  - View and edit personal profile details securely.
  - Upload profile pictures by choosing from the Gallery or capturing directly from the Camera.
  - Premium UI design featuring circular profile frames and modern layouts.
- **Support:** "Contact Us" integration for quick customer assistance.

### 💼 For Service Providers (Partners)
- **Become a Worker:** A dedicated onboarding and registration flow that empowers skilled individuals to sign up, verify their profiles, and start receiving service requests.
- **Register a Company:** Allows contractor agencies and professional service companies to register their business on the platform to provide services and manage multiple workers.

## 🚧 Future Roadmap (TODO / Pending)
The project is under active development. The following features are planned for upcoming version releases:
- [ ] **Real-time SMS Verification:** Replace the dummy verification with a production-ready real-time SMS OTP system (e.g., Firebase Authentication).
- [ ] **Advanced Map Integration:** Implement accurate location tracking using Google Maps APIs, and map-based precise address selection for service delivery.
- [ ] **Live Payment Gateway:** Integrate robust external online payment processors (like Stripe/Razorpay) to fully power the Wallet and checkout sections.
- [ ] **Advanced Push Notifications:** Setup Firebase Cloud Messaging (FCM) to trigger background alerts for important events.

## 🛠️ Tech Stack & Tools
- **Language:** Kotlin
- **Architecture & UI:** Native Android XML Layouts, Material Design Components, custom Vector Drawables.
- **Key Libraries:** `androidx.core`, `de.hdodenhof:circleimageview`, Google Maps Services, Firebase Base integrations.

## 📌 Development Status
*Active Development* - This repository represents an iterative approach to building the application. Code is pushed in stages as new UI components, features, and backend integrations are finalized.
