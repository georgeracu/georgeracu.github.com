---
layout: post
title: "Set union of two lists in Python"
date: 2013-06-04 07:00:00 +0000
tags: [programming, python]
mathjax: false
---

Hi there!

This is my first blog post so please be patient with me. Today I started a small course about Computer Science and as programming language is using Python. For me this the first time when I use Python and I am impressed by the syntax and other features. I am coming from Java and c# but I hope I will do fine with Python to.

One of my tasks for today it was to define a function in Python that takes as input two lists a and b, it does set union on them and the result is the list a modified with the new values. I find it very easy to do, I didn't had to use any indexes or something like that, just 3 lines of code and problem solved.

Here is the code:

```python
def union(a, b):
    for el in b:             #iterate over list b
        if el not in a:      #if element of b is not in a
            a.append(el)     #append the element to a
```

Now a test in console it should look like:

```bash
a = [1, 2, 4]
b = [2, 3, 6]
union(a, b)
print a
&gt;&gt;&gt; [1, 2, 3, 4, 6]
print b
&gt;&gt;&gt; [2, 3, 6]
```

That's it.
