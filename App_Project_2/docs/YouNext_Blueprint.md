## YouNext — Technical & Design Blueprint

### Executive Summary
YouNext is a career guidance and future opportunities platform for graduating students. It combines personalized recommendations, a skills roadmap builder, job/internship discovery, portfolio creation, and a community chat to help users plan their next steps. The Android app is built with Kotlin + Jetpack Compose, backed by Firebase (Auth, Firestore, Storage, Cloud Functions), and includes an on-device + cloud-assisted recommendation engine for career paths and learning content.

### Problem Statement
Graduating students struggle to translate academic achievements into actionable career paths. They face fragmented resources: courses on one site, jobs on another, and little personalized guidance. Advisors have limited bandwidth, and students lack feedback loops to evaluate progress.

### Proposed Solution
YouNext delivers a unified mobile experience:
- Personalized career path recommendations based on profile, skills, interests, and goal roles.
- Learning Roadmap Builder with milestones, skills, and curated content (MOOCs, articles, projects).
- Opportunity Feed aggregating jobs, internships, hackathons, and scholarships.
- Portfolio Builder to showcase projects, certifications, and achievements.
- Community: in-app chat, topic channels, expert AMAs, and peer reviews.
- Progress tracking with KPIs and nudges.

### Objectives
- Provide accurate, explainable recommendations for roles and learning steps.
- Reduce time-to-first-opportunity by 30% for new graduates.
- Increase weekly active learning sessions per user to >3.
- Enable verified portfolios that can be shared externally.
- Support institutional cohorts with analytics (Phase 2).

### Scope
In-Scope (Phase 1):
- Android app (Kotlin + Jetpack Compose)
- Firebase Auth (email/Google), Firestore data, Storage for portfolio media, Cloud Functions for ML orchestration, FCM for notifications
- Recommendation system (hybrid: rules + ML ranking)
- Roadmap Builder, Opportunity Feed, Portfolio, Community chat (channel-based), Basic moderation
- Analytics via Firebase + custom events

Out-of-Scope (Phase 1):
- iOS/web clients, employer dashboards, advanced content ingestion pipelines, proctoring

### Technology Stack
- Language/UI: Kotlin, Jetpack Compose, Material 3, Navigation Compose
- Architecture: MVVM + Repository, Kotlin Coroutines + Flow, Hilt DI
- Data: Firebase Auth, Firestore, Storage, Remote Config, Cloud Functions (Node.js/TypeScript)
- ML: On-device TensorFlow Lite for light ranking; Cloud Functions for heavier feature engineering and model inference (fallback)
- Messaging: Firestore subcollections for channels/messages; FCM for push
- Analytics: Firebase Analytics + Performance + Crashlytics
- Testing: JUnit5, MockK, Turbine, Paparazzi/Screenshot testing, Firebase Test Lab
- DevOps: Gradle, Version Catalog, GitHub Actions (CI), Play Console (internal testing)

### High-Level Architecture
Presentation (Compose) → ViewModel (State + Actions) → UseCases → Repositories → Firebase SDKs / Cloud Functions → Firestore/Storage. Recommendation engine exposes a Cloud Function endpoint with an on-device fallback model embedded via TFLite.

### Data Model (Firestore)
Collections and key fields:
- users/{userId}
  - profile: name, email, university, graduationYear, interests[], skills[] (with proficiency), goals[], resumeUrl, portfolioVisibility
- users/{userId}/roadmaps/{roadmapId}
  - title, roleTarget, milestones[{id, skill, contentRef, dueDate, status}]
- opportunities/{opportunityId}
  - type(job|internship|hackathon|scholarship), title, company, tags[], location, mode, applyUrl, createdAt
- users/{userId}/recommendations/{recId}
  - type(career|course|opportunity|project), payload, score, explanation, createdAt
- portfolios/{userId}
  - summary, links[], projects[{title, desc, skills[], mediaUrl}], certifications[]
- communities/{communityId}
  - name, topics[], moderationRules
- communities/{communityId}/channels/{channelId}
  - title, topicTags[]
- communities/{communityId}/channels/{channelId}/messages/{messageId}
  - authorId, text, attachments[], createdAt, flagged

Storage:
- user-media/{userId}/portfolio/*
- resumes/{userId}/resume.pdf

Remote Config:
- recommendation.weights, feed.filters, feature.toggles

### Security Rules (sketch)
Authentication required for most reads; users can only update their own profile, portfolio, and roadmaps. Moderators can flag/remove messages.

### Recommendation System
- Features: user skills, interests, academic domain, interaction history (click/apply/save), roadmap progress, market trends (tag frequencies).
- Models:
  - On-device: lightweight ranking model (TFLite) for top-N re-ranking.
  - Cloud Function: candidate generation + scoring (Matrix factorization or Two-Tower with embeddings). Explanations from top contributing features.
- Workflow:
  1) Client requests recommendations with context (userId, page, type).
  2) Cloud Function generates candidates from Firestore using tags and collaborative filters.
  3) Optional on-device re-rank for latency and personalization.
  4) Store served list in users/{userId}/recommendations for auditing.

### UI/UX Design
Design language aligns with the provided homepage reference: clean header with brand, prominent CTA (Register/Sign In), and a side drawer: Home, Find Opportunities, Post Application, Profile, Log Out. Material 3 components, large cards, and iconography for trust metrics.

Primary Screens:
1. Onboarding & Auth
   - Welcome, Google Sign-In, profile basics (interests, skills seed)
2. Home Dashboard
   - Hero banner, quick actions: Login/Sign Up or Continue Learning; metrics tiles; recommendation carousels (Careers, Courses, Opportunities)
3. Find Opportunities
   - Filter by role, tags, remote/on-site; save/apply; explain-why
4. Roadmap Builder
   - Create role-based roadmap, milestones with due dates, progress ring, nudges
5. Portfolio
   - Projects, certifications, links; export/share profile URL
6. Community
   - Channel list, message threads, reactions, attachments; report/flag
7. Profile
   - Skills matrix (self-rated + inferred), resume upload, preferences
8. Settings/Notifications

Accessibility: large touch targets, content descriptions, dynamic font sizes, high contrast mode.

### Android App Structure (Modules & Packages)
Single-module (Phase 1) with internal feature packages; ready to extract into feature modules later.

Package root: `com.younext.app`

core/
- design/ (theme, typography, color, dims)
- ui/ (common Compose components, loading, error, app bars, cards)
- data/ (Firestore models, mappers)
- network/ (Cloud Functions client)
- analytics/
- auth/

features/
- home/
- opportunities/
- roadmap/
- portfolio/
- community/
- profile/
- settings/

common/
- util/ (date, formats, validators)
- di/ (Hilt modules)
- recommendation/ (on-device TFLite wrapper)

navigation/
- AppNavGraph.kt (Navigation Compose graph and routes)

### Key Kotlin Files (suggested)
- MainActivity.kt — hosts `YouNextApp()` with `NavHost` and `ModalNavigationDrawer`.
- YouNextApp.kt — sets MaterialTheme and top-level navigation.
- core/design/Theme.kt — Material3 theme with brand colors.
- navigation/AppNavGraph.kt — sealed routes: Home, Opportunities, Roadmap, Portfolio, Community, Profile, Settings.
- core/auth/AuthRepository.kt — FirebaseAuth, Google sign-in.
- core/data/UserRepository.kt — Firestore `users` CRUD, converters.
- core/data/OpportunityRepository.kt — Firestore `opportunities` queries with filters, paging.
- common/recommendation/OnDeviceRanker.kt — loads TFLite model and scores.
- features/home/HomeViewModel.kt — dashboard state (carousels, metrics).
- features/opportunities/OpportunitiesViewModel.kt — search/filter.
- features/roadmap/RoadmapViewModel.kt — CRUD milestones, progress.
- features/portfolio/PortfolioViewModel.kt — media upload to Storage.
- features/community/ChatService.kt — channels/messages backed by Firestore; FCM subscriptions.
- core/network/FunctionsClient.kt — callable functions (e.g., `getRecommendations`).

### Navigation Routes (sealed)
`Home`, `Opportunities`, `Roadmap`, `Portfolio`, `Community`, `Profile`, `Settings`, `Auth`.

### Cloud Functions (TypeScript) — Endpoints
- getRecommendations(userId, type, limit, context)
- logInteraction(userId, itemId, type, action)
- moderateMessage(communityId, channelId, messageId)
- ingestOpportunities(batch)

### Analytics Events
- app_open, sign_in, view_item, save_item, apply_item, roadmap_milestone_complete, share_portfolio, message_send, rec_click with parameters {type, id, score}.

### Offline & Performance
- Firestore local cache enabled; view models expose cached + network flows.
- Image lazy loading, thumbnails in Storage.
- Remote Config for feature flags and ranking weights.

### Security & Privacy
- Firebase Auth email/Google; device encryption for local prefs.
- Firestore rules enforcing user-level read/write, moderator roles.
- Message toxicity filter (basic) via Cloud Functions before write acknowledgment (Phase 1.5 async moderation).

### Testing Strategy
- Unit tests for repositories and use cases with Fake Firestore emulator.
- UI tests with Compose Test; snapshot tests with Paparazzi.
- Integration on Firebase Test Lab for device coverage.

### Release Strategy
- Internal testing → Closed testing (campus cohorts) → Open beta.
- Staged rollout with monitoring of Crashlytics and KPIs.

### Timeline (12 Weeks)
Weeks 1–2: Foundations — Auth, theme, navigation, data models, Firestore rules
Weeks 3–5: Features — Home, Opportunities, Roadmap CRUD, Portfolio basics
Weeks 6–7: Community chat MVP, notifications, moderation basics
Weeks 8–9: Recommendations v1 (cloud), on-device re-rank, analytics
Week 10: Hardening — QA, accessibility, offline polish
Week 11: Beta — internal + campus cohort
Week 12: Play Store prep, marketing assets, rollout

### Team & Roles
- Product Manager — scope, roadmap, stakeholder feedback
- Android Engineer(s) — Kotlin + Compose, architecture, integrations
- Backend/ML Engineer — Cloud Functions, data pipelines, models
- Designer — UX flows, components, brand
- QA — test plans, Test Lab, beta feedback

### KPIs
- Weekly Active Users (WAU), D1/D7 retention
- Avg. learning sessions/user/week; roadmap milestone completion rate
- CTR on recommendations; apply/save conversion
- Portfolio shares and external views
- Community messages/day, flagged message rate
- Crash-free sessions > 99.5%, ANR rate < 0.3%

### Initial Backlog (Phase 1)
- Implement Auth (Email + Google) and profile creation flow
- Build navigation + drawer matching reference layout
- Home dashboard with recommendation carousels (stub data)
- Opportunities search with Firestore composite indexes
- Roadmap CRUD + progress visualization
- Portfolio editor with Storage uploads
- Community channels + messages + push notifications
- Recommendation function (stub → v1)
- Analytics events wiring

### Android Directory Skeleton (suggested)
```
app/src/main/java/com/younext/app/
  MainActivity.kt
  YouNextApp.kt
  navigation/AppNavGraph.kt
  core/
    design/
    ui/
    data/
    auth/
    network/
    analytics/
  common/
    di/
    recommendation/
    util/
  features/
    home/
    opportunities/
    roadmap/
    portfolio/
    community/
    profile/
    settings/
```

---
This blueprint is intended to function as both technical documentation and a starting plan for implementation. It is sized for a 12-week MVP with room for future modularization and multi-platform expansion.


