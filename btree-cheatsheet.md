# gdx-ai `.btree` Format Cheatsheet

## documentation 
https://github.com/libgdx/gdx-ai/wiki/Behavior-Trees

## Directives

| Keyword | Syntax | Description |
|---|---|---|
| `import` | `import alias:"fully.qualified.Class"` | Register a user task under a short alias |
| `subtree` | `subtree name:"treeName"` | Declare a named internal sub-tree (macro) |
| `root` | `root` | Entry point of the tree (optional; assumed if absent). **Accepts exactly one child** — wrap multiple tasks in a composite. |

---

## Composite Tasks

| Keyword | Description |
|---|---|
| `sequence` | Runs children in order. Fails on first failure; succeeds when all succeed. |
| `selector` | Runs children in order. Succeeds on first success; fails when all fail. |
| `randomSequence` | Like `sequence` but shuffles children each run. |
| `randomSelector` | Like `selector` but shuffles children each run. |
| `parallel policy:"sequence\|selector"` | Runs **all** children each tick. Policy determines pass/fail rule (see below). |
| `dynamicGuardSelector` | Re-evaluates guards every tick. Cancels current child if a higher-priority guard becomes true. |

### `parallel` attributes

| Attribute | Values | Default | Description |
|---|---|---|---|
| `policy` | `"sequence"` / `"selector"` | `"sequence"` | Fail-all or succeed-any |
| `orchestrator` | `"resume"` / `"join"` | `"resume"` | Whether to re-run finished children each step |

---

## Decorators (wrap exactly one child)

| Keyword | Description |
|---|---|
| `invert` | Swaps success ↔ failure. |
| `repeat [times:N]` | Repeats child N times (omit `times` for infinite). |
| `alwaysFail` | Always returns failure regardless of child result. |
| `alwaysSucceed` | Always returns success regardless of child result. |
| `untilFail` | Repeats child until it fails; then succeeds. |
| `untilSuccess` | Repeats child until it succeeds; then succeeds. |
| `random success:0.0` | Succeeds with the given probability (0.0–1.0); child is ignored. |
| `semaphoreGuard name:"id" [count:N]` | Limits how many agents can run the child at once. |
| `include subtree:"path/to/file" [lazy:true]` | Grafts an external `.btree` file as a child. |

---

## Built-in Leaf Tasks

| Keyword | Attributes | Description |
|---|---|---|
| `wait` | `seconds:N` | Runs for N seconds then succeeds. Supports distributions. |
| `failure` | — | Immediately fails. |
| `success` | — | Immediately succeeds. |

---

## Sub-tree Reference

```
$treeName
```
Inlines the named `subtree` block anywhere a task is legal.

---

## Guards

Guards precede the task they protect on the same line:

```
(guardTask) task
($guardSubtree) task       # guard is a sub-tree
(g1) (g2) task             # chained guards = implicit sequence
() task                    # empty guard — always true (fallback slot)
```

Used as children of `dynamicGuardSelector`:
```
dynamicGuardSelector
  (condition?) $subtreeA
  ()           $fallback
```

---

## Attribute Value Types

| Type | Example |
|---|---|
| Boolean | `enabled:true` |
| Number | `meters:20` |
| Enum string | `policy:"selector"` |
| Constant dist. | `times:3` or `times:"constant,3"` |
| Uniform dist. | `seconds:"uniform,3,6"` |
| Gaussian dist. | `seconds:"gaussian,5,1"` |
| Triangular dist. | `seconds:"triangular,1,5,3"` |
| Null | `value:null` |

---

## Naming Conventions

| Convention | Meaning |
|---|---|
| `taskName?` | Alias ends with `?` — signals the task is a **condition** |
| `$treeName` | Reference to a `subtree` block |
| `# comment` | Line comment (rest of line is ignored) |

---

## File Structure Skeleton

```
import myAction:"com.example.MyAction"
import myCondition?:"com.example.MyCondition"

subtree name:"mySub"
  sequence
    myCondition?
    myAction

root
  dynamicGuardSelector
    (myCondition?) $mySub
    ()             $fallback
```
