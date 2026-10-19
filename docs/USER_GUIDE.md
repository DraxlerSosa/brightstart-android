# BrightStart - User Guide

BrightStart helps administrators, coordinators and volunteers manage students, programs, sessions, attendance, performance and donations from an Android phone.

## 1. Getting started
**Install:** copy `app-debug.apk` to your phone, open it and allow installation from this source. Open **BrightStart**. You need an internet connection.

**Create an account**
1. On the Home screen tap **Register**.
2. Enter your full name, email, a username (3-30 letters, numbers, `.` or `_`) and a password (at least 8 characters with a letter and a number), and choose your role.
3. Tap **Register**. A BrightStart administrator must **approve** your account before you can log in.

**Log in**
- Enter your email and password and tap **Login**, or tap **Continue with Google**.
- Wrong details show a message such as "Invalid email or password". An account that is not approved yet shows "pending administrator approval".

## 2. The Dashboard
After logging in you land on the Dashboard: totals for students, volunteers, programs, sessions, enrolments and donations, a chart, recent activity and shortcuts to every section. Pull down to refresh. The bottom bar gives quick access to Home, Dashboard, Students, Donations and Reports.

## 3. Managing records
Students, Volunteers, Programs, Sessions, Enrolments, Attendance, Performance and Donations all work the same way:
- Tap **+** to add, the **pencil** to edit (where available), the **bin** to delete (you are asked to confirm).
- Forms check your input. If something is wrong, a message explains what to fix and the form stays open so you do not lose what you typed.
- Dates and times are chosen with pickers; phone numbers must look like `0821234567`.

| Section | Notes |
|---|---|
| Students | Name, date of birth (must be in the past), gender, community, contact number, enrolment date |
| Volunteers | Name, email, phone, subject speciality, join date, status |
| Programs | Name, description, start and end dates (end cannot be before start), status |
| Sessions | Program, volunteer, date, start and end time (end after start), venue |
| Enrolments | Student, program, date, status |
| Attendance | Session, student, Present / Absent / Excused / Late, notes |
| Performance | Student, program, date, score 0-100, grade, comments |
| Donations | Donor, contact (email or phone), amount above 0, date, type, notes |

## 4. Roles
| Role | Can do |
|---|---|
| Volunteer, Management | View and add/edit records |
| Coordinator | Everything above, plus delete records |
| Admin | Everything, plus approve/reject users and change roles (**Manage Admin Users**) |

The server enforces these rules. If you try something you are not allowed to do you will see "You do not have permission to do that".

## 5. Settings
Dashboard -> **Settings**:
- **Profile:** change your name and username, then **Save profile**.
- **Change password:** enter your current password and a new one twice. Your other devices are signed out.
- **About:** app version and the server the app talks to.
- **Log out:** ends your session on this phone and on the server.

## 6. Help
Dashboard -> **Help & Support** answers common questions inside the app.

## 7. Troubleshooting
| Problem | What to do |
|---|---|
| "Could not reach the server" | Check your internet connection and try again |
| "Your session has expired" | Log in again (sessions last 7 days or until you change your password) |
| "Invalid email or password" | Re-enter your details; ask an administrator if you forgot your password |
| "Pending administrator approval" | Wait for an Admin to approve your account |
| Lists are empty | Pull down to refresh; check your connection |
| Google sign-in fails | Make sure a Google account is added on the phone; see `docs/GOOGLE_SIGNIN_SETUP.md` |
