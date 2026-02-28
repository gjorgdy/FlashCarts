<center>
    <h1>TO-DO: Add banner image</h1>
  Minecarts are arguably the best transport in Minecraft, but they can be better
</center>

<br>

## About

The primary goal of this mod is to make minecarts better without breaking your Redstone.

### New Minecart Physics

To make Minecarts faster, Flash Carts uses Minecrafts experimental physics. <br>
These experimental physics allow for a lot higher speeds, but do often break redstone contraptions.
Because of this, the mod only applies the new physics where you would benefit from the higher speeds.

By default, the new physics are applied to normal and TNT minecarts, but not to chest, furnace, or hopper minecarts.

### Building tools

To make building of minecart tracks easier, Flash Carts adds selection placing inspired by the Create mod.

To start, right-click a rail block with a rail in your hand, and a preview of the rail will appear in front of you. You can then place the rail by right-clicking again.

> For people who want a more vanilla experience, Flash Carts also adds an 'extend' build mode which can be enabled in the config.

### Speed HUD

To make sure you use the new maximum speeds to their full potential, a speedometer and speedbar show up when you are in a minecart.

### Station Titles

To make Minecarts more convenient to create transport networks, a station can show its name when you are standing still on top of it. 

To set a station's name, place a sign under the block where a Minecart stands still, and write the station name on the first line of the sign.
Any extra lines on the sign will be combined into the subtitle under the station name.

### Minecart 'Crashes'

To deal with people leaving minecarts hanging around, a minecart with a passenger will cause colliding empty minecarts to break without affecting its own speed.

<br>

## Configuration

On its own, the mod will not create a config file.
To change settings, you can install [Fzzy Config](https://modrinth.com/mod/fzzy-config).

To load changes to the config file, you can use the vanilla ``/reload`` command.

```toml
cheaperRecipes = true
# Whether to show the speedometer when in minecart (current speed in blocks per second), vanilla: false
showSpeedometer = true
# Whether to show the speed bar when in minecart (bars up to max speed), vanilla: false
showSpeedBar = true
# Whether to show the station title (title when standing still at a block with a sign under it), vanilla: false
showStationTitle = true
# Percentage of boost a powered rail should give, vanilla: 0.06 (6%)
poweredRailBoostPercentage = 0.06
# How slow a minecart has to be, to be considered halted, vanilla: 0.03
haltSpeedThreshold = 0.03
# Multiplier applied to speed when minecart is considered halted, vanilla: 0.5
haltSpeedMultiplier = 0.5
...
```