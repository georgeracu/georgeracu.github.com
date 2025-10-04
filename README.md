# Personal Blog & Presentation Platform

A Jekyll-based personal website featuring blog posts and presentation content, built on the Minima theme with custom enhancements.

## Features

- **Blog Posts**: Technical articles focused on software development, distributed systems, and development practices
- **Presentations**: Integrated slideshow system using [Remark.js](https://github.com/gnab/remark/wiki) for markdown-to-slides conversion
- **Dev Container Support**: Complete development environment setup for consistent cross-platform development
- **PWA Support**: Progressive Web App capabilities with manifest and service worker
- **SEO Optimised**: Built-in SEO optimisation with jekyll-seo-tag plugin

## Content

### Distributed Systems Series
A comprehensive 5-part blog series covering:
1. **Recipe**: Overview of distributed system development phases
2. **Step 1**: The Walking Skeleton - Basic setup and CI/CD
3. **Step 2**: Solid Foundation - Health checks, logging, Infrastructure as Code
4. **Step 3**: Production Readiness - Monitoring, testing, security
5. **Step 4**: Operational Maturity - Resilience patterns, distributed tracing
6. **Step 5**: Evolution & Continuous Improvement - A/B testing, capacity planning

## Development

### Local Development
```bash
# Install dependencies
bundle install

# Clean and build the site
bundle exec jekyll clean
bundle exec jekyll build

# Serve locally (development server)
bundle exec jekyll serve

# Development with LiveReload
bundle exec jekyll serve --host 0.0.0.0 --livereload
```

### Dev Container Development
This repository includes a complete Dev Container configuration:

- **Base**: Multi-stage Ruby 3.1 Debian image
- **Features**: GitHub CLI integration
- **Ports**: 4000 (Jekyll), 44004 (LiveReload)
- **Mount Point**: `/srv/jekyll`
- **Dependencies**: Automatically installs on container creation

Compatible with VS Code, GitHub Codespaces, and other Dev Container-supporting environments.

## Architecture

### Structure
- `_posts/`: Published blog posts
- `_drafts/`: Draft posts and unpublished content
- `_includes/`: Reusable components (presentations, social, UX content)
- `_data/`: Site data files including centralised link management
- `_layouts/`: Page templates including presentation layout
- `_sass/`: Custom SCSS styling
- `assets/`: Static assets and presentation resources

### Theme
Built on Jekyll Minima theme with custom enhancements:
- Bootstrap and Flexbox Grid integration
- Custom Remark.js styling for presentations
- Social media integration
- Google Analytics tracking

## References

- [Jekyll Documentation](https://jekyllrb.com/)
- [Minima Theme](https://github.com/jekyll/minima)
- [Remark.js Slideshow Library](https://github.com/gnab/remark/wiki)
