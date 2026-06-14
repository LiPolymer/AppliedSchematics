# AppliedSchematics

[![GitHub](https://img.shields.io/badge/GitHub-%23121011.svg?logo=github&logoColor=white)](https://github.com/LiPolymer/AppliedSchematics)
[![GitLab](https://img.shields.io/badge/GitLab-FC6D26?logo=gitlab&logoColor=fff)](https://gitlab.com/LiPolymer/AppliedSchematics)

A Minecraft mod that bridges [Create](https://modrinth.com/mod/create)'s Schematicannon with [Applied Energistics 2](https://modrinth.com/mod/ae2)'s ME network, allowing the Schematicannon to pull building materials directly from your ME system.

Adapted from [Beyond Dimensions](https://modrinth.com/mod/beyonddimensions)'s Create integration.

## Features

- **ME Schematicannon Pathway** - A new block that acts as an intermediary between the Schematicannon and the ME network.
- Place the pathway block adjacent to a Schematicannon and connect it to your ME network with cables.
- The pathway automatically reads the Schematicannon's material checklist and exposes the required items from ME storage.
- Items are extracted from the ME network on demand as the Schematicannon operates - no manual restocking needed.
- Supports multiple adjacent Schematicanons simultaneously.
- The item appears in the AE2 creative tab.

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1+ |
| Applied Energistics 2 | 19.2.17+ |
| Create | 6.0.10+ |
| Kotlin for Forge | 5.3.0+ |

## License

This project is licensed under the [AGPLv3](LICENSE).


## Acknowledge

Thanks to [Re_Construction](https://github.com/ReConstruction-127) for his amazing texture