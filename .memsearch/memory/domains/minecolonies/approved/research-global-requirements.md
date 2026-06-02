**Title:** MineColonies research — implicit global prerequisites not encoded in node JSON
**Type:** fact
**Intent triggers:** university requirement, research prerequisite, research access, university building, implicit requirement, research gate, missing requirement, research UI, all research, global research requirement, building upgrade chain, builder hut, townhall level, dependency gap, incomplete prerequisites
**Source:** [[none]]
**Rule or fact:** University level 1 is required to access the MineColonies research UI at all. This prerequisite is NOT encoded in any individual research node's requirements array — it is a game mechanic enforced by the University building itself.

Any code or advisor logic that reads research node requirements from the flat JSON must inject this requirement manually. Trusting the JSON alone will produce incorrect "you can research this" answers for players who have not yet built a University.

**The research JSON is necessary but not sufficient.** It accurately captures research-level prerequisites, but the full dependency graph for any given goal includes layers not present in this data:

- Known gap: **Building upgrade chains** — leveling a building (e.g. Townhall 5) has its own prerequisites (e.g. Builder's Hut 5) that are not encoded here
- Known gap: **University gate** — documented above
- Unknown gaps: other implicit game mechanic dependencies may exist and have not yet been identified

Advisor logic must treat this data as one layer of a deeper graph, not a complete picture.

**Why:** Research JSON was treated as complete in prior implementation, producing incomplete advisor answers. Building upgrade chains surfaced as a specific known gap during review.

**How to apply:** When evaluating whether a player can achieve a goal, always: (1) check University >= level 1, (2) evaluate node-specific research requirements, (3) explicitly note that building upgrade chain requirements and other potential dependencies are outside the scope of this data.
