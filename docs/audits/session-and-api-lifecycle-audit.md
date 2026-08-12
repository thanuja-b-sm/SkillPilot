# Session & API Lifecycle Audit Report

**Date:** August 12, 2026  
**Scope:** Session restoration, `/api/auth/me` DTO serialization, cold-start startup race conditions, browser refresh route authority, and HTTP error semantics.

---

## 🔍 Findings & Root Causes

### 1. Admin Refresh Route Bug (`/admin`)
- **Root Cause:** `/api/auth/me` returned `UserProfileResponse` Java DTO which lacked `userRole` and `role` properties. On page refresh, `profile.userRole` was `undefined`, causing role downgrade to student and redirection to `/results`.
- **Solution:** Added `userRole` and `role` to `UserProfileResponse.java` and mapped them in `UserProfileMapper.java`. Updated `AppContext.tsx` to inspect returned role and preserve `/admin` route.

### 2. Cold-Start Backend Startup Race Bug
- **Root Cause:** `initializeSession` purged stored JWT tokens from `localStorage` whenever initial network fetch failed (e.g. backend starting up). Master data fetched only once on mount and failed silently if backend was delayed.
- **Solution:** Differentiated between 401 Unauthorized (clear token) and transient network/server errors (retain token). Implemented `ensureMasterDataLoaded()` with 3-attempt bounded retry loop. Created atomic `loginWithAuthData` helper.

---

## ✅ Verification Results

- `npx tsc --noEmit`: 0 errors.
- `npm run build`: Success.
- `.\mvnw.cmd test`: All backend tests passed (`BUILD SUCCESS`).
- `SessionLifecycleAuditTest`: Added 5 unit tests verifying role serialization, 401, 403, and 200 responses.
