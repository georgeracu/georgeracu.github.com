
{% assign remarkjs_link = site.data.links | where: "id", 52 | first %}
{% assign jekyll_link = site.data.links | where: "id", 53 | first %}
{% assign github_pages_link = site.data.links | where: "id", 54 | first %}

# How to add RemarkJS to a Jekyll blog

<hr/>

* What is [{{ remarkjs_link.title }}]({{ remarkjs_link.link }})?
* Why combine it with [{{ jekyll_link.title }}]({{ jekyll_link.link }})?
* Step-by-step implementation
* Real-world examples from this blog
* Advanced customization
* Best practices and tips

---

## What is RemarkJS?

<hr/>

{% assign remarkjs_link = site.data.links | where: "id", 52 | first %}

[{{ remarkjs_link.title }}]({{ remarkjs_link.link }}) is a simple, in-browser, markdown-driven slideshow tool.

### Key Features:
* **Markdown-based** - Write slides in familiar Markdown syntax
* **Browser-native** - No external dependencies or build tools
* **Keyboard navigation** - Arrow keys, page up/down, and more
* **Presenter mode** - Dual-screen support with notes
* **Themeable** - Customise with CSS
* **Lightweight** - Single JavaScript file (~85KB)

### Why RemarkJS?
* Simple syntax for complex presentations
* Version control friendly (plain text)
* Easy to integrate with static site generators
* Perfect for technical presentations with code

---

## Why Jekyll + RemarkJS?

<hr/>

{% assign jekyll_link = site.data.links | where: "id", 53 | first %}
{% assign github_pages_link = site.data.links | where: "id", 54 | first %}

### The Perfect Combination

**[{{ jekyll_link.title }}]({{ jekyll_link.link }}) provides:**
* Static site generation
* Template system with Liquid
* Markdown processing
* [{{ github_pages_link.title }}]({{ github_pages_link.link }}) hosting

**RemarkJS adds:**
* Interactive presentations
* Browser-based slideshow capabilities
* Professional presentation features

---

## Benefits

<hr/>

* **One codebase** - Blog posts and presentations in the same repository
* **Consistent styling** - Shared CSS and theme
* **Easy deployment** - Automatic publishing via GitHub Pages
* **SEO friendly** - Each presentation gets its own URL
* **Reusable content** - Include presentation content in blog posts

---

## Architecture Overview

<hr/>

### This Blog's Setup

```
├── _layouts/
│   └── presentation.html     # RemarkJS presentation layout
├── _includes/
│   └── presentations/        # Presentation content files
│       ├── 001-spring-boot.md
│       ├── 002-testing.md
│       └── ...
├── _posts/
│   └── 2024-01-01-my-talk.md # Presentation wrapper posts
├── assets/
│   ├── css/
│   │   └── custom-remark.css # Custom RemarkJS styling
│   └── js/
│       └── remark-0.14.1.min.js # RemarkJS library
└── presentations.html        # Presentations index page
```

### Data Flow:
1. **Content** → `_includes/presentations/talk.md`
2. **Post** → `_posts/2024-talk.md` (includes content)
3. **Layout** → `_layouts/presentation.html` (RemarkJS setup)
4. **Styling** → `assets/css/custom-remark.css`

---

## Download RemarkJS to your project

<hr/>

### Get the Library

```bash
# Download RemarkJS to your assets directory
curl -o assets/js/remark-latest.min.js https://remarkjs.com/downloads/remark-latest.min.js

# Or use a specific version (recommended)
curl -o assets/js/remark-0.14.1.min.js \
  https://github.com/gnab/remark/releases/download/v0.14.0/remark.min.js
```

### File Structure
```
assets/
└── js/
    └── remark-0.14.1.min.js  # 85KB minified
```

**Why local hosting?**
* Reliable access (no CDN dependencies)
* Consistent performance
* Works offline during development

---

## Create Presentation Layout and Styling

<hr/>

- Create a custom layout that will be used when rendering presentations.
- Checkout this file for a custom layout in Jekyll `_layouts/presentation.html`
- Create custom styling to be applied for presentations.
- The content of this file should help `assets/css/custom-remark.css`
- All presentations live in `_includes/presentations`
- Each presentation lives in the `presentations` directory
- Create a presentation slide and add your content: `_includes/presentations/my-talk.md`

---

## Create Presentation Post

<hr/>

### Post Content

Post title is `_posts/2024-01-15-my-amazing-talk.md`

Post content is only the import of the presentation file:

{% highlight yaml%}
{% raw %}

---
layout: presentation
title: My Amazing Talk
permalink: /presentations/my-amazing-talk/
tags: [presentation, demo, tutorial]
description: A comprehensive guide to building amazing presentations
---

<pre>{% include presentations/my-talk.md %}</pre>

{% endraw %}
{% endhighlight %}

### Key Elements:

* **`layout: presentation`** - Uses our custom layout
* **`permalink:`** - Clean URL structure
* **`<pre>` tag** - Preserves formatting for RemarkJS
* **`{% raw %}{% include %}{% endraw %}`** - Separates content from metadata

---

## Code Highlighting - With a RemarkJS Extension

<hr/>

RemarkJS supports syntax highlight with some built-in extensions.

### Basic Code Blocks:

```java
public class Example {
    public void method() {
        System.out.println("Hello");
    }
}
```

---

## Images and Media

<hr/>

### Responsive Images:

Markdown: 

`![Description](path/to/image.png)`

HTML with custom image sizing: 

`<img src="path/to/image.png" width="50%" />`

### Background Images:

{% highlight markdown %}
{% raw %}

background-image: url(path/to/background.jpg)

# Slide Title

Content with background image

{% endraw %}
{% endhighlight%}

---

## Integration with Jekyll

<hr/>

### Using Jekyll Variables:

{% highlight markdown%}
{% raw %}
# Welcome to {{ site.title }}

Today: {{ site.time | date: "%B %d, %Y" }}

Presenter: {{ page.author | default: "George Racu" }}
{% endraw %}
{% endhighlight %}

### Including Shared Content:

{% highlight markdown%}
{% raw %}
# About the Speaker

{% include speaker-bio.md %}

# Contact Information

{% include contact-info.md %}
{% endraw %}
{% endhighlight %}

### Liquid Filters:

{% highlight markdown%}
{% raw %}
# Recent Posts

{% for post in site.posts limit:3 %}
* [{{ post.title }}]({{ post.url }})
{% endfor %}
{% endraw %}
{% endhighlight %}

---

## Performance Optimisation

<hr/>

### Loading Speed:
* **Use local RemarkJS** - No CDN delays
* **Optimize images** - Compress presentation images
* **Minimize CSS** - Remove unused styles
* **Lazy loading** - Load slides on demand

### Browser Performance:
```javascript
// In presentation.html
var slideshow = remark.create({
    slideNumberFormat: '', // Disable slide numbers for performance
    ratio: '16:9',        // Set aspect ratio
    navigation: {
        scroll: false,     // Disable scroll for mobile performance
        touch: true,      // Enable touch for mobile
        click: false      // Prevent accidental clicks
    }
});
```

---

## Mobile Optimisation

<hr/>

```css
/* Responsive design */
@media (max-width: 768px) {
    .remark-slide-content {
        font-size: 1.2em;
        padding: 1em;
    }

    .remark-code {
        font-size: 0.9em;
    }
}
```

---

## Best Practices

<hr/>

### Content Organization:
* **One concept per slide** - Keep it simple
* **Use bullet points** - Easy to scan
* **Limit text** - More visuals, less text
* **Consistent formatting** - Use templates

### File Management:
* **Separate content files** - Keep in `_includes/presentations/`
* **Descriptive filenames** - `001-topic-name.md`
* **Version control** - Git-friendly plain text
* **Reusable components** - Common headers/footers

### Presentation Tips:
* **Test on target devices** - Different screens/browsers
* **Prepare for offline** - Local assets
* **Practice navigation** - Know your shortcuts
* **Have backups** - PDF export, local copies

---

## Troubleshooting

<hr/>

### Common Issues:

**Slides not loading:**

Check textarea id

**Formatting problems:**

Ensure proper slide separators: `---`

Not `--` or `----`

**JavaScript errors:**

```html
<!-- Verify RemarkJS path -->
<script src="/assets/js/remark-0.14.1.min.js"></script>
```

**Mobile issues:**

```css
/* Add viewport meta tag */
<meta name="viewport" content="width=device-width, initial-scale=1">
```

---

## Real Examples from This Blog

<hr/>

### File Structure:

```plain
_includes/presentations/
├── 001-spring-boot-on-k8s.md      # Kubernetes deployment
├── 002-code-fit-for-testing.md    # Testing practices
├── 003-curse-of-optional.md       # Java Optional guide
├── 004-efficient-java.md          # Immutability patterns
└── 2021-09-01-remarkjs-jekyll.md  # This presentation!
```

### Generated URLs:

* `/presentations/run-spring-boot-on-kubernetes/`
* `/presentations/write-code-fit-for-testing/`
* `/presentations/curse-of-optional/`
* `/presentations/efficient-java/`

### Navigation Integration:

```html
<!-- In _includes/header.html -->
<li><a href="/presentations">Presentations</a></li>
```

---

## Advanced Customisation

<hr/>

### Dynamic Content:

```javascript
// Custom RemarkJS macros
remark.macros.scale = function (percentage) {
    var url = this;
    return '<img src="' + url + '" style="width: ' + percentage + '" />';
};

// Usage: ![:scale 50%](image.png)
```

### Theme Variations:

```css
/* Dark theme */
.dark-theme {
    background: #2d3748;
    color: #e2e8f0;
}

/* Corporate theme */
.corporate {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}
```

---

## Deployment

<hr/>

### GitHub Pages Setup:

**Repository structure:**

```plain
your-blog/
├── _config.yml
├── _layouts/presentation.html
├── assets/js/remark-0.14.1.min.js
└── _posts/presentation-posts.md
```

**GitHub Pages configuration:**

```yaml
# _config.yml
plugins:
  - jekyll-seo-tag

# Build settings
markdown: kramdown
highlighter: rouge
```

**Automatic deployment:**
   - Push to main branch
   - GitHub Pages builds automatically
   - Presentations available immediately

---

## Resources & References

<hr/>

{% assign remarkjs_link = site.data.links | where: "id", 52 | first %}
{% assign jekyll_link = site.data.links | where: "id", 53 | first %}
{% assign github_pages_link = site.data.links | where: "id", 54 | first %}
{% assign google_fonts_link = site.data.links | where: "id", 55 | first %}
{% assign mdn_link = site.data.links | where: "id", 56 | first %}

### Essential Links:
* [{{ remarkjs_link.title }}]({{ remarkjs_link.link }}) - Official documentation
* [{{ jekyll_link.title }}]({{ jekyll_link.link }}) - Static site generator
* [{{ github_pages_link.title }}]({{ github_pages_link.link }}) - Free hosting
* [{{ google_fonts_link.title }}]({{ google_fonts_link.link }}) - Typography
* [{{ mdn_link.title }}]({{ mdn_link.link }}) - CSS reference

---

## Use this blog as an example

<hr/>

### This Blog's Code:
* [View source on GitHub](https://github.com/{{ site.github_username }}/{{ site.github_username }}.github.io)
* Copy presentation templates
* Study real-world examples
* Contribute improvements

### Learning Path:
1. Start with basic slides
2. Add custom styling
3. Integrate with Jekyll
4. Deploy to GitHub Pages
5. Create presentation library

---

{% include contact-info.md %}

**Try it yourself:**
1. Clone this blog's repository
2. Explore the presentation files
3. Create your own presentation
4. Deploy to GitHub Pages
