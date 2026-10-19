# Azure DevOps (evidence for the DevOps life cycle)

Task 2 requires Azure DevOps to be used throughout. Suggested set-up for the Android app:

1. **Project:** https://dev.azure.com -> New project `BrightStart` (private).
2. **Boards:** create an *Epic* "BrightStart Android app" and one *User Story* or *Task* per feature, each assigned to the member who owns it. Move items To Do -> Doing -> Done as you work and keep dated screenshots.
   - Suggested items: token login and registration, Google SSO, Settings screen, Help screen, input validation, UI refresh, unit tests, GitHub Actions workflow, user guide, demo video.
3. **Repos (choose one):**
   - *Import* the GitHub repo into Azure Repos (Repos -> Import repository), or
   - *Mirror* it: `git remote add azure <azure-repo-url>` then `git push azure main` after each milestone.
4. **Pipelines (optional):** `azure-pipelines.yml` in this repo builds and tests the app. New Azure DevOps organisations may need a free parallelism grant (https://aka.ms/azpipelines-parallelism-request) before Microsoft-hosted agents run. GitHub Actions (`.github/workflows/android.yml`) already runs the same steps.
5. **Evidence to screenshot:** the board with items moving to Done, the commit history, a green pipeline or Actions run, and meeting notes linked to work items.

| DevOps phase | Where it shows |
|---|---|
| Plan | Azure Boards work items, meeting notes |
| Code | Commits in GitHub / Azure Repos |
| Build | `./gradlew assembleDebug` (GitHub Actions) |
| Test | `./gradlew testDebugUnitTest`, test report artifact |
| Release | Tagged version, APK artifact |
| Deploy | API on Azure App Service, app installed on a real phone |
| Operate / Monitor | Azure portal metrics and logs, Boards feedback items |
