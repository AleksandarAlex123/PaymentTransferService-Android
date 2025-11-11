💳 PaymentTransferService-Android

Overview

This Android project implements an internal payment transfer feature for a digital banking app.
It enables users to transfer funds between accounts by providing the source account ID, destination account ID, and the transfer amount.
Before processing, the system validates available funds on the source account and logs each successful transaction for audit and tracking purposes.

Tech Stack

Language: Kotlin

Architecture: MVVM + Clean Architecture

UI: Jetpack Compose

DI: Hilt

The project includes error handling on both the UI and domain layers, domain-level typed errors, and localized user feedback through Material3 snackbars.

How to Test

Run the app directly from Android Studio (no backend required — it uses in-memory data).

Use the demo accounts below to simulate transfers:

ACC-001

ACC-002

ACC-003

ACC-004

Example:

Source: ACC-001

Destination: ACC-002

Amount: 50

The app will display messages such as:

1. Transfer successful

2.️ Insufficient funds

3.️ Invalid account

All balances are displayed in EUR (€) and update immediately after each successful transfer.

Branching

All development work and commits are located on the develop branch, which served as the active development branch.
The main branch represents the stable version of the project.
