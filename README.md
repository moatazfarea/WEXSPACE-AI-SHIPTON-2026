# WEXSPACE AI — Shipaton 2026

WEXSPACE AI is a human-governed AI execution workspace for durable, resumable, evidence-linked work across specialist projects.

This public repository is the judge-facing Shipaton 2026 / Next Gen export. The commercial WEXSPACE product remains in a separate private canonical repository.

## What is included
- Android application source
- RevenueCat Test Store / Galaxy integration path
- Deterministic engineering and governance components
- Public website source
- Submission icon and screenshot
- Security and third-party dependency notices

## Android build
```bash
cd android
./gradlew test assembleDebug
```

## RevenueCat
The app implements RevenueCat SDK 10.15.1 paths for offering fetch, purchase, CustomerInfo refresh, restore purchases, and the `pro` entitlement. Runtime purchase success is claimed only when verified against the Test Store.

## Submission
RevenueCat Shipaton 2026 — Next Gen.

## License
MIT. Third-party components remain governed by their respective licenses/notices.
