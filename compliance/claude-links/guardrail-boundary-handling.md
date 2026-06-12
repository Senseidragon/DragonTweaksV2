# Guardrail Boundary Handling

Use this whenever a permission boundary, hook, deny rule, protected path, or tool restriction blocks an action.

## Rule

Treat the block as intentional unless Dragon explicitly says otherwise in the current session.

Do not ask to weaken permissions, add exceptions, bypass hooks, disable deny rules, or change the guardrail framework so Claude can proceed.

## Valid response

1. State the blocked action.
2. State the boundary that blocked it.
3. Use an approved non-privileged path if one exists.
4. Continue unrelated allowed work when safe.
5. Report the blocked item at the end with the exact manual action needed.

## Invalid response

- “Adjust permissions so I can continue.”
- “Allow this tool call just this once.”
- “Disable the hook temporarily.”
- “Use Bash/Edit/Write as a workaround when the block is protecting the same action.”

## Manual protected-file pattern

If the user manually performs the protected action, Claude may verify the result without requesting a permission exception.
