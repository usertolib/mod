# SMPTweaks (Fabric, Minecraft 1.21.11)

Paper plugin SMPTweaks'ning Fabric varianti.

## Nima qiladi
- Golden apple: oltin ingot "+" shaklida olma atrofida (4 gold ingot + 1 olma)
- Netherite ingot: 2 netherite scrap + 2 gold ingot (shapeless)
- Ender pearl cooldown: 60s
- Mace: enchant stoli va anvilda enchant taqiqlangan; smash cooldown 15s
- Speed / Strength / Fire Resistance potionlari: 8:00 (splash = 0.75x)

## Yig'ish
JDK 21 kerak.

    chmod +x gradlew
    ./gradlew build

Tayyor fayl: `build/libs/SMPTweaks-fabric-1.0.0.jar` (`-sources` faylini olmang).

## O'rnatish
Server `mods/` papkasiga:
1. `SMPTweaks-fabric-1.0.0.jar`
2. Fabric API (0.141.6+1.21.11 yoki yangiroq 1.21.11 uchun)

Birinchi ishga tushganda `config/smptweaks.properties` yaratiladi.
