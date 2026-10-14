# Validation and change summary

## Environment and baseline

Validation used Linux, OpenJDK/Javac **21.0.12.1**, the JDK's `jar` tool and Xvfb for Swing. No external Java dependencies were introduced. The seven original test programs passed before refactoring, using the original source tree and an external build directory.

The baseline still exposed reproducible defects:

```text
Out-of-board destination: ArrayIndexOutOfBoundsException
Empty source accepted: true
King remains on throne after capture: true
Captured game's result: 1; result in its copy: 0
```

The new regressions exercise these behaviours directly.

## Executed checks

| Check | Result | What it establishes |
| --- | --- | --- |
| Clean source compilation and JAR build | Passed | Java 21 target, UTF-8 sources, correct `tablut.Tablut` entry point and packaged resources |
| `TestModel` | Passed | Initial state, legal opening, diagonal rejection, turn and board update |
| `TestTableauBit` | Passed | Board serialization round-trip before/after a move |
| `TestCommunication` | Passed | Move/request/response serialization and checked failure for invalid numeric input |
| `TestReseauLocal` | Passed | Two-client initial synchronization, legal move, opponent-piece/out-of-range rejection, negotiated undo/draw, disconnect and server shutdown |
| `TestAIEasy` | Passed | First move and bounded AI-vs-AI simulation reaching an end condition |
| `TestAIInter` | Passed | First move and bounded AI-vs-AI simulation reaching an end condition |
| `TestAIHard` | Passed | First move and a two-move progression check; not a full game |
| `TestRules` | Passed | Invalid input causes no mutation; throne restrictions, normal/royal captures, escape, terminal state, undo/redo and repetition |
| `TestPersistence` | Passed | UTF-8 names, current state/history round-trip, terminal result/draw, all ten legacy saves, malformed/missing files and filename validation |
| `TestAIRegression` | Passed | Immediate wins for all three levels, search isolation, one history entry per applied move, no post-game move, invalid level rejection and alpha-beta agreement with exhaustive two-ply minimax on both sides |
| `TestResources` | Passed | All **82 original PNGs** load through the application JAR; test classes are absent from the application artifact |
| `TestGui` under Xvfb | Passed | Application entry point, both themes, two nickname prompts, new game, eleven tutorial pages, cancelled nickname flow, responsive EDT during hard search, cancellation after disposal and one applied background easy move |
| `TestGui` launched from `/tmp` against the JAR | Passed | Desktop and tutorial images do not depend on the checkout's current working directory |
| Protected-file hash comparison | Passed | All **95** original images, reports, saved examples and the original JAR retain their bytes |
| Shell script syntax | Passed | Build, test, run and compatibility scripts parse successfully |

`bash scripts/test.sh` reports **11 test programs passed**. The optional GUI check is a twelfth executed program. These are standalone Java checks; no JUnit test count or coverage percentage is claimed. The suite checks the built JAR rather than mixing test classes into the application artifact.

The full automated suite and desktop check passed together with `xvfb-run ... bash scripts/test.sh --gui`. After the final small redo/persistence-test adjustment, the project was rebuilt and the affected rules/persistence checks were rerun successfully. Build outputs are ignored; the original university JAR is never overwritten.

## Changes made

- Conventional `src/main/java`, `src/test/java`, `src/main/resources` layout and lowercase `tablut.*` packages.
- AI separated from the model; the three selected strategies share `AlphaBetaAI` traversal while retaining their original depths and heuristics.
- Original PDFs moved intact to `docs/reports/`, images moved intact to the resource tree and the original Java 25 artifact archived at `dist/legacy/Tablut.jar`.
- Movement validation and consistent king/result/history state, safer persistence, shared repetition detection and a meaningful replacement for the tautological model check.
- Common classpath image loader, reliable packaged tutorials, corrected icon scaling and fixed cancelled-game flow.
- Background search through SwingWorker, stale-result cancellation and window timer cleanup; Swing dispatch for network updates.
- Server lifecycle cleanup, message-write synchronization and authoritative game results in network replies.
- Reusable JDK scripts, improved `.gitignore`, a GitHub Actions workflow, English portfolio README, real interface captures and a detailed source/report audit.

## Bounds of the verification

- The Java 25 legacy JAR is preserved and inspected, but was not executed with a Java 25 runtime. The refactored Java 21 JAR was built and exercised.
- Tests cover local TCP sockets. A game between remote machines, Internet routing, and a pair of graphical network clients were not exercised.
- Hosted GitHub Actions has not run locally; its equivalent build/test commands passed. Nothing was pushed to trigger it.
- The hard AI's complete-game behaviour, statistical difficulty ranking and the historical report's win rates were not reproduced. Random tie-breaking and the easy evaluation mean simulations are not deterministic benchmarks.
- The desktop check covers the named flows, not every possible gameplay sequence, screen size or accessibility requirement.
- The code is compiled on Linux. Native Windows/macOS execution was not tested; Bash scripts and classpath separators are documented accordingly.
- Remaining design and rule-variant limitations are documented in [AUDIT.md](AUDIT.md). No unresolved failure remains in the checks listed as passed.

Work is isolated on `refactor/project-structure`. No push or merge into `main` is performed as part of this work.
