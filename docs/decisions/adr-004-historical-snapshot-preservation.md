# ADR-004: Historical Snapshot Preservation

## Status
Accepted

## Context
When administrators modify career requirements or system scoring weights in the live database, past user career match evaluations must remain understandable and audit-compliant.

## Decision
When `CareerDiscoveryService` calculates career matches for a student, it serializes the exact `configSnapshot` (system scoring weights) and `requirementsSnapshot` (career skill requirement levels) at that point in time and persists them into `CareerMatchResult`.

Future administrative changes to career skill requirements or system weights affect future evaluations but **NEVER** overwrite existing historical snapshot fields in `CareerMatchResult`.

## Consequences
- **Positive:** Guarantees historical audit integrity for past career assessments.
- **Positive:** Allows admins to safely evolve career requirements over time without corrupting past match data.
