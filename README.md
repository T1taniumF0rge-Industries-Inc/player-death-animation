<!-- Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.) -->
# Player Death Animation

Fabric **1.20.1** mod that adds stylized death transitions.

## Features
- Custom local-player death transition: instant black flash + 3-second fade before vanilla death screen.
- Custom observer animation for other players: freeze, kneel, fall, and final spread pose.
- Animation registry architecture so additional death animations can be added easily.
- Config screen integrated with **Mod Menu**.
- Client commands:
  - `/pda`
  - `/playerdeathanimation`

## Config
Configuration is stored in:
- `config/pda-config.toml`

Current options:
- `enable_self_death_effect`
- `enable_observer_animation`
- `observer_animation`
