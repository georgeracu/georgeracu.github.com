---
layout: post
title: Hosting a PWA with Jekyll and Github pages
permalink: /blog/articles/hosting-pwa-with-jekyll-and-github-pages/
tags: [jekyll, github, pwa, post]
mathjax: false
description: Progressive Web App hosted on Github pages and generatd by Jekyll
---

In order to have a [Progressive Web App (PWA)](https://developer.mozilla.org/en-US/docs/Web/Progressive_web_apps) hosted in GitHub and served as part of your Jekyll generated website we need to follow several steps listed bellow.

## Add a `manifest.json` file

Using a [web app manifest](https://developer.mozilla.org/en-US/docs/Web/Manifest), we are signalling to the browser that the website can be installed and a regular app and that it can work offline.

## Add a Service Worker

The [Service Worker](https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API) are a proxy between the web app, the website and the network (when available).

## Generate app icons

https://github.com/elegantapp/pwa-asset-generator

