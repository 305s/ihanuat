## JOIN DISCORD FOR SUPPORT: https://discord.gg/BZxsfAcYYW

Feature List:

- Improved wardrobe swap (adds visitor wardrobe, much less delays)
- Equipment swap
- Rod swap (optional)
- Auto-unstash
- Auto George (sells Slug and Rat pets in inventory)
- Auto Book combine
- AOTV to roof
- Support for farms that start with /plottp
- Custom NPC sell
- Junk dropper
- Automatic farming tool swap when accepting visitor offers (grants bonus farming XP)
- HUD overlay showing macro state, session/lifetime timers, next rest, farming fortune, pest chance, and pests alive count
- Farming fortune display pulled live from tab list (shows combined base + crop fortune)
- Pest chance and pests-alive rows in HUD (pest count color-coded yellow/red based on threshold)
- Reset session and/or lifetime running-time counters from the config screen

Use O to open the config and K to start the macro (change in controls accordingly)  
before starting, ensure the auto start pest, auto start visitor, and wardrobe swap is off in taunahi settings. Use taunahi's rewarper.

enable visitors, wardrobe swap, equipment swap according to your needs, all can be toggled on or off  
set the start and end of the farm using the button in config while standing on that block in the config.  

change the auto direction and should work on most farms, use .ez-listfarms to find the right command.

<img width="1273" height="716" alt="image" src="https://raw.githubusercontent.com/mizly/ihanuat/refs/heads/main/screenshot.png" />
dynamic rest works by setting the scripting time and break time, with the offset offsetting that scripting/break time by plus or minus the offset minutes  
dynamic rest is always on, can disable by setting scripting time very high  

### HUD

The status HUD panel is draggable and resizable:
- **Drag** to reposition
- **Ctrl+Drag** to resize (0.5× – 2.5×)
- HUD is editable when an inventory screen is open

Rows shown (some conditional on tab-list data):
| Row | Description |
|-----|-------------|
| macro state | Current state color-coded (farming=green, cleaning=orange, visitor=cyan, etc.) |
| current session | Time macro has been running this session |
| lifetime session | Total accumulated macro run time (persisted across restarts) |
| next rest | Time until the next dynamic rest break |
| farming fortune | Combined farming + crop fortune from tab list (e.g. `☘1,801`) |
| pest chance | Pest infestation chance % from tab list |
| pests alive | Number of active pests (shown only when > 0; yellow below threshold, red at/above) |
