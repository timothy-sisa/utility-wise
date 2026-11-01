# Utility-Wise

Utility-Wise is an Android app that helps students and shared households track utility meter readings, estimate costs, manage bills and split expenses. Users can also photograph or attach meter displays, bills and receipts.

## Screens

- **Dashboard:** recent readings, estimated costs and monthly spending.
- **Add Reading:** enter or edit readings, dates and tariffs, and attach photos.
- **Usage Analysis:** compare consumption and costs across billing periods.
- **Bill Management:** record bills, split expenses and track payments.

## Navigation

Explicit intents move between screens and pass record IDs. An implicit intent shares expense summaries through other apps.

## Sensors

The accelerometer detects a deliberate shake on the Dashboard and refreshes it. The camera is used to capture meter displays and bills, but it is a feature, not the sensor requirement.

## Storage

A local SQLite database stores readings, tariffs, bills and expense splits. Images are saved in app storage, and their file paths are linked to records.

## Tech

- Kotlin
- Jetpack Compose
- Minimum SDK 26

## Milestones

- **Milestone 1:** four screens, user interaction, explicit and implicit intents.
- **Milestone 2:** accelerometer shake to refresh and camera capture.
- **Milestone 3:** SQLite storage, full CRUD and performance optimisation.

## Author

Timothy Sisa
