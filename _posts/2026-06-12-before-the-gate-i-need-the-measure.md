---
layout: post
title: "Before The Gate I Need The Measure"
date: 2026-06-12
tags: [post, ai-agents, agent-harness, agent-protocols, claude-code]
description: "In the previous post I committed publicly to wire a PreToolUse deny hook for the AGENTS.md routing rule. This time I wired a counter instead, because enforcement without measurement is a bit reckless "
permalink: /blog/articles/before-the-gate-i-need-the-measure/
canonical_url: https://georgeracu.substack.com/p/before-the-gate-i-need-the-measure
mathjax: false
---

> Originally published on [Substack](https://georgeracu.substack.com/p/before-the-gate-i-need-the-measure) on 12 June 2026.

## Before the Gate, the Counter

The previous post argued that `AGENTS.md` loses to a Bash reflex. The thesis came from the *Dive into Claude Code* paper: text-shaped rules can’t beat a trained tool-dispatch habit, and the only thing that holds is a denial at the harness layer. I ended that post with a public commitment to wire a PreToolUse hook against my own vault within the week, because I’d been finding raw `sed -i` calls in places they shouldn’t be. \[1\] \[2\]

This is the follow-through, and the follow-through is a bit awkward, as the hook I actually wired is not the one I promised. I built a counter instead.

### The Promise And The Pivot

The original plan was a `PreToolUse` deny hook for the `AGENTS.md` routing rule. Catch raw bash file commands targeted at vault paths, return a denial, force the agent to reach for the obsidian CLI it was supposed to be using anyway. Twenty lines of shell, one regex, done (if you get the regex right).

I started writing the regex and stopped about ten minutes in. The thing I couldn’t answer was which rules deserved a gate. `AGENTS.md` has a dozen pieces of guidance in it, and the bash routing rule was the noisiest one because it had bitten me the previous week, but noisiest is not the same as worst. Some of the rules in there might already be working fine, and a deny hook for a working rule is just noise. Some might be ignored so completely that a deny hook would block the agent into a corner. I had no idea which was which, as I had no data.

The initial version of “I’ll wire the gate” was “I’ll wire the gate without knowing what to gate”. That seemed worse than waiting.

### What I Actually Did

The counter is a PostToolUse hook with a `Skill` matcher, firing every time the agent invokes a Skill, capturing the call, and appending one JSON line to a log inside the vault. Each line records the skill name, the timestamp, the session ID, the duration, the success flag, and the working directory. Just under fifty lines of bash and Python, parsing the payload Claude Code hands the hook on stdin and writing it out to `.agents/usage-analytics/tool_calls.jsonl`.

The hook reads, the hook records, the hook doesn’t block. Pure observation, the kind of measurement you put in before deciding what to enforce.

I borrowed the structure from another repo, where the same pattern logs Skill calls for a separate project. That code had been running for weeks and produced exactly the data I wished I had: which skills were invoked, how often, which sessions ignored them entirely (and which harness was using the skill). I copied the pattern, pointed the log path at my vault, and let it work. I used only the hook for Claude Code but soon I will hook Gemini CLI as well.

### Wiring the PostToolUse: A Matcher and a Script

The whole thing is two files. Registration goes in `.claude/settings.json`, which tells Claude Code when to fire the hook:

    {
      "hooks": {
        "PostToolUse": [
          {
            "matcher": "Skill",
            "hooks": [
              { "type": "command", "command": "bash .claude/hooks/capture-tool-call.sh" }
            ]
          }
        ]
      }
    }

The matcher is where the scope decision lives. `Skill` fires on Skill invocations and nothing else, and swapping it for `Bash` would point the counter at the other half of the question, the reflex side, when I’m ready to ask it.

The script is the second file, bash on the outside and Python on the inside. The bash layer is plumbing: capture the payload Claude Code pipes in on stdin, make sure the log directory exists, hand the JSON over, and never fail.

    VAULT_ROOT="$HOME/path/to/your/vault"
    LOG_FILE="$VAULT_ROOT/.agents/usage-analytics/tool_calls.jsonl"
    mkdir -p "$(dirname "$LOG_FILE")"

    PAYLOAD=$(cat)
    printf '%s' "$PAYLOAD" | python3 -c "$PY_SCRIPT" >> "$LOG_FILE"

    exit 0

The Python part builds one event per invocation:

    d = json.loads(sys.stdin.read())
    tool_input = d.get("tool_input") or {}
    resp = d.get("tool_response") or {}
    tool_name = d.get("tool_name")

    event = {
        "ts": time.strftime("%Y-%m-%dT%H:%M:%S%z"),
        "session_id": d.get("session_id"),
        "tool": tool_name,
        "tool_label": tool_input.get("skill") if tool_name == "Skill" else None,
        "duration_ms": d.get("duration_ms"),
        "success": resp.get("success") if isinstance(resp, dict) else None,
        "cwd": rel_home(d.get("cwd")),
    }
    print(json.dumps(event))

Everything sits inside a try that prints a `parse_error` line instead of raising, and the bash ends with `exit 0` regardless, as a counter that breaks the agent when the payload surprises it would be enforcement by accident, the exact thing I decided not to ship. The `rel_home` helper swaps my home directory for a tilde before the working directory lands in the log, as the vault syncs through a cloud drive and the log doesn’t need my username on every line.

A real line from the first days of recording, trimmed for width:

    {"ts": "2026-06-12T13:27:32+0900", "session_id": "446e0acd...", "tool": "Skill", "tool_label": "qmd", "duration_ms": 11, "success": true}

### The Question The Counter Answers

What I want to know is the gap between AGENTS.md and the harness. Skills registered through `.agents/skills/` are values: routing suggestions loaded at session start, hoping the agent will reach for them. The harness has its own defaults, a built-in tool dispatcher with strong opinions about how to read a file, run a search, write text. When those two disagree, the harness almost always wins, because the harness is where execution actually happens.

The counter measures that disagreement: every Skill invocation is a vote for the registered routing, every session with zero Skill invocations on a task that obviously called for one is a vote for the harness reflex. The ratio tells me where AGENTS.md is working and where it’s wallpaper.

I’d guess, before any data comes in, that the writing skills get invoked most often, as they’re explicitly named in my Substack workflow. I’d guess the obsidian-cli skill is almost never invoked despite my AGENTS.md saying it should gate every vault read, because the agent goes straight to the obsidian binary by reflex rather than loading the skill that documents it. Whether those guesses hold or not, the counter will tell me within a week or two.

### What I Got Wrong The First Time

The first iteration of the hook used a relative path for the log file. That works in my other repo as the project root is always the cwd, but in my vault the agent occasionally runs from a subfolder, and the relative path would have scattered logs into wherever the agent happened to be. I switched to the absolute vault path.

The other thing I caught late was that the hook only fires for the Skill tool, not for arbitrary bash calls. That’s the right scope for what I’m trying to measure (am I using my own skills?), but it leaves me blind to the side of the question I asked in the previous post (is the bash reflex still firing?). For that I’d need either a second hook on the `Bash` matcher, or a different log filter. One question at a time.

### The Argument I’d Rather Make

The previous post made an architectural argument: AGENTS.md doesn’t enforce, hooks do. The argument still holds. What I’d add now is that enforcement without measurement is a bit reckless. The gate stops behaviour you’ve decided is wrong. Deciding what’s wrong is a separate problem, and it needs evidence the AGENTS.md author probably doesn’t have.

So the order matters: counter first, gate second. The counter answers what to gate, and the gate then enforces what the counter found.

I’ll publish the numbers when they come in. The hook started recording today; the first interesting question is which skills hit zero invocations across a typical week, as those are either dead rules or rules being routed around. Either answer is useful.

### Glossary

- **PreToolUse hook:** A user-defined script that runs before the agent harness dispatches a tool call. Can allow, modify, or deny the call.

- **PostToolUse hook:** Same plumbing, but fires after the call has completed. Can record, react, or trigger follow-up work; cannot un-do the call.

- **Matcher:** The filter that decides which tool calls a hook fires on. `Skill` only fires on Skill invocations; `Bash` only fires on bash calls.

- **Harness:** The orchestration layer around the model. The model emits suggested tool calls; the harness validates, applies permissions, and dispatches.

- **AGENTS.md:** A markdown file loaded as agent context at session start. Used to communicate rules, conventions, and project values to the model.

### References

\[1\] *Why Your AGENTS.md Lost to a Bash Reflex*,

[Why My AGENTS.md Lost to a Bash Reflex](/blog/articles/why-my-agents-md-lost-to-a-bash-reflex/)

[Why My AGENTS.md Lost to a Bash Reflex](/blog/articles/why-my-agents-md-lost-to-a-bash-reflex/)  
\[2\] Liu, J., Zhao, X., Shang, X., & Shen, Z. (2026). *Dive into Claude Code: The Design Space of Today’s and Future AI Agent Systems.* arXiv:2604.14228v1.
