# Main-Thread Nonblocking and LLM Isolation

## Absolute invariant

Nothing blocks the Minecraft main/client/render thread. Ever.

## Forbidden on Minecraft threads

- network I/O
- synchronous HTTP
- filesystem I/O
- sleeps
- joins
- blocking futures
- `Future.get()`
- `CompletableFuture.join()`
- lock waits
- API-key validation
- model discovery
- LLM request/response generation
- expensive scans or indexing
- external process calls

Putting blocking work in another thread is not proof of compliance. The Minecraft thread must not wait for it directly or indirectly.

## Required shape

Minecraft thread emits a request or event only.

A dedicated LLM executor owns network work.

Minecraft receives completed results later through a safe queue/callback on the correct game thread boundary.

## Failure isolation

The game must remain interactive if:

- OpenRouter is slow
- OpenRouter is offline
- API key validation fails
- model discovery hangs
- request timeout occurs
- the LLM worker queue is saturated
