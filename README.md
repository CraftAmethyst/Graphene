# Graphene

📖 **Overview**
---

![Forge](https://cdn.modrinth.com/data/cached_images/3983ada573d85145a6ebc665c0a71a0f37b07d9b.png)
![Fabric](https://cdn.modrinth.com/data/cached_images/f1ecd847bac87f84988c0aded9edb67e7e02343f.webp)
![quilt](https://cdn.modrinth.com/data/cached_images/763c5d457466b9b3e9746bb2c4fdccd44c47aaa5.webp)
![neoforge](https://cdn.modrinth.com/data/cached_images/46accc8dee394028c66c514817189c2023551df8.webp)
---

Graphene is a powerful, free and open-source performance mod for Minecraft 1.19.4 and above. It fixes a handful of vanilla bugs and boosts frame-rate, startup speed and server tick-time while remaining highly configurable and compatible with almost everything else in your mod folder.  
Whether you run a light client pack or a 400-mod kitchen-sink, Graphene usually “just works” and the gains are especially visible on crowded servers.

---

## 👀 What Graphene Does

### Performance
- **Distant-tile suspension** – entities and TEs far away from players stop ticking, but bullets (TaCZ), the Ender Dragon, etc. are protected from freezing.  
- **Faster client bootstrap** – shave seconds off launch.  
- **Background throttling** – optional mode that lowers FPS and chunk updates when the window is out-of-focus; instantly restores on return.  
- **Quicker world-gen** – compatible with other chunk-acceleration mods.  
- **Light-engine tweaks** – reduce lighting recalculations.

### Rendering
- **Leaf culling** (NeoForge ≤ 1.21.1 only).  
- **Static chest models** – chests are batched and skipped at long range (configurable distance).  
- **Reflex** – lowers render latency by pipelining GL calls.

### Game-play fixes & tweaks
- Boats no longer break or deal fall damage from any height (vanilla bug fix).  
- Ground-item merger – faster, configurable replacement for Stxck.  
- Bamboo light checks suppressed.  
- Language changes apply instantly; no resource-pack reload.  
- Several memory-leak patches ported from standalone mods.

---

## 🐛 Reporting Issues
Open an issue on the official Graphene repo (link at the top of this page).  
Before you post:
1. Update to the latest build.  
2. Try reproducing with default config – a few aggressive opts are off by default.  
3. Attach the crash-log or latest.log.

---

## ❓ FAQ

**Where is the newest file?**  
Head to the Modrinth project page – every approved build is there.

**Can I ship it in my pack?**  
Yes. Graphene is MIT-licensed; redistribute freely.

**Version support policy?**  
We target the platforms that need optimisation most first, then the widest-used versions, then the rest when time allows.

| MC version   | Forge | NeoForge | Fabric | Quilt | Plug-ins |
|--------------|-------|----------|--------|-------|----------|
| 1.21.6-1.21.8| plan  | full     | full   | –     | plan     |
| 1.21.3-1.21.5| –     | paused   | full   | –     | plan     |
| 1.21-1.21.1  | full  | full     | full   | full  | plan     |
| 1.20.3-1.20.6| plan* | –        | partial| partial|plan     |
| 1.20.2       | full  | –        | full   | full  | plan     |
| 1.20.1       | full  | full     | full   | full  | plan     |
| 1.20         | full  | –        | plan   | plan  | plan     |
| 1.19.4       | full  | –        | plan   | plan  | –        |
| 1.12.2       | beta  | –        | –      | –     | –        |

*1.20.4 & 1.20.6

---

## 🙋 My game crashed
Do these checks before blaming Graphene:
1. Run Graphene alone – still crash?  
2. Revert any config changes you made – fixed?  
3. Suspect a mod conflict? Load only Graphene + the suspected mod in a clean instance.

If the crash survives the steps above, open a GitHub issue with logs attached.

### ⚠️ Attention!
Both Graphene and EntityCulling use LogisticsCraft/OcclusionCulling to implement similar features, which may cause conflicts!

---

## ❓ Something feels off?
Graphene is built for the average player, not for one specific setup. Every optimisation can be toggled in `config/graphene.toml`. If a feature breaks your pack or feels wrong, just switch it off – the rest will still help.

---

Enjoy the extra frames!

> **Note:** Not an official Minecraft product.
