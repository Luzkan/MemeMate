# TodoTasks
All current tasks for this project will be documented in this file.

High priority tasks are written using **bold** font, regular with no formatting and low priority - with *italics*. Details of the tasks are written with additional list for that task.

At this moment there's more to create than to improve or build-on so let's change it asap :3

___________________________________

### [Bugs found]
- None

___________________________________

## Current TodoTasks:

- Meme Zoom In    
- Implement changeEmail in SettingsActivity
- Hearth Animation
- Order of Liked Memes


## Changes awaiting implementation:

- Big photo to last liked meme
- Starting position of navbar to browse (main)

### Build-on:

### Improve:

- Settings
	- Deleting memes from storage after deleting an account. (meme in db should contain filename)

- Memes Algorithm
	- Neat idea: The more a meme gets high% of dislikes the lesser chance of it showing up for other users 
	- Show memes only in desired radius from user location

- User Profile UI
	- Overall design.	
	
- *Detailed Info About the Meme and it's creator upon onClick()*
	- *Look up for other memes by user? Problem is that you couldn't like them and it is kinda weird with <more than one like needed to match> feature.*
	- *Full-screening the meme
	
- *Moderating module*
	- *Concern: dick pics*
	
- Very Minor: Change Serializable in "GalleryFullscreenFragment", "Browse Fragment" and "Profile Fragment" to Parcelable to avoid potential ClassCastException

### Done:
- Swipe hand gesture between fragments
- Colors & Icons
- Meme Browsing
- Memes DB
- Login and sign up (also with Facebook)
- Connected to firebase
- Profile UI
- Basic add meme UI
- Data on profile and in top is downloaded from Firebase
- Log out, password reset, account deletion
