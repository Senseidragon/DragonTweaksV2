# Guardrail Boundary Handling

Source: [[none]]

Permission blocks, deny rules, hooks, and protected paths are normally intentional boundaries.

Do not suggest weakening permissions, adding exceptions, bypassing hooks, disabling deny rules, or changing guardrails so Claude can proceed.

When blocked, use an approved non-privileged path if available. Otherwise report the blocked action and the manual step Dragon can choose to perform.

Do not treat Bash, Edit, or Write as interchangeable bypass tools when the same protected action is being blocked.
