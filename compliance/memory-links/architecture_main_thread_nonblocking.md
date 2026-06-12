# Main-Thread Nonblocking Invariant

Source: [[none]]

Nothing blocks the Minecraft main/client/render thread. Ever.

LLM/OpenRouter work must be failure-isolated from gameplay and rendering.

Forbidden on Minecraft threads:

- network I/O
- synchronous HTTP
- file I/O
- sleeps
- joins
- blocking futures
- lock waits
- API-key validation
- model discovery
- LLM response generation
- external process calls

Putting blocking work in another thread is not proof of compliance if the Minecraft thread can wait on it directly or indirectly.
