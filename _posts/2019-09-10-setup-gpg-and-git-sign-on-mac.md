---
layout: post
title: "Setup GPG on Mac and sign git repositories"
date: 2019-09-10 07:00:00 +0000
tags: [programming, software-engineering, gpg, git]
mathjax: false
---

Gnu Privacy Guard is an encryption software program that uses public-key cryptography for key exchange. More about it can be found on [Wikipedia's](https://en.wikipedia.org/wiki/GNU_Privacy_Guard) web page or on the [official](https://www.gnupg.org/) web page.

Using a GPG key to sign your commits allows Github/GitLab/BitBucket to show a nice *Verified* icon against your commit and also to show the key ID that was used for that commit. This is a safety feature that allows commit owners to prove that they authored the commit, or not authored, depending on the situation. This is necessary as anyone can create GitHub/GitLab/BitBucket accounts and pretend to be someone else by using their name.

In order to use it on Mac, a few easy steps are required:

- Install the tools using [Homebrew](https://brew.sh/)
  `brew install gnupg2 pinentry-mac`
  - GnuPG2 is the package with the GPG related tools
  - Pinentry-mac is used to capture the key passphrase when using the key
- After successfull instalation, we need a small hack to make the gpg agent use pinentry-mac:
  `echo "pinentry-program /usr/local/bin/pinentry-mac" >> ~/.gnupg/gpg-agent.conf`
- Restart the gpg-agent:
  `killall gpg-agent`
- Create a new GPG key:
  `gpg --full-generate-key`
- A few things to choose when generating a key:
  - Key kind: use (1), default
  - 4096 bits long
  - (0) key does not expire
  - Add your details (name, email, comment)
  - Choose a strong key passphrase that you **can remember**
- List all the keys on the system:
  `gpg --list-secret-keys`
- Grab the sec part, without the rsa/4096 and without the created date
- Go to Github `https://github.com/settings/keys` and click _New GPG key_
- Output the public key in ASCII `gpg --armor --export <sec-part-here>` and copy the output
- Save the new key
- To use this key globally, instruct git to use it:
  `git config --global user.signingkey <sec part here>`
- If you have several GPG keys that you want to use on the same machine for different repositories, then add the key sec on the local git config, edit .git/config file:

```bash
[user]
        name = <Your name here>
        email = <email address from the GPG key>
        signingkey = <sec id from your key>
[commit]
        gpgsign = true
```

- The settings above can be accomplished by editing the .git/config file or using git commands:

```bash
git config user.name <Your name here>
git config user.email <email address form the GPG key>
git config commit.gpgsign true
git config user.signingkey <key sec here>
```

- If you don't have gpgsign flag enabled, when you commit, you can ask git to sign the commit with `git commit -S -m "Initial commit"`
- If you don't want to use the _-S_ flag all the time, you can also enable signing globally with `git config --global commit.gpgsign true`
- If you get an error similar to _secret key not available_, then you might want to set your gpg program globally as gpg2:
  `--global gpg.program gpg2`
