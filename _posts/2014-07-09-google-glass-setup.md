---
layout: post
title: "Google Glass Development - setup tools, environment and turn on debugging on Glass"
date: 2014-07-09 07:00:00 +0000
tags: [programming, Android, Google Glass]
description: A comprehensive guide to setting up Google Glass development environment using Android Studio, enabling debug mode on Google Glass hardware, and navigating the Glass interface for app development.
mathjax: false
---

Hi there!

Recently I acquired a Google Glass and I thought to make my final year project based on this device. I know this is new technology and the hardware is still a prototype but I think I can learn new interesting things. So lets get started by installing the development tools. First I downloaded and installed Android Studio from Google. Here is the link I used [Android Studio](https://developer.android.com/studio). I'm using Windows so you have to select your OS if is not already detected by Google and you are presented with a download link for your OS. You can also develop your project(s) by using Eclipse and the ADT plugin.

After I installed Android Studio I put my glass in development mode. This is not very hard to do, you just need a bit of practice on how to navigate with the touch pad. Just a short description on how to navigate the touch pad:

- swipe backward to get to next menu item or to the settings menu if you are on the home screen;
- swipe forward to access previous items or cards;
- touch the pad to access the menu item highlighted, to see a picture or to play a video. Tapping means that you want to execute the menu option in focus.

Now lets get back on how to put your Google Glass in debug mode. First step is to turn on your Google Glass by pressing the little round button inside the frame of the Glass. After your device is ON then on the Home screen (the screen with clock and "ok glass" command) swipe back with the touch pad (swipe from front to the back of your head) until you will see the menu "Settings" and you can see the icons with the battery and the cloud. At this point tap on your touch pad to access this menu, then swipe forward using the touch pad until you see the "Device info" menu screen. At this point tap on your touch pad to access this menu and swipe forward until you see the "Turn on debug" menu. When you see this menu option tap on the side and the Glass it will inform you that you have activated debug mode and your menu will change to "Turn off debugging". At this moment you are ready to start developing.

To turn off debugging on your Google Glass just follow previous steps and tap the glass when you see "Turn off debugging" menu option.

Next option after this menu item should be "Factory Reset" so please be careful what you tap or you might erase all your data on your Glass. To avoid this turn on backup and Google will backup what's on it.

Once all your installation is done and your device is ON debug mode you can start developing that cool stuff you dreamed off or you are paid to do it. Either one of these, enjoy it :).

I will come back with more information regarding my project on Glass and code samples.
