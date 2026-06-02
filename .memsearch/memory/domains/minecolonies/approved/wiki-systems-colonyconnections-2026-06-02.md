---
title: MineColonies system — Colony Connections
domain: minecolonies
fact: Colony connections link colonies via sign paths between Gatehouses, enabling diplomacy (ally/feud), inter-colony travel, and indirect connection discovery.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: de9ec1101d31617b1b958bf4a972c71105e95caf645a7c5c1780dac893dab7e0
validated_at: 2026-06-02T23:25:41.793486+00:00
approval_route: user-review
user_approved: true
---

Colony connections allow you to link your colony to neighbouring colonies, forming a network that serves as the foundation for future inter-colony features like trading and diplomacy.

## Requirements

To establish connections, each colony involved must have a gatehouse. The gatehouse is the anchor point for all connections to and from your colony.

## Establishing a Connection

To create a connection between your colony and another, you will need several colony sign blocks. The process works as follows:

1. Right-click your own gatehouse hut block to start the connection.
2. Walk toward the other colony, placing a new colony sign within 50 blocks of the previous one as you go.
3. The path between your signs must be fully pathfindable. Think of these signs as marking the road that colonists will travel between colonies.
4. Continue placing signs until you are close enough to the other colony's gatehouse, then right-click their hut block to finalize the connection.

Note that gatehouse are always open to interact with, regardless of who owns the colony. Other hut blocks outside your own colony cannot be opened, but gatehouse are the exception, which is what makes this process possible.

## Direct and Indirect Connections

Once a connection is established, it will appear in your gatehouse's Connections tab.

- **Direct connections** are colonies you have connected to directly by building a sign path to their gatehouse.
- **Indirect connections** are colonies that are visible through the connections of your allied colonies. If a colony you are allied with is connected to a third colony, that third colony will appear as an indirect connection for you.

## Diplomacy

Your diplomatic status with each connected colony can be managed in the Alliances tab of your townhall. You can request an alliance or declare a feud with any connected colony. Both sides must independently set the same status for it to take effect. A colony is neutral by default until both sides agree otherwise.

## Travelling

From the Connections tab in your gatehouse, you can teleport directly to the gatehouse of any colony you are allied with. Neutral and enemy colonies cannot be travelled to.

**Source:** [[docs/wiki-ref/systems/colonyconnections.mdoc]]
