# ZenSteps 🧘

ZenSteps is a comprehensive wellness mobile application designed to help users build and maintain healthy daily habits while tracking their emotional well-being and hydration levels. Built with Kotlin for Android, it provides a calm and intuitive interface for personal growth and self-care.

---

## Features

### 🔐 User Authentication
- Secure login and registration system
- Guest mode for quick access without an account
- Persistent user sessions with SharedPreferences

### 📱 Habit Tracking
- Create custom daily or weekly habits with names and descriptions
- Mark habits as complete and build continuous streaks
- Visual progress tracking with pie chart statistics
- Quick habit management via add/edit dialog interface

### 😊 Mood Journaling
- Daily mood logging with emoji-based selection
- Add personal notes to each mood entry
- View mood history in list view or calendar view
- Mood trend visualization with line/bar charts
- Delete individual mood entries with confirmation

### 💧 Hydration Monitoring
- Set a personalized daily water intake goal (default: 2000ml)
- Log hydration intake in custom amounts (ml)
- Alarm-based reminders with configurable intervals
- Quiet hours support — no reminders during sleep time
- Hydration history with progress visualization via pie chart
- Boot-completed receiver to restore alarms after device restart

### 🔔 Smart Notifications & Reminders
- Exact alarm scheduling for hydration reminders
- Notification permission handling for Android 13+
- Wake lock support for reliable background alerts
- Alarms automatically restored after device reboot

### 📊 Analytics & Insights
- Mood trends chart for emotional pattern tracking
- Habit completion streaks and total habit count
- Daily progress indicator on the Dashboard
- Hydration intake tracking against daily goal

### 🏆 Achievements
- Unlock achievements based on wellness milestones
- Tracks habit streaks (e.g., 7-day habit streak)
- Tracks hydration consistency and mood logging history
- Toast notifications when achievements are unlocked

### ⚙️ Settings & Customization
- Theme selection: Default, Red, Orange, Yellow
- Light and dark mode support
- User logout and session management

### 🎨 User Interface
- Material Design 3 interface
- Bottom navigation for quick access to all sections
- Smooth onboarding flow with 3 intro screens
- ViewPager2-based onboarding with skip/next support
- Responsive layouts for various screen sizes

---

## Technical Features

| Detail | Info |
|---|---|
| Platform | Android (API 24+) |
| Language | Kotlin |
| Architecture | Fragment-based with Repository pattern |
| UI | XML Layouts + ViewBinding |
| Navigation | Jetpack Navigation Component |
| Data Storage | SharedPreferences (local, no internet needed) |
| Background Tasks | WorkManager + AlarmManager (Exact Alarms) |
| Charts | MPAndroidChart v3.1.0 |
| Permissions | Notifications, Exact Alarms, Wake Lock, Boot Completed |

---

## Getting Started

1. **Installation** — Clone the repo and open in Android Studio
2. **Onboarding** — Complete the 3-screen onboarding flow
3. **Authentication** — Register an account, log in, or continue as guest
4. **Setup** — Add your habits and set your daily hydration goal
5. **Track** — Log your habits, moods, and water intake daily
6. **Monitor** — View your progress from the Dashboard and analytics screens

```bash
git clone https://github.com/your-username/ZenSteps.git
```

Open the project in **Android Studio**, sync Gradle, and run on a device or emulator (API 24+).

---

## Privacy & Security

- All data is stored locally on your device via SharedPreferences
- No internet connection required for core functionality
- User data remains private — no third-party data sharing

---


## Contributing

This project is developed for personal wellness tracking. For suggestions or improvements, please create an issue in the repository.

---

## License

This project is for educational and personal use.
