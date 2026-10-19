# Demo video checklist (record on a real phone, with voice-over)

Record the phone screen (Android's built-in screen recorder) and narrate. Practise once first.

**Before recording**
- [ ] Install the debug APK on the phone and confirm it logs in.
- [ ] Open the Azure App Service once so it is awake.
- [ ] Have MySQL Workbench open and connected to the Azure database, the Azure portal and the Google Cloud console signed in (hide passwords and keys).

**In the app**
1. [ ] Home screen, then **Register** a new account (show a validation error first, e.g. a weak password).
2. [ ] Try to log in: "pending administrator approval".
3. [ ] Log in as **admin@brightstart.org** and approve the new user in Manage Admin Users.
4. [ ] Log in with the new account, then log out.
5. [ ] **Continue with Google** sign-in.
6. [ ] Dashboard: counts, chart, recent items, pull to refresh.
7. [ ] For each of Students, Volunteers, Programs, Sessions, Enrolments, Attendance, Performance and Donations: **add**, **edit** (where available) and **delete** a record.
8. [ ] Show **invalid input** being rejected without a crash (empty name, bad phone, score 150, end date before start date).
9. [ ] Reports screen.
10. [ ] **Settings:** change name/username, change password, log out and log in with the new password.
11. [ ] **Help & Support** screen.

**Online services (show the data is stored online)**
- [ ] **Azure API/authentication:** Azure portal -> App Service overview and Environment variables (hide values); open `https://<app>.azurewebsites.net/api/get_students.php` in a browser: "Authentication required".
- [ ] **Database:** MySQL Workbench -> table row counts and the `administrator` table with bcrypt `PasswordHash` values; show the record you just added in the app.
- [ ] **SSO:** Google Cloud console -> OAuth clients.

**DevOps evidence (a short closing section)**
- [ ] GitHub repo and a green GitHub Actions run (tests and build).
- [ ] Azure DevOps board.
