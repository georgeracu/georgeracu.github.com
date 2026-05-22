---
layout: post
title: "AI Agents That Remember: Building a Second Brain for Claude and Gemini"
date: 2026-04-12
tags: [post, development, ai, ai-agents, agent-memory, obsidian, gomi-bunrui]
description: "A practitioner's guide to solving the 'Goldfish Effect' in AI sessions through a simple, markdown-based memory protocol."
permalink: /blog/articles/ai-agents-that-remember/
mathjax: false
---

>Originally published on [Substack](https://georgeracu.substack.com/p/ai-agents-that-remember-building)

## The Problem: The Goldfish Effect

In my [previous post](/blog/articles/building-gomi-bunrui-part-1/) about building Gomi Bunrui, I named what I called "the unsolved problem": cross-tool context. Kiro didn't know what Claude Code had just implemented. Claude Code didn't know what Gemini had reviewed. I was the only thread connecting them, which meant I was also the bottleneck.

That observation was more significant than I'd realised at the time.

Gomi Bunrui is a spare-time project, built over roughly eight weeks while holding down a full-time job. The working pattern was irregular by necessity. Some weeks I worked on it every day. Other times, I wouldn't touch it for four or five days, then find twenty minutes before dinner and want to make some progress. Quick sessions, then dash out.

When you're working like that, and switching between Claude Code and Gemini CLI on the same codebase, the context problem grows. The repo for Gomi Bunrui ended up with three separate AI tool configurations: `.claude/` for Claude Code, `.gemini/` with three agents and twenty-one skills, and `.kiro/` with thirty-one numbered specs. Each tool had its own setup and none of them shared memory.

So I'd open a Claude Code session to work on the municipality-specific classification prompts, build up context around how Nagoya's waste categories differed from Kyoto's, make some decisions, then close the session. A few days later, I'd open Gemini to review the same area. I'd spend the first part of the session re-explaining what Claude and I had already worked through. Then the reverse when I went back to Claude.

I call this the Goldfish Effect as every session resets and every agent starts from zero. You end up doing the work twice: once with the AI, and once re-explaining it to the next AI.

Research from Gloria Mark at UC Irvine found that it takes an average of 23 minutes and 15 seconds to fully regain focus after a single interruption. Rebuilding conversational context with an AI tool isn't a simple interruption. The cost isn't just the minutes spent re-explaining; it's the decisions you misremember, the nuance you compress, and the momentum you never quite recover.

My workaround at the time was to copy-paste context between tools and try to keep each tool's remit clearly separate so that handover was minimal. It worked, up to a point. But it was friction, and it meant I was the human doing the remembering on behalf of tools that should have been able to manage it themselves.

Tools are fine, they're stateless by design, and each session is a blank slate. The problem is that we've been using them like disposable Post-it notes. We write something, use it once, and throw it away.

But what if the agent remembered?

Not in some vague "chat history" way, but through a real, structured, queryable memory: one the agent actively reads from at the start of every session and writes to as it works.

I've found that you can build exactly that, and you probably already have everything you need.

## The Insight: The Right Lever

The answer isn't inside the AI itself as you can't patch Claude to remember things it wasn't designed to retain. That's not the right lever.

The more effective lever is what you give the agent to _read from_ and _write to_.

Think about what a memory system actually needs to be: structured, searchable, persistent, and something you can audit and correct. It doesn't need to be a database, nor an API, nor live in the cloud. It simply needs to be a folder of well-organised text files.

If you use Obsidian, or any markdown-based note-taking app, you already have the perfect foundation:

- Human-readable files you can open, review, and edit yourself.
- A folder structure that maps naturally to different types of memory.
- Rich metadata via properties: the YAML block at the top of each note.
- A searchable, queryable knowledge base that any agent can read.

The missing piece wasn't infrastructure: it was a _protocol_.

That's where `AGENTS.md` comes in. It's one file, at the root of your vault. Every agent, being Claude, Gemini, or whatever you prefer to use, reads it at the start of every session. It tells them exactly how to load context, how to write new memories, and how to leave the vault in a better state than they found it.

One file, every agent, same protocol.

## The Stack

This is what I used to build this system:

| Component          | Role                                                                   |
| ------------------ | ---------------------------------------------------------------------- |
| **Obsidian**       | The memory store: a vault of markdown files organised by memory type.  |
| **`AGENTS.md`**    | The protocol: one file every agent reads at session start.             |
| **`obsidian-cli`** | The write interface: how agents create and update memory entries.      |
| **`qmd`**          | The recall engine: how agents search memory at the start of a session. |

Two of these need a quick introduction if you haven't used them before.

**obsidian-cli** is a command-line companion that ships with the Obsidian app. It lets any program, including an AI agent, read, create, and update notes in a running Obsidian instance. On macOS, you expose it with a single symlink and from that point any agent with terminal access can write directly into your vault. It requires Obsidian to be open, which means your memory store is always a living part of your workflow rather than a separate system.

**qmd** is a local search engine for your markdown files. It supports both keyword search and semantic search, meaning you can find notes by meaning even if the wording doesn't match exactly. Everything runs on your machine: no cloud, no subscription.

The memories themselves are plain markdown files, each with a small block of structured metadata at the top: the note's type, date, a one-line summary, and a status. I've found that five types cover almost everything:

- **context**: active project state and working assumptions.
- **decision**: choices made that future agents should respect.
- **mistake**: pitfalls encountered and how to avoid them.
- **pattern**: reusable approaches that worked well.
- **snapshot**: full working memory checkpoint for session continuity.

Any agent that can read a markdown file can use this system, that's the whole point.

## How It Works in Practice

Let me walk through what a session looks like once this is in place, using Gomi Bunrui as the example.

**Starting a session**

The agent opens `AGENTS.md` first. The protocol says so, and the agent follows it. From there, it uses `qmd` to search for anything relevant to the current task.

Say I'm coming back to Gomi Bunrui after four days away. I want to continue refining the municipality-specific classification prompts, specifically the rules for Kyoto, which handles waste categories differently from Nagoya. The agent searches, finds a `decision` entry recording how we structured the city-specific prompt sections, and a `snapshot` from the last session explaining exactly where things were left off: which prompts were updated, what still needed testing, and a note about an edge case in how Kyoto handles PET bottle caps.

The agent reads both. It now has context that would have taken fifteen minutes to reconstruct manually, and that I might have reconstructed inaccurately after four days away.

**During the session**

As the agent works, it captures things worth keeping. Perhaps it discovers that a particular way of phrasing the waste category disambiguation rules produces more reliable classifications. Rather than letting that observation disappear at the end of the session, it writes a `pattern` entry:

```bash
obsidian vault="My Vault" create \\
  path="agent-memory/patterns/2026-04-10-prompt-disambiguation.md" \\
  content="---\
type: pattern\
date: 2026-04-10\
tags: [agent-memory, pattern]\
status: active\
summary: Placing the item type before the category in prompts improves Kyoto disambiguation accuracy\
---\
\
..." \\
  silent
```

One command and the entry is in the vault as that knowledge belongs to the memory store now.

The next session, whether I open Claude Code or Gemini CLI, that pattern will be found.

**Ending the session**

Before stopping, the agent writes a `snapshot`: a structured handover document covering what was in progress, what was completed, what comes next, and which files were touched. The next agent reads it and starts with full context. It doesn't matter whether that agent is Claude or Gemini, they both read `AGENTS.md`, they both write to the same store, and the handover is just a file.

**The memory view**

In Obsidian, a `memory.base` file gives you a live table of all active memories, grouped by type. This uses Obsidian Bases, available in v1.9 and later. If you're on an older version, `qmd search "agent-memory"` gives you the same recall without the visual dashboard.

The system stays current on its own: you add an entry and it appears; you mark one as stale and it disappears. There's no index to maintain.

## Setting It Up

The whole thing is perhaps smaller than you'd expect.

**What you need**

- Obsidian v1.9+ (Bases requires v1.9; enable it under Settings > Core Plugins > Bases).
- obsidian-cli: ships with the Obsidian app. On macOS, add it to your PATH with one symlink:

  ```bash
  ln -s /Applications/Obsidian.app/Contents/MacOS/obsidian /usr/local/bin/obsidian
  ```

- qmd: `npm install -g @tobilu/qmd`, then `qmd collection add ~/path/to/vault --name <your-collection-name> && qmd embed` to index your notes. Note: `qmd embed` downloads around 2 GB of models on first run.

_Links to both tools and their full setup guides are at the end of this post._

**The fast path: use the template**

Rather than creating all of this by hand, I've put together a ready-to-clone vault template on GitHub — [georgeracu/obsidian-agent-vault](https://github.com/georgeracu/obsidian-agent-vault). It includes the folder structure, the `.base` files, the `AGENTS.md` protocol, and the agent skills pre-installed. Two find-and-replace steps (your vault name and your qmd collection name) and it's ready to go.

The manual steps below explain what each piece does and why — useful whether you're using the template or building from scratch.

**What to create**

**1. `AGENTS.md` at the root of your vault**

This is the most important file. It must be self-contained: any agent reading it cold should know exactly what to do. It covers how to load context at session start, when and how to write each memory type, what a snapshot must include, and how to flag entries for pruning. It doesn't need to be long, it has to be complete.

**2. Five subdirectories under `agent-memory/`**

```bash
agent-memory/
├── context/
├── decisions/
├── mistakes/
├── patterns/
└── snapshots/
```

**3. A consistent metadata block for every entry**

Every memory file needs this at the top:

```yaml
---
type: context|decision|mistake|pattern|snapshot
date: 2026-04-10
tags: [agent-memory, context]
status: active
summary: One-line description of this entry
---
```

The `summary` field is what surfaces in searches and the Bases view. Keep it specific.

**4. `memory.base`**: an Obsidian Bases file that live-queries all entries where `status` is `active`, grouped by type. No index to maintain.

**5. `stale.base`**: the pruning queue. When an entry is outdated, an agent sets its status to `stale`. That entry disappears from `memory.base` and appears here instead. You review it and delete what you approve. Agents propose; you dispose.

That's the entire system. A handful of markdown files, two command-line tools, no database, no API, no cloud dependency. It runs entirely inside your vault.

If you want a step-by-step walkthrough with copy-paste commands for every file, there is a dedicated follow-up post that covers the full setup end-to-end: [Building the Agent Memory System: The Setup Guide](/blog/articles/building-agent-memory-system/).

## What This Unlocks

In the Gomi Bunrui post, I described cross-tool context as "the unsolved problem." I was right that it was a problem. I was wrong that it was unsolved.

My workaround at the time was to copy-paste context manually and keep each tool's scope narrow enough that the gaps didn't matter too much. That approach has a ceiling — it scales with how much effort I'm willing to put into being the shared memory between tools. The agent memory system removes that ceiling.

Once agents have a place to remember, a few things change:

- **Sessions compound.** Later sessions are genuinely smarter than earlier ones as patterns accumulate and decisions stick. Those twenty-minute sessions between other commitments become genuinely productive, because the agent starts where you left off rather than where it always starts.
- **Agents become interchangeable.** Claude Code starts a piece of work and writes a snapshot; Gemini CLI picks it up next session. They don't need to know about each other — they both read `AGENTS.md`, they both write to the same store, and the handover is just a file.
- **You stay in control.** Agents flag outdated entries as stale; you decide what gets deleted. The memory store doesn't grow unchecked. You can open it in Obsidian, browse it like any other set of notes, edit entries by hand, and prune anything that no longer serves you.
- **Your vault gets smarter over time.** The `agent-memory/` folder gradually becomes a knowledge base about how _you_ work: your patterns, your decisions, your known pitfalls. That's something that has to be built — and it'll be built with every session when you use this protocol.

The tools were always there. The only thing missing was a protocol.

---

## What Can You Do Now?

You don't need to build this all at once. If your workflow looks anything like mine — irregular hours, multiple tools, projects you return to days later — this is worth an afternoon.

**Step 1 — Install the tools** (15 minutes)
Install `obsidian-cli` and `qmd` via npm. Index your vault with `qmd`. Confirm both are working before writing a single memory file.

**Step 2 — Write `AGENTS.md`** (30 minutes)
One file at your vault root. Define session start behaviour, the five memory types, snapshot format, and how to flag stale entries. It doesn't need to be long — it needs to be complete. Any agent reading it cold should know exactly what to do.

I won't pretend my first version was clean — it took a few sessions to find the right level of detail. But even an imperfect protocol outperforms no protocol at all.

**Step 3 — Create the folder structure and schema** (10 minutes)
Five directories under `agent-memory/`. Add the frontmatter template to your note-taking app as a snippet so new entries take seconds, not minutes.

**Step 4 — Run one session end-to-end** (your next AI session)
Open a session, let the agent search for context, work on something real, and close by writing a snapshot. That snapshot is your first proof that the system works.

Everything after that starts compounding and builds on previous work: the Bases views, the pruning workflow, the pattern library.

The whole setup fits in an afternoon and the compounding starts immediately.

---

## Glossary

**AGENTS.md** — A file placed at the root of your vault that functions as the shared protocol for all agents. Any AI tool that reads it knows how to load context, write memory, and hand off to the next session.

**agent-memory** — The folder structure that holds all memory entries. Organised into five subdirectories by type: context, decisions, mistakes, patterns, and snapshots.

**Obsidian Bases** — A feature in Obsidian v1.9 and later that lets you create live, queryable table views of your notes using filter and grouping rules. Used here to maintain a real-time dashboard of active memory entries.

**obsidian-cli** — A command-line tool that connects to a running Obsidian instance and allows external programmes (including AI agents) to create, read, and update notes via terminal commands.

**qmd** — A local, offline search tool for markdown files that supports both keyword and semantic (meaning-based) queries. Used by agents to retrieve relevant memory entries at session start.

**snapshot** — A structured memory entry written at the end of a session. Records in-progress work, completed tasks, next steps, and files touched, so the next session (or the next agent) can start with full context.

**stale** — A status value applied to memory entries that are no longer current. Agents set entries to `stale` rather than deleting them directly; you review and remove them via the pruning view.

---

## References

1. Gloria Mark, UC Irvine — _The Cost of Interrupted Work: More Speed and Stress_ (2008). Average time to regain focus after interruption: 23 minutes 15 seconds.
2. [georgeracu/obsidian-agent-vault](https://github.com/georgeracu/obsidian-agent-vault) — The ready-to-clone vault template described in this post.
3. [qmd on npm](https://www.npmjs.com/package/@tobilu/qmd) — Local semantic and keyword search for markdown collections.
4. [Obsidian](https://obsidian.md) — The markdown-based knowledge base used as the memory store.
5. [Obsidian Bases documentation](https://help.obsidian.md/bases) — Live database views for Obsidian vaults (v1.8+).

---

_Next post: [Building the Agent Memory System: The Setup Guide](/blog/articles/building-agent-memory-system/)_

_This post is part of a series about building with AI tools. [Read Part 1: I Used 8 AI Tools to Build and Ship a Product Solo](/blog/articles/building-gomi-bunrui-part-1) for the full picture._

_Follow the series: [Substack](https://georgeracu.substack.com/) · [Dev.to](https://dev.to/george3421) · [LinkedIn](https://www.linkedin.com/in/george-racu-70670443) · [X](https://x.com/georgeracu)_
