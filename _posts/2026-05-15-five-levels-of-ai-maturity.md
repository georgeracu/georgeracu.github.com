---
layout: post
title: "Moving Beyond Seat Activation: The Five Levels of AI Maturity"
date: 2026-05-15
tags: [post, ai-engineering, ai-sdlc, engineering-leadership, jidoka]
description: "Why seat activation is a vanity metric and how to measure real AI maturity across an engineering organisation."
permalink: /blog/articles/five-levels-of-ai-maturity/
mathjax: false
---

>Originally published on [Substack](https://georgeracu.substack.com/p/five-levels-of-ai-maturity)

A few months into any corporate AI rollout, things start to feel a bit flat. The "seat activation" numbers look great on the slide deck: 80% of developers have the plugin installed, and everyone is nodding along to the 25% suggestion acceptance rate. Most managers are happy to see a green bar on a chart, as it looks like a win for the quarterly budget.

Walking the floor (or sitting in the Slack channels) tells a bit of a different story. You see developers using AI to save a few keystrokes on a unit test or to remember the syntax for a `map` function, while the core bottlenecks (the messy review cycles, the deployment friction, and the architectural debt) stay exactly where they were. I won't pretend I haven't done the same: reaching for the tab-key to finish a boilerplate loop while ignoring the fact that the loop itself is probably a sign of a larger design smell.

The problem is that we’re measuring adoption, not maturity. We're celebrating the fact that people have the tool, not what they're actually doing with it. My current work on our platform standards has been an attempt to map out what comes after the initial "tab-complete" honeymoon phase. I've broken it down into five levels, and honestly, most of us are stuck at the second one, wondering why the promised revolution hasn't arrived.

## 1. The Wild West: Where Luck is the Only Strategy

This is the "unsanctioned" baseline. AI is either a ghost or "shadow IT," which means there’s no policy, no training, and certainly no safety net. A few early adopters use personal accounts to finish their Jira tickets faster, but as there's no organisational logic, it's just individual luck. Proprietary code flies into public models with zero guard-rails, as nobody has bothered to set the content exclusion filters yet.

## 2. The Seat Activation Trap: Better Keyboards, Same Code

This is the plateau where most teams live. The tools are provisioned, the lawyers have signed off on the Acceptable Use Policy, and the dashboards are blinking. We talk a lot about "developer joy" and "speed," as if those were the only goals that mattered.

Conversation at this level is almost always about "how many seats are active." This vanity metric doesn't tell you if the software is getting any better. At Level 2, you often just end up shipping legacy-style spaghetti code, just faster. The AI acts as a better keyboard, not a better engineer. Reactive patterns dominate: tab-completing a line here and there without ever really understanding the "why" (or the "why not").

## 3. The Workflow Pivot: Augmenting the Team, Not the Dev

AI finally leaves the IDE at Level 3 and starts doing some of the heavy lifting in the team workflow. AI-augmented merge request reviews and automated pipeline failure analysis become the norm. The focus moves from "helping the dev" to "fixing the process."

Weekly active users climb and test coverage gets a measurable "AI uplift" as you feel the shift. For example, we've started using agents to draft the first pass of architectural documentation. It isn't perfect (what is?), but it's 80% there, which is 80% more than we usually managed when doing it by hand. You're no longer just assisting the person: you're augmenting the team. We start moving from contributing to the individual to contributing to the team.

## 4. The Agentic Leap: Becoming the Editor-in-Chief

Things get interesting here, as we move from "augmentation" to "delegation." Scoped autonomous agents handle defined tasks end-to-end. I'm talking about the boring stuff: dependency bumps, lint fixes, or those small, annoying feature requests that usually sit in the backlog for months.

Agents read the context, write the code, run the tests, and open the MR before a human even has to think about it. Humans become the editor-in-chief, not the writer. If at least 30% of your MRs aren't being initially drafted by an agent, you aren't at Level 4 yet. Trust is required here, but as the evaluation pass rate stays high, that trust starts to build. It isn't a gut feeling: it's built on running agents in shadow mode for a month and comparing their drafts against human ones until the gaps disappear.

## 5. The North Star: Jidoka and the Ultimate Andon Cord

The final level makes people a bit nervous, as it should. It’s where specific, low-risk changes move to merge without a human having to click "approve" every single time. Some might see this as skipping the human, but I see it as the ultimate expression of *jidoka*, or "autonomation."

In the Toyota sense, *jidoka* is about building a machine that can detect an abnormality and stop itself. At Level 5, the human touch (that essential "人" radical in the Japanese character for work) has moved from the repetitive task of clicking "merge" to the high-leverage task of architecting the safety net.

A Level 5 dependency bump is only possible because the system is empowered to pull its own Andon Cord. The agent updates the library, the tests pass, and the canary deployment looks solid, but as the system has superhuman observability, it can detect a micro-regression in latency and trigger an auto-rollback faster than any human could. We aren't there for production features yet, but for the "toil," Level 5 is about removing the noise so we can focus on the architecture that the agents can't quite grasp. The human hasn't left the loop: they've just built a better loop.

## Scaling Maturity Without the Hype

I'm obsessed with these levels as a way to stop "AI tourism." That's when we try a bit of everything without actually mastering any of it.

Teams firmly at Level 2 shouldn't waste time trying to build autonomous deployment bots. Focus on the pivot to Level 3 instead: get AI into your code reviews. Stabilise your guard-rails. Ensure your evaluation metrics are something you're actually proud of.

We’re moving from an era where we "use AI" to one where we "partner with agents." That transition is a bit messy, as all good engineering is, but it requires a new way of measuring what "good" actually looks like.

---

**I’m curious:** Honestly, where are you on this map? Most of us are hugging the Level 2 line. Pick one workflow today, like your unit test generation or your MR descriptions, and try to move it from "reactive tab-complete" to "automated draft." Let me know what breaks first.

---

## Glossary

- **AI-Assisted:** AI provides suggestions that a human reviews in the IDE.
- **Delegating to agents:** When an autonomous agent handles a whole task from start to finish (also called "agentic" activity).
- **Risk tiers:** Sorting code changes by how likely they are to break the system, such as a library update versus a database change.

## References

1. [Jidoka in AI-Assisted Development](/blog/articles/jidoka-for-ai-assisted-coding/)
2. [SDD is not Waterfall](https://georgeracu.substack.com/p/sdd-is-not-waterfall)

---

_Follow: [Substack](https://georgeracu.substack.com/) · [Dev.to](https://dev.to/george3421) · [LinkedIn](https://www.linkedin.com/in/george-racu-70670443) · [X](https://x.com/georgeracu)_
