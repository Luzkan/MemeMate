# Changelog
All notable changes to this project will be documented in this file.

___________________________________

## [0.8.2a] - 2019-05-24 - Marcel Jerzyk
### Improved
- Further Paddings adjustments
- Readme Logo
### Fixed
- View crash bug

## [[0.8.2]](https://github.com/Luzkan/MemeMate/commit/812992ac8e3393038def2dc181504acda3366976) - 2019-05-24 - Marcel Jerzyk
### Improved
- Adjusted image constraints & size on meme browsing and meme adding fragment 
- Adjusted paddings for symmetry
- Extracted strings

## [[0.8.1]](https://github.com/Luzkan/MemeMate/commit/f02045b053ebb18b66de6a06681e349da7d1a1bb) - 2019-05-24 - Marcel Jerzyk
### Improved
- Code overhaul, junked removed or archived, removed junk and weird new lines from code, code and comments were standardized and 'beautified' (& fixed typos)
### Fixed
- Syntax, property, type and all the other warnings
- Weird navbar visual glitch (text was twitching and fading to min size)
- Found and fixed profile bug fixed after meme added
- Gathered Todo's spread all over the code into the TODO.md

## [[0.8.0c]](https://github.com/Luzkan/MemeMate/commit/e0d1094fe44bc829d2e1d44be68d3932b889a82e) - 2019-05-24 - Marcel Jerzyk
### Improved
- Instead of weird two profile fragments, now the "Matches" is just empty
### Fixed
- Swipe disabled on meme swiping fragment
	- Added: CustomViewPager to achieve that with disable/enable touches functions
	- Could be Improved: Disable Touch on MemeDrag (onCardDraggin in BrowseFragment) and Enable on MemeSwiped or MemeSwipeCanceled (onCardCanceled & onCardSwiped)
- Navbar is responsive to swipes between fragments

## [[0.8.0b]](https://github.com/Luzkan/MemeMate/commit/b0efc8f610cb70c81d9d93b4b04fffd56d5dc1f8) - 2019-05-24 - Marcel Jerzyk
### Added
- Wrote whole version history from version 0.2.1 to 0.6.4 with their authors (based on commits)
- Version Files are now clickable hyperlinks to commits

### Fixed
- Fixed fullscreen pop memes browsing
- Fixed navbar usage  

## [[0.8.0a]](https://github.com/Luzkan/MemeMate/commit/7cd9f303c2a60df9f0dadde21bd0ed675c7ab176) - 2019-05-23 - Marcel Jerzyk
### Fixed
- Adjusted navbar to fragments swipe
- Adjusted "container" in activity_main to fit on screen (end on navbar)

## [[0.8.0]](https://github.com/Luzkan/MemeMate/commit/ace7e50b531187ffae988618c8516c243ce3226c) - 2019-05-23 - Marcel Jerzyk
### Description:
This feels like it is a really "no-brain" feature that needs to be added to an app like that for the increased comfort of use and to keep "the flow" since all main features rely on swipe gestures. 
### Added
- Swipe hand gesture to move between fragments added

## [[0.6.4]](https://github.com/Luzkan/MemeMate/commit/5d602501de9fcc620b84557e506d3302234df4d3) - 2019-05-22 - Karol Szymończyk
### Added
- Swipe Animation when fragment change

## [[0.6.3]](https://github.com/Luzkan/MemeMate/commit/aeb5c10bda6f30fa3e0eebff63fd1ad075c74ca6) - 2019-05-22 - Karol Szymończyk
### Added
- Meme Panel Update

## [[0.6.2]](https://github.com/Luzkan/MemeMate/commit/32fa259f9ca929651612dd5a9bdb57f072a9fef4) - 2019-05-21 - Paweł Rubin
### Change
- Code cleanup

## [[0.6.1c]](https://github.com/Luzkan/MemeMate/commit/5a985d5c95a4811ecb06de30725b9d4e642ae958) - 2019-05-21 - Karol Szymończyk
### Added/Fixed
- onBackPressed override done

## [[0.6.1b]](https://github.com/Luzkan/MemeMate/commit/4cc36bbbb303d511ab5bda2ee406fbc0e014c60b) - 2019-05-21 - Szymon Głąb
### Fixed
- Unknown bugfix again
- Profile and Top List now downloads from firebase 

## [[0.6.1a]](https://github.com/Luzkan/MemeMate/commit/4cc36bbbb303d511ab5bda2ee406fbc0e014c60b) - 2019-05-21 - Szymon Głąb
### Fixed
- Unknown bugfix

## [[0.6.1]](https://github.com/Luzkan/MemeMate/commit/c3d0a3b6ec23e5e60cf0dea7e1e5ecf8619e30ed) - 2019-05-21 - Szymon Głąb
### Change
- Activites into Fragments

## [[0.6.0a]](https://github.com/Luzkan/MemeMate/commit/2d30f0224c72f13674df18deafb57e1f65ae1d89) - 2019-05-16 - Karol Szymończyk
### Improved
- Background Color Top Screen Added

## [[0.6.0]](https://github.com/Luzkan/MemeMate/commit/6ca50c03e3e89ea28aed7275d2de16818766b2e4) - 2019-05-16 - Karol Szymończyk
### Added
- Top Screen

## [[0.5.1]](https://github.com/Luzkan/MemeMate/commit/4fa72465ab17b24c1b8c96e559f6183b2899e5fb) - 2019-05-16 - Szymon Głąb
### Fixed
- Downloading memes from Firestore works, user can add meme and all information about meme is updated.

## [[0.5.0]](https://github.com/Luzkan/MemeMate/commit/2a93de9da5a46ff97cf04b331f0a71650b9779b5) - 2019-05-15 - Paweł Rubin
### Added
- Profile Page

## [[0.4.0]](https://github.com/Luzkan/MemeMate/commit/cb904bbae5393c1845cb8cba1cba7f2e91aa2d16) - 2019-05-15 - Paweł Rubin
### Added
- Fullscreen Meme Preview

## [[0.3.4]](https://github.com/Luzkan/MemeMate/commit/56af981538e847b464d62f79f94e6e6798d3ccd2) - 2019-05-09 - Szymon Głąb
### Added
- Dynamic Fields

## [[0.3.3a]](https://github.com/Luzkan/MemeMate/commit/806b3d71c9fe2a9a1da12c2feebc95867433a7a5) - 2019-05-09 - Karol Szymończyk
### Improved
- Facebook button added

## [[0.3.3]](https://github.com/Luzkan/MemeMate/commit/de2c817ae627665b0270f6097a4a833210257e59) - 2019-05-09 - Karol Szymończyk
### Added
- Loading Button and Soft Keyboard Handling

## [[0.3.2b]](https://github.com/Luzkan/MemeMate/commit/2476bfd36cd4de60f737b1d2873009e92130f92c) - 2019-05-09 - Szymon Głąb
### Added
- Facebook Login

## [[0.3.2a]](https://github.com/Luzkan/MemeMate/commit/057f263e15819fa984ba58c8c6690f3ec6bbcf08) - 2019-05-08 - Szymon Głąb
### Fixed
- Login & Register error handled

## [[0.3.2]](https://github.com/Luzkan/MemeMate/commit/057f263e15819fa984ba58c8c6690f3ec6bbcf08) - 2019-05-08 - Szymon Głąb
### Added
- Firebase, users are created and saved to database

## [[0.3.1]](https://github.com/Luzkan/MemeMate/commit/aef50e36c8b59e89bcf777428a111f8f9564f336) - 2019-05-08 - Karol Szymończyk
### Added
- Animations and Logo

## [[0.3.0]](https://github.com/Luzkan/MemeMate/commit/8e6dde92ec5492338355efa1810d1b7009ca8481) - 2019-05-08 - Szymon Głąb & Karol Szymończyk
### Added
 - Added sign in panel
 - Added sign up panel

## [[0.2.1]](https://github.com/Luzkan/MemeMate/commit/e7d6ee41837442c456da6a8007b442e19bf8dd11) - 2019-05-08 - Marcel Jerzyk
### Added
- Added Reload function for Meme Adapter
### Fixed
- Corrected columns for Meme in MemeDB

## [[0.2.0a]](https://github.com/Luzkan/MemeMate/commit/13be598c0f69a5c8a1a2e3710c9167d3b4e36913) - 2019-05-08 - Marcel Jerzyk
### Added
- TodoTasks file for easy project managment and clarity of work progress w/o interruptions

### Fixed
- CHANGELOG.md formatting and dates 

## [[0.2.0]](https://github.com/Luzkan/MemeMate/commit/6a1cc80774ed06e4b4b5b360ceb5a244163e1d18) - 2019-05-08 - Marcel Jerzyk
### Added
- Room Database implementation
	- Meme, Interface, ListDatabase classes
	- Add Meme feature
	- Load Memes from db feature
	- This needs to be changed to firebase and is ready to go
- Dialog_New_Image.xml for meme addition
- CHANGELOG.md to keep track of changes in convenient way
- Plugin: kotlin-kapt
- Comments in places where they've not been

## [[0.1.0]](https://github.com/Luzkan/MemeMate/commit/2a278d3f08585ea5107d157c37ce8be07774c074) - 2019-05-07 - Marcel Jerzyk
### Description:
Small but big step towards functioning app: swiping.

### Added
- Meme Browsing with all the features
	- Swiping left / right
	- "Redo" swipe button
	- Pictures reloading 
	- All based on URL's
- Glide dependency
- Round Image view dependency
	
### Changed
- Colors and UI looks

## [[0.0.2]](https://github.com/Luzkan/MemeMate/commit/317bcb37184146c4f9ab18e4707565cb3b6a9c9e) - 2019-05-07 - Marcel Jerzyk
### Description:
Project is now successfully running.

### Added
- Readme

## [[0.0.1]](https://github.com/Luzkan/MemeMate/commit/6f7c3783ec893694cab408070b0513f6f0097ec1) - 2019-05-07 - Marcel Jerzyk
### Description:
Project is now successfully running. We have determined the looks of the app and preliminary work schedule.

### Added
- Bottom navbar
- Color Palette
- Launcher Icon (Logo)
- Icons
