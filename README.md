# 欢迎您查看~喵~(Nov 19 2018 15:57)
# Convarter
An android app that allows you to edit Minecraft (MCPE) gamemaps with javascripts, create customized flat levels and other features.

Now supports gamemap editing using BlockLauncher-style javascripts.
* `getTile(x,y,z)` returns block id.
* `getData(x,y,z)` returns block data.
* `setTile(x,y,z,id)` sets the block's id.
* `setTile(x,y,z,id,data)` sets the block's id and data.
* `setData(x,y,z,data)` sets the block's data.

With this app you can generate huge and complex things in Minecraft that are hard
or impossible to be build manually or using in-game scripts.  
This app has a previous version which was made several years ago that already
made popular game maps uploaded to MCPE Master.

## Ideas
* Place sand, torch and cactus in space...
* 256x256x256 3D maze...
* Music railway with super long musics...
* Huge structures...
* Convert HDL files into redstone circuits...
* Parse an obj 3d file and... 
* Well, everything up to you... 

## Features that may be added
* Create customized flat levels, e.g. empty, infinite ocean.
* Convert an existing game map's flat layers, get rid of the existing grass ground.
* Visulized copy|cut|paste in 2d|3d view.
* Convert a game map back to 0.x format.
* Make portals invisible and single-directioned.
* Conversion between PC maps and PE maps.

## Build & Install
* If you want this app's apk open an issue.
* You could clone and Build with Android Studio.
* This repo works as a backup, it may or may not be able to be opened on your PC.

## Library projects used
* F43nd1r's [Rhino-android](https://github.com/F43nd1r/rhino-android) to enable script optimization.
* Mozilla's [Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino), a javascript
environment.
* [SimpleNBT](https://github.com/boomshroom/SimpleNBT), read|write nbt files e.g. level.dat.
Originally [SpoutDev](https://www.google.com/search?&q=spout.org)'s work, forked by
Boomshroom, since spout was gone.
* Google's [Dx tool](https://android.googlesource.com/platform/dalvik). Here we uses
a repacked version:`'com.jakewharton.android.repackaged:dalvik-dx:7.1.0_r7'`
* [leveldb-mcpe-android](https://github.com/oO0oO0oO0o0o00/leveldb-mcpe-android) thanks to
[@litl](https://github.com/litl/android-leveldb)'s,
[@mojang](https://github.com/Mojang/leveldb-mcpe)'s and
[@google](https://github.com/google/leveldb)'s work.
* Inspired by reverse engineering [@zhuowei](https://github.com/zhuowei)'s
apps... To be honest. Anyway we're not copying his code in this new Convarter.
