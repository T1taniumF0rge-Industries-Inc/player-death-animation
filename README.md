<!-- Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.) -->
# Finishers

Fabric **1.20.1** mod that adds stylized player death finishers.

## Features
- Death finisher animations now apply to all dead players, including your own player model in 3rd person.
- Available finisher animations: `Knee Fall`, `Lightning`, `Frozen`, `Random`, and `Off`.
- Finisher VFX:
  - **Lightning** spawns multiple visual-only strike flashes over the body.
  - **Frozen** spawns a packed-ice style cube effect around the body.
- Per-finisher sound behavior:
  - `Off`
  - `Match Finisher`
  - `Custom` (pick custom sound id)
  - Explicit finisher sound choice (`knee_fall`, `lightning`, `frozen`)
- Config screen integrated with **Mod Menu**.
- Client commands:
  - `/finishersConfig`

## Config
Configuration is stored in:
- `config/finishers-config.toml`

Current options:
- `play_finishers`
- `finisher_animation`
- `finisher_sound`
- `custom_finisher_sound`

Runtime remember-state keys used for restoring settings after toggling `play_finishers` back on:
- `last_finisher_animation`
- `last_finisher_sound`
