---
layout: post
title: "Building the Agent Memory System: The Setup Guide"
date: 2026-04-17
tags: [post, development, ai, ai-agents, agent-memory, obsidian, gomi-bunrui]
description: "A copy-paste setup guide for the agent memory protocol: from zero to a working memory system in an afternoon, with two paths: clone the template or extend an existing Obsidian vault."
permalink: /blog/articles/building-agent-memory-system/
mathjax: false
---

_This is part of an ongoing series about building [Gomi Bunrui](https://gomi-bunrui.app/app/), an AI-powered waste classification app for Japan, using AI tools at every layer of the stack. [Start with Part 1](/blog/articles/building-gomi-bunrui-part-1)._

---

>Originally published on [Substack](https://georgeracu.substack.com/p/building-the-agent-memory-system)

## The Starting Point: From Zero to Memory

In the [previous post](/blog/articles/ai-agents-that-remember/), I described the Goldfish Effect: every AI session resets, every agent starts from zero, and you end up doing the work twice. To fix this, I created a structured memory system my agents read from and write to: it's built on top of Obsidian, two CLI tools and several agent skills. Let me show you how I did it.

By the end of this post you'll have a working memory system: `AGENTS.md` at the root of your vault, five memory directories, `qmd` indexed and ready to search, and one complete session run end-to-end to prove it works.

There are two ways to get there:

- **The Fast Path**: clone the template from GitHub, two find-and-replace steps, done in under 30 minutes.
- **The Manual Path**: extend your existing vault by following the steps below: they're useful if you already use Obsidian and want to extend what you have rather than starting fresh.

Either way, the result is the same, only the journey is different.

## The Prerequisites: What You'll Need First

A few things before any commands:

**Obsidian v1.9 or later.** Bases, the live dashboard that surfaces your memories, requires v1.9. Once you've upgraded, enable it under Settings > Core Plugins > Bases.

**obsidian-cli.** This ships with the Obsidian app: there's nothing to install separately. On macOS, you enable this in the UI.

**qmd.** A local, offline search engine for your markdown files:

```shell
npm install -g @tobilu/qmd
```

Before moving on, verify both are reachable:

```shell
obsidian --version
qmd --version
```

One note on `qmd embed`, which you'll run shortly: it downloads around 2 GB of embedding models on first run. Run it when you're not on mobile data and you have a stable Internet connection. Under the hoods, `qmd` uses `node-llama-cpp` to run the embedding models and downloads them from HuggingFace.

## The Faster Path: Clone the Template

If you're starting from a fresh vault, this is the quickest route. I've published a ready-to-clone template at [georgeracu/obsidian-agent-vault](https://github.com/georgeracu/obsidian-agent-vault) that includes everything: the folder structure, `AGENTS.md`, the `.base` files, and several agent skills pre-installed.

- **Step 1.** Click "Use this template" on GitHub, then clone your new repo.
- **Step 2.** Open the cloned folder as a vault in Obsidian: File > Open folder as vault.
- **Step 3.** Enable Bases if it isn't already: Settings > Core Plugins > Bases.
- **Step 4.** Enable the obsidian-cli from settings (see Prerequisites above).
- **Step 5.** Index the vault with `qmd`. Pick a short name for your collection: I use `george-vault`:

```shell
qmd collection add /path/to/your/vault --name <your-collection-name>
qmd embed
```

- **Step 6.** Find and replace two placeholders across `AGENTS.md` and `Resources/AI & Agents/agent-memory-tutorial.md`:
  - `<your-vault-name>`: The folder name you opened in Step 2
  - `<your-collection-name>`: The name you chose in Step 5

That's it. Verify the setup with two checks:

- Open `agent-memory/memory.base` in Obsidian. It should render as an empty table with columns for Summary, Type, and Date.
- Run `qmd search "agent" -c <your-collection-name>`. It should return results from the vault.

If both pass, you're ready for your first session.

## The Manual Path: Extending Your Existing Vault

If you already use Obsidian and don't want to migrate to a new vault, you can add the memory system to what you have. It's five steps.

**Create the folder structure**

```shell
mkdir -p agent-memory/{context,decisions,mistakes,patterns,snapshots}
touch agent-memory/{context,decisions,mistakes,patterns,snapshots}/.gitkeep
```

**Write AGENTS.md**

This is the most important file. Place it at the root of your vault. It's got to be self-contained: any agent reading it cold should know exactly how to load context, write new memories, and leave the vault in a better state than it found it.

I won't reproduce the full file here: the template version is the best reference. What it must cover:

- Session start: search `agent-memory/` with `qmd`, read matching entries, check `snapshots/` if resuming work
- The five memory types and when to write each (context, decision, mistake, pattern, snapshot)
- The snapshot format: Task State, Conversation Summary, Files Touched
- Pruning: agents set `status: stale`, they never delete directly

The test I use: if a new AI tool I'd never configured read only this file, would it know what to do? If yes, it's complete enough.

**Create memory.base and stale.base**

Two files at the root of `agent-memory/`. These are Obsidian Bases files: they auto-query your entries without any index to maintain.

`agent-memory/memory.base`:

```yaml
filters:
  and:
    - file.inFolder("agent-memory")
    - file.ext == "md"
    - status == "active"
properties:
  summary:
    displayName: Summary
  type:
    displayName: Type
  date:
    displayName: Date
  tags:
    displayName: Tags
views:
  - type: table
    name: All Memories
    groupBy:
      property: type
      direction: ASC
    order:
      - summary
      - type
      - date
      - tags
```

`agent-memory/stale.base`:

```yaml
filters:
  and:
    - file.inFolder("agent-memory")
    - file.ext == "md"
    - status == "stale"
properties:
  summary:
    displayName: Summary
  type:
    displayName: Type
  date:
    displayName: Date
views:
  - type: table
    name: Pruning Queue
    order:
      - summary
      - type
      - date
```

**Index the vault**

```shell
qmd collection add ~/path/to/your/vault --name <your-collection-name>
qmd embed
```

**Wire up your AI tools**

For Claude Code, one line in `CLAUDE.md` at the vault root:

```shell
Always load AGENTS.md
```

For Gemini CLI, the same in `GEMINI.md`. Both resolve to the same protocol: that's the whole point of `AGENTS.md`.

## The Maiden Voyage: Your First Session

The system starts empty and that's fine. Try to run a first session and see it working.

Open a new AI session. Before doing anything else, run:

```shell
qmd search "your project or task" -c <your-collection-name>
```

No results. Expected: the memory store only knows what you've put into it. This is the last time you'll start from nothing.

**Write a context entry.** Capture where things stand right now. Say you're picking up a side project after a few days away:

```shell
obsidian vault="<your-vault-name>" create \
  path="agent-memory/context/2026-04-11-project-state.md" \
  content="---\ntype: context\ndate: 2026-04-11\ntags: [agent-memory, context]\nstatus: active\nsummary: Resuming auth refactor, with JWT middleware extracted, session store still needs updating\n---\n\n## Current State\n\nJWT middleware extracted to src/middleware/auth.ts. Session store still uses legacy cookie approach, needs migrating to the new token format before the auth flow is testable end-to-end.\n\n## Relevant Files\n\n- src/middleware/auth.ts: new middleware, in progress\n- src/store/session.ts: needs updating" \
  silent
```

**Make and capture a decision.** During the session, you decide something worth preserving:

```shell
obsidian vault="<your-vault-name>" create \
  path="agent-memory/decisions/2026-04-11-session-store-approach.md" \
  content="---\ntype: decision\ndate: 2026-04-11\ntags: [agent-memory, decision]\nstatus: active\nsummary: Use httpOnly cookies for session tokens, not localStorage\n---\n\n## Decision\n\nSession tokens stored in httpOnly cookies, not localStorage.\n\n## Why\n\nLocalStorage is accessible to JavaScript and vulnerable to XSS. httpOnly cookies are not.\n\n## Applies to\n\nAny future work on session handling or auth token storage." \
  silent
```

**Write a snapshot before stopping:**

```shell
obsidian vault="<your-vault-name>" create \
  path="agent-memory/snapshots/2026-04-11-session-end.md" \
  content="---\ntype: snapshot\ndate: 2026-04-11\ntags: [agent-memory, snapshot]\nstatus: active\nsummary: Auth refactor session: JWT middleware done, session store in progress\n---\n\n## Task State\n\n- **Done:** JWT middleware extracted to src/middleware/auth.ts\n- **In progress:** Session store migration to new token format\n- **Next:** Update src/store/session.ts, then test the full auth flow end-to-end\n\n## Conversation Summary\n\nRefactored auth middleware. Decided on httpOnly cookies for session token storage. Session store not yet updated: that's the next piece before the flow can be tested.\n\n## Files Touched\n\n- src/middleware/auth.ts: created\n- src/store/session.ts: read, not yet modified" \
  silent
```

Now open `agent-memory/memory.base` in Obsidian. Three entries should appear, grouped by type: one context, one decision, one snapshot.

That's the system working. The next session, whether you open Claude Code, Gemini CLI, or anything else that reads `AGENTS.md`, 'll find those entries and start with context instead of from zero.

## The Maintenance: Keeping It Healthy

The system doesn't need much maintenance, but four habits keep it from getting cluttered. I won't pretend it's perfect; it took a few sessions to find the right level of detail that didn't overwhelm the agent while still giving me everything I needed to resume work.

**Flag, don't delete.** When an entry is outdated, mark it stale rather than removing it:

```shell
obsidian vault="<your-vault-name>" property:set name="status" value="stale" file="<entry-name>"
```

It disappears from `memory.base` and appears in `stale.base` instead. Open `stale.base` in Obsidian, review the queue, and delete the files you're happy to remove. Agents propose what's stale; you decide what goes.

**Write summaries that search well.** The `summary` field is how both `qmd` and the Bases view surface entries. "Updated context" tells you nothing three weeks later. "JWT middleware extracted, session store still needs updating" tells you exactly where things stood. Every time you write an entry, ask: would this summary find itself if I searched for it by topic?

**Snapshot before stopping.** This is the highest-value habit. An empty `snapshots/` folder means every session starts from scratch. One snapshot from the previous session means the next session starts with full context. It takes two minutes and compounds immediately.

**Automate the ritual with a Wrap-up skill.** Once the protocol is stable, you can give your agent a specific `wrap-up` skill that handles the closing ritual for you. It writes the snapshot, searches for and marks interim snapshots as stale, and runs the `qmd update` command to refresh your search index. It ensures every session ends with a clean handoff and an updated index without you having to prompt for it manually.

Six months in, `agent-memory/` becomes something more than a memory store. It's a record of how you think and work: the decisions that shaped a project, the patterns that took sessions to discover, the mistakes you've stopped making. That's not something you can recreate from git history or commit messages. It has to be built, and it builds itself, one session at a time.

---

## The Next Steps: Starting Your Own Memory Store

You don't need to do this all at once. If your workflow looks anything like mine, the minimum viable version is simpler than it looks:

- **Today (30 minutes)**: Install the tools, clone the template or create the folder structure, and add the symlink.
- **This week (your next AI session)**: Write one context entry at the start. Write one snapshot at the end. Open `memory.base` and confirm they appear.
- **Ongoing**: Let the system grow with your work. Flag entries when they're outdated. Write good summaries. Snapshot before stopping.

Be consistent and trust the system. When not getting the right result, adjust.

---

## Glossary

**AGENTS.md**: The shared protocol file at the root of your vault. Every agent reads it at the start of every session. One file, every agent, same protocol.

**obsidian-cli**: The command-line interface that ships with the Obsidian app. Agents use it to create and update memory entries without raw file I/O.

**qmd**: A local search engine for markdown files. Supports keyword and semantic search. Runs entirely on your machine.

**memory.base**: An Obsidian Bases file that live-queries all `status: active` entries in `agent-memory/`, grouped by type. No index to maintain.

**stale.base**: The pruning queue. Shows all entries with `status: stale` for human review.

**wrap-up skill**: A custom agent skill that automates the session-end ritual. It handles writing the final snapshot, marking interim snapshots as stale, and refreshing the search index.

---

## References

1. [georgeracu/obsidian-agent-vault](https://github.com/georgeracu/obsidian-agent-vault): The ready-to-clone vault template.
2. [qmd on npm](https://www.npmjs.com/package/@tobilu/qmd): Local semantic and keyword search for markdown collections.
3. [Obsidian](https://obsidian.md): The markdown-based knowledge base used as the memory store.
4. [Obsidian Bases documentation](https://help.obsidian.md/bases): Live database views for Obsidian vaults (v1.9+).

---

_Previous post: [AI Agents That Remember: Building a Second Brain for Claude and Gemini](/blog/articles/ai-agents-that-remember/)_

_Follow the series: [Substack](https://georgeracu.substack.com/) · [Dev.to](https://dev.to/george3421) · [LinkedIn](https://www.linkedin.com/in/george-racu-70670443) · [X](https://x.com/georgeracu)_
