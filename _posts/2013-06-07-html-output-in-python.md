---
layout: post
title: "How To: Get the rendered HTML of a webpage with Python"
date: 2013-06-07 07:00:00 +0000
tags: [programming, python]
mathjax: false
---

Hello again!

This time I am going to show you how to get the rendered HTML of a webpage using Python. For this task we need only ten lines of code and to import `urllib2`, the library that it will help us. The rendered HTML of a webpage is the HTML that our web-browser receives from the server and renders on the screen. In simple words, this is what we see on the screen and not the code used to generate the webpage.

This HTML code I use to find specific tags into the code.

Here is the code:

```python
def get_page_code(link):
    import urllib2                 # import the necessary library
    html = ""                      # initiate an empty string as the variable that holds the html code
    req = urllib2.Request(link)    # initiate a request
    try:
        response = urllib2.urlopen(req) # store the response in a file
        html = response.read()     # read the content of the file
        response.close()           # close the file
    except ValueError:
        return html                # if there is an error return the empty string
    return html                    # return the generated html
```

Keep in mind that you will get a long string as output.
