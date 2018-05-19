# Software Engineering Summit - Android Workshop
Welcome to the code repo for the Android workshop for Capital One's Software Engineering Summit 2018.

In this workshop, you will learn a basic Android app featuring a login screen which
leads to an "account summary" screen which displays the user's name and the recent transactions
for one of their cards.

<img src="screenshots/login.png" width="400"> . <img src="screenshots/summary.png" width="400">

The resulting app will feature the following:
- A two Activity app, with data passed from the first to the second.
- UI layout using [ConstraintLayout](https://developer.android.com/training/constraint-layout/)
- Local data storage for user credentials using [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences)
- Rendering a list using a [RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- Real networking & JSON parsing for login
