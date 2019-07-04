---
layout: post
title: "Git micro commits"
date: 2015-04-12 07:00:00 +0000
tags: [programming, git]
mathjax: false
---

Probably many of you have used [Git](http://git-scm.com/), or [SVN](https://subversion.apache.org/) or both. If you didn't use them then give Git a try and forget SVN, it will make your life easier. You can find the most popular git cloud repositories at [GitHub](https://github.com/) and [BitBucket](https://bitbucket.org/). I'm not gonna waste your time with what is Git or SVN, you can read yourself and figure it out.

Now why I choose to compare micro commits vs large commits? Well, until recently I used a piece of software built on SVN. When it launched, probably was a very hot tool to use, but as time passes other tools come around and this one doesn't satisfy your needs at full. Using SVN for a few years now I couldn't branch. The tool had the option to branch but it was a nightmare to use it so instead of ruining the whole repository I choose not to use it. So basically I was forced to commit and push only after my code was done, tested and fully functional. Imagine working a week, or even more, adding more and more stuff to your solution and push everything into one massive chunk of code. For me it was a nightmare as if any of the code me or my team were pushing might need removal you have to remove the entire push. This is the story for mega commits.

Recently I decided to use git for my final year project. Some reasons behind this decisions involved a good branching system, more grained control over your code and your work and very easy rollback in case of mistakes. Usage of a new system came with the specific learning curve and after I got used with using it I forgot about the old SVN system. What is interesting is that when you learn a similar technology you tend to use your old habits with it and not be aware of some of the advantages. This happened to me as even having all the advantages of git I kept commiting and pushing after all the work has been done. While working on my [Android](https://www.android.com/) project I had a git issue that involved adding activities, classes, creating data models and changing other existing ones so I started to commit after each change. For example when I added a class, an activity or an xml file I commited. Then I realized that this is a more efficient way to commit my work as I could see better commit messages and cherry pick commits. Also my commit messages are more specific and my final one will close the GitHub issue I was working on.

This is one day of work:

![image](/assets/img/git_micro_commits.PNG)

The tool I use to manage my repositories is called [SourceTree](https://www.sourcetreeapp.com/) and is free.
