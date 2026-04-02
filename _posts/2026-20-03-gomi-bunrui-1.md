---
layout: post
title: "I Used 8 AI Tools to Build and Ship a Product Solo. Here's the Honest Breakdown."
date: 2026-03-20
tags: [post, development, ai, gomi-bunrui]
description: "This is Part 1 of an ongoing series about building [Gomi Bunrui](https://gomi-bunrui.app), an AI-powered waste classification app for Japan, using AI tools at every layer of the stack."
permalink: /blog/articles/building-gomi-bunrui-part-1/
mathjax: false
---

_This is Part 1 of an ongoing series about building [Gomi Bunrui](https://gomi-bunrui.app), an AI-powered waste classification app for Japan, using AI tools at every layer of the stack._

---

Over the past few weeks, I built and shipped a full-stack product as a solo developer, in my spare time. A production app with five Lambda functions (so far), a React PWA, bilingual support (English and Japanese), Cognito authentication, structured observability, CI/CD pipelines, 60+ test files, and a 24-document knowledge base.

The app is called Gomi Bunrui (ゴミ分類). It uses your phone camera to scan waste items and tells you exactly how to sort them based on your city's specific rules. Japan has 1,741 municipalities, each with different recycling categories. A single PET bottle becomes three separate waste streams. It's genuinely confusing, and I say that as someone who lives here.

But this post isn't really about the app, it's about how I built it.

I used eight AI tools across every stage: planning, architecture, coding, design, testing, content, and the product's own AI brain. Some of them were brilliant. Some of them fought each other. One of them generated 31 structured specs that became the most useful development artefact I've ever worked with. Another one gave me a logo I'm still not sure about.

Here's the landscape of what I used, why I chose each tool, and what I actually think of them after shipping.

---

## The Tool Map

Before diving in, here's the full picture. Each tool occupied a distinct role, though the boundaries got messy in practice.

**Kiro** handled spec-driven development. I'd describe what I wanted to build, and Kiro would generate requirements, a design document, and a task list. Over the course of the project, this produced 31 numbered specs covering everything from backend initialisation to guardrail implementation. Three custom hooks automated commit messages, smoke test analysis, and spec numbering. Kiro was the project manager I didn't have. I used 2600 credits with Kiro.

**Claude Code** was my command-line coding partner. When I needed to implement something specific, refactor across files, or debug a gnarly issue, Claude Code worked directly in the codebase. It handled Kotlin backend code, React frontend components, SAM templates, and GitHub Actions workflows. Where Kiro thought in specs, Claude Code thought in code. I used Claude Code after the competition ended and I run out of credits for Kiro.

**Claude Chat** was the strategist. Planning, writing, brainstorming, content drafting, prompt design. Every system prompt for the Bedrock classification started as a conversation here. The blog post you're reading was drafted here too. Claude Chat doesn't touch code directly, but it shaped many of the important decisions.

**Gemini** entered the picture as a specialised agent system. I configured three agents (a documentation organiser, a spec reviewer, and a frontend developer) backed by 21 skill definitions. Gemini handled documentation structure using the Diataxis framework and served as a second opinion on specs and pull requests. I would use Gemini CLI to fix code review comments and Gemini GitHub bot to automatically review my PRs.

**Jules** (Google's asynchronous coding agent) became my fallback when I ran out of Kiro credits. I'd hand it independent tasks mostly on React frontend, and it would work through them in its own environment. The surprise was that Jules wasn't just a weaker Kiro substitute. It had genuinely different strengths, particularly in how it reasoned through implementation. More on this in a later post, but the short version is: having a second AI coding agent with a different model behind it gave me a useful second perspective, not just a backup. Gemini models are being used by the Gemini CLI as well, so basically different tools and interfaces for the same LLM.

**Amazon Bedrock** (Claude Haiku 4.5) is the product's AI core. It powers the actual waste classification, running behind a Kotlin Lambda with city-specific system prompts. This isn't a development tool in the same sense as the others; it's the thing the app sells. But designing and iterating the prompts was a huge part of the build.

**Stitch** (Google's design generation tool) produced screen designs from structured prompts. I developed a consistent prompt template: brand/style block, screen-specific layout, real sample data, explicit exclusion sections, and a "feel" paragraph. The outputs became the visual reference for every frontend screen. I also created a design system that I would use for all my designs.

**NanoBanana** handled image generation for logos, hero images, and social media graphics. The results were hit-or-miss, but when it hit, it saved me from needing a designer entirely.

---

## What This Actually Cost

People always ask, so here it is. This wasn't free, but it wasn't expensive either.

**Claude Pro** ($204/year) gave me access to Claude Chat for planning, writing, and prompt design, plus Claude Code for hands-on coding in the repo. This was an active subscription that I had from last year and I used only for a couple of months.

**Google One AI Pro** (family plan) unlocked Gemini, Jules, Stitch and NanoBanana Pro model. I didn't know that someone from my family was already paying for the subscription until towards the end of the competition, so the marginal cost for the AI tools was zero. This is partly why I ended up using so many Google products and because they have a generous free tier for Stitch. When Jules turned out to be surprisingly capable for independent backend and frontend tasks, a subscription that someone from my family was already paying for effectively gave me another AI coding agent.

**Kiro+** came with 2,500 credits as part of the AWS AIdeas competition (500 on signup and 2000 when being shortlisted) and 50 monthly credits for the free subscription. Those credits fuelled the spec-driven workflow for most of the build. When they ran out, Jules filled the gap. All Kiro usage was covered by free credits.

**AWS Free Tier + competition credits** covered the infrastructure. Lambda, DynamoDB, API Gateway, S3, CloudFront, Cognito. The only service that sits outside the free tier is Amazon Bedrock (Claude Haiku 4.5), and even that's modest at Haiku pricing. The competition also included AWS credits to offset costs during the build, so that was free as well.

**NanoBanana** has a free tier that covered what I needed and when I discovered that I have a Pro subscription I didn't have any work for it.

All in, my recurring cost was roughly **$17/month for Claude Pro**. Everything else was free tier, competition credits, or using a family plan that I didn't pay for. For what I shipped, that's absurdly cheap.

---

## What Surprised Me

**Four AI coding assistants can coexist in one repo.** My `.kiro/` directory has 31 specs and 3 hooks. My `.claude/` directory has project configuration. My `.gemini/` directory has 3 agents and 21 skills. Jules worked from its own environment on tasks I assigned it. They don't know about each other, and that's fine. They serve different purposes and I reach for whichever one fits the task. The mental model is closer to having four colleagues with different strengths than having one tool you configure four ways.

**Spec-driven development changed the game.** This was a big productivity multiplier. Reading my Kiro specs in sequence (01-backend-initialization through 30-backend-guardrails) is basically the story of the entire project. Each spec forced me to think about requirements and design before writing code, which meant fewer rewrites and a traceable audit trail of every decision. I'll write a dedicated post about this because there's a lot to unpack.

**The orchestration overhead is real.** Having eight tools sounds impressive until you're context-switching between them constantly. Each tool has its own context window, its own memory (or lack of it), its own strengths. Knowing when to use Kiro vs. Jules vs. Claude Code vs. Gemini is a skill I developed over time, and I got it wrong plenty of times. There's a post coming on this too.

**AI-generated designs need a strong brief.** Stitch produced great results when I gave it a highly structured prompt with real data and explicit constraints. When I gave it vague instructions, it produced generic app screens that could have been anything. The quality of your brief determines the quality of your output. This applies to every tool on this list.

**Production-grade observability doesn't happen by accident, even with AI help.** X-Ray tracing on all five Lambda functions, correlation IDs flowing through every log line, PII masking for email addresses, Bedrock call tracking with token usage metrics. The AI tools helped implement each piece, but I had to know what to ask for. The tools accelerated the work; they didn't replace the knowledge of what "production-ready" means.

---

## What Didn't Work

**Cross-tool context is the unsolved problem.** Kiro doesn't know what Claude Code just did. Claude Code doesn't know what Jules implemented. Gemini doesn't know what any of them suggested. I was constantly copy-pasting context between tools until I built a strong knowledge-base and documentation inside the project and I didn't share work between tools, each one had an individual task assigned to it.

**AI-generated tests need human judgment.** The tools could generate tests quickly, but they tended toward happy-path coverage. The interesting edge cases, the property-based tests that actually caught bugs, the integration tests that verified real Bedrock behaviour; those required me to think about what could go wrong and then direct the tools to implement it. They also required me to think about several testing layers (think of the Swiss cheese model) and guide the AI in creating those tests.

**Image generation is the weakest link.** NanoBanana produced usable assets, but the iteration cycle was slow and unpredictable. I spent more time on image generation than I expected, and the results were the part of the project I'm least satisfied with. For a solo developer, this is still the area where hiring a human would have the clearest ROI.

---

## The Numbers

Just to give a sense of scale:

- **5 Lambda functions** (Kotlin, Java 21, ARM64, all with X-Ray tracing)
- **31 Kiro specs** (requirements → design → tasks for every feature)
- **11 GitHub Actions workflows** (test, release, deploy across three projects)
- **60+ test files** (unit, property-based, integration, e2e)
- **4 municipality prompt files** (Nagoya, Shibuya, Osaka, Kyoto)
- **24 knowledge base documents** across 11 categories
- **Fully bilingual** (English/Japanese throughout)
- **Zero clickops** (everything deployed via SAM and scripts)

All of this was built by one person over roughly eight weeks, in my spare time, while having a full-time job.

I'm not claiming AI tools made this trivial. They didn't. There were late nights, frustrating debugging sessions, and plenty of moments where I had to override what the AI suggested. But I am claiming that this scope would have been impossible without AI assistance. The tools didn't replace my judgment. They multiplied my capacity.

---

## What's Coming Next

This is the first post in a series. Each subsequent post will go deep on a specific aspect of the build. Here's what I'm planning:

1. **Kiro's spec-driven workflow** and how 31 specs became the backbone of the project
2. **Claude Chat as a daily adviser** on project planning and ideation
3. **Prompt engineering for production** with Amazon Bedrock (the classification system)
4. **AI for design** with Stitch and NanoBanana
5. **The orchestration problem** when you have 8 AI tools and one brain
6. **Building bilingual** (EN/JA) with AI assistance
7. **Infrastructure as code** with AI-generated SAM templates, Lambda, and CI/CD
8. **Observability and production-readiness** for an AI-powered app
9. **How to document your project** using Diataxis method
10. **Creating and maintaining a knowledge-base** for providing context to AI and humans

I'll also write about the specific technical decisions that don't fit neatly into one category: why `http4k` over Spring Boot, why prompt-based guardrails instead of AWS Bedrock Guardrails, why ARM64 for all Lambdas, and the unified S3 bucket strategy that lets a website and PWA coexist.

If you're a developer exploring how AI tools fit into real development work, I think you'll find something useful here. The honest answer is messier and more interesting than "AI writes all your code now."

It doesn't. But it does change what one person can build.

---

_Gomi Bunrui is live at [gomi-bunrui.app](https://gomi-bunrui.app). The project was built as an entry for the AWS 10,000 AIdeas competition. You can read the full competition article on [AWS Builder Center](https://builder.aws.com/content/3Ao3pYCt3LOf4G4Ju8Ct7V5a9O6/aideas-gomi-bunrui)._

_Follow the series: [Substack](https://georgeracu.substack.com/) · [Dev.to](https://dev.to/george3421) · [LinkedIn](https://www.linkedin.com/in/george-racu-70670443) · [X](https://x.com/Georgeracu)_
