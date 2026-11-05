---
layout: post
title: "Pi-Agent vs Claude Code in Custom Code Review Agents"
date: 2026-07-13
tags: [post, pidev, software-engineering, agent-harness, claude-code, agentic-code-review]
description: "The Invocation Gap: Same Model, Same Prompt, Two Different Reviewers"
permalink: /blog/articles/pi-agent-vs-claude-code-in-custom-code-review-agents/
canonical_url: https://georgeracu.substack.com/p/pi-agent-vs-claude-code-in-custom
mathjax: false
---

> Originally published on [Substack](https://georgeracu.substack.com/p/pi-agent-vs-claude-code-in-custom) on 13 July 2026.

I've spent the last few weeks benchmarking AI code review bots for our GitLab pipelines, and like most people running these evaluations I'd been treating the model as the thing under test: Sonnet against Haiku, prompt tweak against prompt tweak. This week I turned the question round. What happens if you hold the model still and swap the loop around it? Same model, same prompt, same diff, same tools, same posting pipeline, and only the *harness* changed. The quality difference was larger than most model swaps I've measured, and it came down to a behaviour I hadn't thought to test.

## The Benchmark: Five Bugs I Planted Myself

The test bed is a small Java client library and one deliberately broken merge request. Before any review ran I seeded five defects (planting bugs on purpose is more fun than it should be): an inverted boolean flag that contradicts its own javadoc, full domain objects with personal data going into log lines, string concatenation where the codebase convention is structured logging, a missing retry annotation on a method whose siblings all carry one, and a catch (Exception) that returns false and quietly blinds the circuit breaker. Alongside them sits a clean control, a method that looks suspicious at a glance but is provably correct, there to catch reviewers that pattern-match instead of verify. Ground truth gets written down before each run, and every round goes on a fresh merge request so the reviewed diff contains only the seeded code.

The incumbent reviewer is [pi](https://pi.dev/docs/latest), a deliberately minimal coding agent, driving Sonnet 4.5 on Bedrock, with [reviewdog](https://github.com/reviewdog/reviewdog) handling everything GitLab-facing. Context comes from two files in the repo: an onboarding `AGENTS.md` covering project layout, build, and conventions (no defect hints), and a project-local code-review skill describing methodology, including one structural directive I added after watching earlier rounds guess at API semantics: the WireMock stubs are the only in-repo record of how the downstream API behaves, so verify parameter semantics there before asserting anything. With that setup, pi scored 5/5 on the seeded defects with zero false positives, at \$0.219 per review.

## The Swap: Holding Everything Else Still

Claude Code doesn’t read `AGENTS.md` or pi’s skill directory natively, so the bridges were minimal: a one-line `CLAUDE.md` importing `AGENTS.md`, and the skill mirrored into `.claude/skills/`. Everything else stayed fixed: the same review prompt, the same diff injection, the same Sonnet 4.5 profile, the same restricted tool set, the same transform and posting pipe. Each harness kept its own native system prompt, as the harness overhead is exactly what’s under test.

## The Reviewer That Ignored Its Own Instructions

Claude Code found all five seeded defects, and did so with exactly three tool calls: read the implementation, read the tests, write the findings file. The session log shows it discovered the code-review skill (the skill appears in the harness’s own command list) and never invoked it. The WireMock stubs were never opened.

The gaps were exactly the ones the skill exists to close. The false positive on the clean control came back, rated major, the same severity as the real bugs. The personal-data logging got downgraded to advisory and the exception swallowing to minor. Same model, same context sitting on disk; the difference is that pi’s model obeyed the skill’s “load this when reviewing” description while Claude Code buried it among twenty-six tools and a dozen commands and moved on. I was a bit surprised the gap turned out to be behavioural rather than architectural, as I’d assumed harness support for skills meant harness use of skills. I call it the *invocation gap*: both harnesses discover project skills, only one reliably acts on them unprompted.

## One Sentence of Forcing

The fix was a single sentence appended to Claude Code’s system prompt: “Before reviewing, invoke the code-review skill and follow its methodology.” Nothing else changed.

That run was the best of the whole series. The skill call came first, immediately followed by a sweep of the WireMock stubs, then the implementation and tests. All five defects found, plus the missing error-path test that most rounds only gesture at, zero false positives, and the sharpest severity calibration of any configuration: the contract bug and the personal-data logging both rated critical, the rest ranked sensibly beneath them.

The forced run cost \$0.359, some 64% more than pi for equivalent output. The reason sits in the token profile, as Claude Code re-reads roughly 37k tokens of its own harness context on every turn of a twelve-turn loop where pi carries about 4.6k. Depth is proportionally dearer on a heavy loop.

## The Slash Command That Skimmed

One more variant, as Claude Code offers a cheaper way to force a skill: pass it as a slash command, so the skill text is injected into the prompt deterministically instead of fetched by a model-chosen tool call. Faster (53 seconds against 76) and cheaper (\$0.275), and the false positive came straight back, rated major again. The trace shows why: a stub got read, just not the one that settles the flag’s semantics. When the skill arrived as prompt text the verification turned perfunctory; when the model deliberately pulled the skill in, the methodology got followed properly. I don’t fully understand the mechanism, and I’d want more than one run before leaning on it, but the pattern matches something I’ve noticed elsewhere: instructions a model chooses to fetch seem to bind tighter than instructions poured into its context.

## The Scoreboard

[Open the interactive chart on Datawrapper](https://datawrapper.dwcdn.net/Efor1/1/)

A few caveats. This is one merge request in one repository, with defects I seeded myself, so the numbers describe this benchmark and nothing grander. One seeded trap also escaped every configuration: a test that faithfully asserts the inverted flag’s wrong behaviour, encoding the bug as truth. Seven rounds, two harnesses, one model, zero catches. I suspect that one is a model limitation rather than a context problem, as no amount of harness discipline has touched it.

## What I’m Taking Into the Pilot

`pi` keeps the value crown: the same quality as the best Claude Code run at 60% of the price, with no forcing needed. Claude Code is entirely viable where it’s already the team’s standard tooling, provided the job prompt forces the skill invocation; unforced, it reads well and verifies nothing. Either way the absolute numbers are pennies against the per-review budget the business case allows, so the choice is about precision and trust rather than cost. A false positive rated major on a correct method is the comment that teaches developers to ignore the bot.

The larger lesson, for me, is about where the leverage lives. Across the whole series the ordering was: a conventions document in the repo bought the most, a verification directive (point at the authoritative source, don’t embed the facts) bought the next chunk, and the harness decided whether any of it actually got used. The model, which is where I started, never changed after week one.

Next step is the uncomfortable one: real merge requests, where nobody wrote the ground truth down first. I’m still deciding what good looks like when I can’t score recall. If you’re evaluating review bots yourself, hold the model still for one round and swap the loop around it; I’d be curious whether your harness picks up its own instructions.

## Glossary

- **Harness**: the agent loop around the model (tool dispatch, context assembly, system prompt, session management). pi and Claude Code are harnesses; Sonnet 4.5 is the model inside both.

- **Agent skill**: a markdown file of instructions (`SKILL.md`) a repo can carry, advertised to the agent by name and description, meant to be loaded when relevant.

- **AGENTS.md**: a convention for repo-level onboarding context aimed at coding agents.

- **Clean control**: a correct method planted in the diff to measure false positives.

- **Recall**: how many of the five seeded defects a run found.

- **reviewdog**: a CLI that posts structured findings as merge request comments, keeping the GitLab API out of the model’s hands.

## References

1. [pi coding agent](https://pi.dev/docs/latest)

2. [reviewdog](https://github.com/reviewdog/reviewdog)

3. [Claude Code](https://docs.claude.com/en/docs/claude-code/overview)
