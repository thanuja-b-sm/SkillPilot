# ADR-001: MySQL as Authoritative Source of Truth

## Status
Accepted

## Context
SkillPilot requires reliable, audit-compliant career matching, skill-gap analysis, and milestone roadmaps for student career guidance.

## Decision
The MySQL relational database (via Spring Data JPA) is the sole authoritative source of truth for all system metrics, user skills, questionnaire mappings, career requirements, match results, and roadmaps.

The React frontend UI is a presentation layer that displays backend-authoritative data. The client SPA must never compute or override authoritative scores or readiness percentages.

## Consequences
- **Positive:** Guarantees consistent career guidance across devices and reloads; protects business logic from client-side tampering.
- **Positive:** Enables complete historical snapshot auditing.
- **Negative:** Requires API round-trips for profile and skill updates.
