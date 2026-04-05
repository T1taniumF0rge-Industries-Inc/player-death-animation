<!-- Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.) -->
# Finishers

Fabric **1.20.1** mod that adds stylized death transitions.

## Features
- Custom local-player death transition: instant black flash + 3-second fade before vanilla death screen.
- Custom observer animation for other players: freeze, kneel, fall, and final spread pose.
- Animation registry architecture so additional death animations can be added easily.
- Config screen integrated with **Mod Menu**.
- Client commands:
  - `/finishersConfig`

## Config
Configuration is stored in:
- `config/finishers-config.toml`

Current options:
- `enable_self_black_screen_death_effect`
- `enable_finishers`
- `finisher_type`
