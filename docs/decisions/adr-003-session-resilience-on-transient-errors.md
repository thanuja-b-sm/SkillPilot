# ADR-003: Session Resilience on Transient API Failures

## Status
Accepted

## Context
In earlier iterations, any network fetch exception during session restoration (`initializeSession`) executed `localStorage.removeItem('skillpilot_token')`, destroying valid user credentials during backend cold starts or temporary connection drops.

## Decision
1. **Explicit Status Classification:**
   - **401 Unauthorized:** Invalid/expired token -> clear token, reset session, redirect to `/login`.
   - **403 Forbidden:** Valid authentication, forbidden route -> retain token and session.
   - **5xx / Network Error / Timeout:** Transient failure -> retain token in `localStorage`, enter recoverable loading state, execute bounded retry loop (`ensureMasterDataLoaded`).
2. **Atomic Session Initialization:** Single `loginWithAuthData()` helper method handles token storage, profile setup, role resolution, route restoration, and data loading atomically.

## Consequences
- **Positive:** Users are never logged out due to server restarts or temporary connectivity issues.
- **Positive:** Eliminates double-login workarounds during backend cold starts.
