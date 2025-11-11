# PaymentTransferService-Android
This project implements a simple internal **fund transfer feature** between user accounts within a digital banking platform.  
The goal was to design a **robust, secure, and maintainable transfer service**, built with clean architecture principles and modern Android tools.

---

## 💡 Architecture & Design Approach

I used a **Clean Architecture** structure combined with **MVVM** for presentation.  
The business logic is isolated, testable, and independent from any framework layer.  
The UI layer uses Jetpack Compose for a modern, reactive approach.

### Layers Overview

| Layer | Description |
|-------|--------------|
| **core** | Base types and value classes (`Money`, `AccountId`) |
| **domain** | Business logic, models, repositories, and the main use case (`TransferFundsUseCase`) |
| **data** | Repository implementations (in-memory for demo purposes) |
| **presentation** | `ViewModel` and Compose UI components |

The structure keeps the project lightweight yet demonstrates production-level organization and scalability.

---

## ⚙️ Tech Stack

- **Kotlin** (Coroutines, Flows, value classes)
- **Jetpack Compose** (Material 3)
- **MVVM pattern**
- **Clean Architecture principles**
- **Kotlinx Coroutines + Mutex** for thread-safe atomic operations
- **Gradle Kotlin DSL**

---
