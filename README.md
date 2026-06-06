# NpcSkinUpdater

A lightweight Spigot plugin to dynamically update NPC skins based on PlaceholderAPI placeholders. Works perfectly with Citizens, FancyNpcs, or any other NPC plugin that supports command-line skin updates.

## Features
- **Highly Configurable**: Change update intervals, target NPCs, and source placeholders.
- **Universal Command Template**: Define any skin command format (e.g. `npc skin {npc} {player}`).
- **Reload Command**: Reload configuration instantly in-game.
- **Update Checker**: Automatically notifies console and administrators when a new version is available on SpigotMC.
- **Lightweight**: Optimized scheduler to ensure zero server lag.

## Installation
1. Download **NpcSkinUpdater-1.0.0.jar**.
2. Place it into your server's `plugins` folder.
3. Ensure you have **PlaceholderAPI** and an NPC plugin installed (e.g. Citizens, FancyNpcs).
4. Restart your server.
5. Configure the generated `config.yml` inside the `plugins/NpcSkinUpdater` folder.
6. Run `/nsu reload` to apply the changes.

## Configuration (`config.yml`)
```yaml
# Update interval in seconds (default is 300 seconds / 5 minutes)
update-interval: 300

# Enable console messages when an NPC skin is successfully updated
log-updates: true

# The command format to run when updating the skin.
# Placeholders:
# {npc} - The name of the NPC from the list below
# {player} - The player name resolved from the placeholder
update-command: "npc skin {npc} {player}"

# List of NPCs and their associated placeholders.
npcs:
  skyblock1: "%Level_bskyblock_top_name_1%"
  skyblock2: "%Level_bskyblock_top_name_2%"

# Update Checker Settings
update-checker:
  enabled: true
  resource-id: 0 # Replace with your actual SpigotMC Resource ID after uploading
```

## Commands & Permissions
- `/npcskinupdater reload` (Alias: `/nsu reload`) - Reloads the configuration.
- Permission: `npcskinupdater.admin` (Default: OP)

---

# NpcSkinUpdater (Türkçe)

NPC görünüşlerini (skin) PlaceholderAPI değişkenlerine göre otomatik ve dinamik olarak güncelleyen hafif bir Spigot eklentisi. Citizens, FancyNpcs veya komut satırından skin güncellemeyi destekleyen herhangi bir NPC eklentisi ile uyumludur.

## Özellikler
- **Tamamen Yapılandırılabilir:** Güncelleme sıklığını, hedef NPC'leri ve değişkenleri kendiniz belirleyin.
- **Evrensel Komut Şablonu:** Eklentinize göre skin komut formatını ayarlayın (örn. `npc skin {npc} {player}`).
- **Yeniden Yükleme Komutu:** Sunucuyu kapatıp açmadan ayarları oyun içinden yenileyin.
- **Güncelleme Kontrolü (Update Checker):** Yeni sürüm çıktığında konsolda ve yetkili oyunculara oyuna girdiğinde bildirim gönderir.
- **Performans Dostu:** Optimize edilmiş zamanlayıcısı sayesinde sunucuda gecikmeye (lag) yol açmaz.

## Kurulum
1. **NpcSkinUpdater-1.0.0.jar** dosyasını indirin.
2. Sunucunuzun `plugins` klasörüne yükleyin.
3. Sunucunuzda **PlaceholderAPI** ve bir NPC eklentisinin (Citizens, FancyNpcs vb.) kurulu olduğundan emin olun.
4. Sunucuyu başlatın.
5. `plugins/NpcSkinUpdater/config.yml` dosyasını düzenleyin.
6. Oyun içinden `/nsu reload` komutuyla ayarları yenileyin.

