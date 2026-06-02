**Title:** NeoForge — concrete main-thread blocking hazard patterns to scan for
**Type:** fact
**Intent triggers:** main thread safety, server thread, blocking, Thread.sleep, Future.get, CompletableFuture.join, CountDownLatch, BlockingQueue, synchronized, tick handler, event handler, file IO, network IO, infinite loop, audit, scan pattern

## Blocking Patterns to Flag

These calls are suspicious anywhere in the codebase, but HIGH severity when found in tick handlers, event handlers, or server-thread contexts:

- `Thread.sleep(`
- `Future.get(`
- `CompletableFuture.join(`
- `CountDownLatch.await(`
- `BlockingQueue.take(`
- File I/O in hot paths (reading/writing files synchronously)
- Network I/O in hot paths
- Infinite loops without yield
- `synchronized` blocks in tick/event/server-thread contexts — risky if the lock is contended

## Context Escalation Rule

Severity escalates to HIGH when any of the above appear inside methods or classes that are identifiably part of:
- Tick handlers (`tickServer`, `tick`, `onTick`, etc.)
- Forge/NeoForge event subscribers (`@SubscribeEvent`)
- Server lifecycle event handlers
- Any context where the call stack is owned by the Minecraft main or server thread

## Relationship to Project Invariant

This list supports enforcement of the standing invariant: nothing may block the Minecraft main/server thread, no exceptions. These are the concrete patterns that violate it.
