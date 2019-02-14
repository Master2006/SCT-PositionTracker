# SCT-PositionTracker

This is a tracker addon for [SimpleCompass](https://www.spigotmc.org/resources/simplecompass.63140/).
It allows to track static positions.

## How to install

- Drop the [jar file](https://github.com/arboriginal/SCT-PositionTracker/releases) into your `plugins/SimpleCompass/trackers` folder
- Restart your server.

## Configuration

Edit the file `plugins/SimpleCompass/trackers/PositionTracker.yml` (automatically created the first time the tracker is loaded).

Read [settings.yml](https://github.com/arboriginal/SCT-PositionTracker/blob/master/src/settings.yml) to have a look on available parameters.

## Permissions

- To use this tracker, players must have:
    - **scompass.use**
    - **scompass.track**
    - **scompass.track.POSITION** (or **scompass.track.***)
- To be able to add or remove positions, players must also have:
    - **scompass.track.POSITION.`<manage>`**
- All named positions in `PositionTracker.yml` use dynamic permissions:
    - **scompass.track.POSITION.defined.`<name>`**
    - **scompass.track.POSITION.defined.spawn** for example to track the position "spawn"
- To have access to all named positions (without **scompass.track.POSITION.defined.<name>** for each), players need:
    - **scompass.track.POSITION.defined.***
