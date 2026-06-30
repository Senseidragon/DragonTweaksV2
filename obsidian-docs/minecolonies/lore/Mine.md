The [[Mine]] is where you can hire a Miner to work the mine, or a Quarrier to work the [[Quarry]]. If you hire a Quarrier, there will be no Miner at this [[Mine]]. 

The Miner will never dig further down than the Y-level specified in the "maximum depth" setting of the building. It is by default set to -100, which effectively means bedrock level.

While mining, sometimes the Miner will get lucky and get an ore block instead of a basic stone block. The chance of getting "Lucky Ores" is set in the config.

> **Note:** When the Miner encounters air whilst building the shaft downwards, they don't make platforms there, as they think they encountered a cave. In particular, that means you should not help them with mining. Even though they skip platforms, they still check the Y-level against the depth threshold and stop digging down if they aren't allowed to dig down further.

| Building Level | Shaft Y Level |
| -------------- | ------------- |
| 1              | 40            |
| 2              | 20            |
| 3              | 0             |
| 4              | Bedrock       |
| 5              | "             |

← [[Lore-Buildings]]
