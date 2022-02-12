

class LevelParser(private val levelFileContents: String) {

    companion object {
        const val PLAYER_SYM = "@"
        const val WALL_SYM = "#"
        const val FLOOR_SYM = " "
        const val TARGET_SYM = "*"
        const val CRATE_SYM = "X"
        val LEGAL_SYMS = listOf(PLAYER_SYM, WALL_SYM, FLOOR_SYM, TARGET_SYM, CRATE_SYM)

        fun forResourcePath(resPath: String): LevelParser {
            val resource = {}::class.java.getResource(resPath)
                ?: throw IllegalArgumentException("Unknown level resource: $resPath")

            return LevelParser(resource.readText())
        }
    }

    fun invoke(): Level {
        val tiles = mutableListOf<Tile>()
        val crates = mutableListOf<Crate>()
        var playerStartCoord: Coord? = null

        levelFileContents.split("\n").forEachIndexed { lineNum, line ->
            line.forEachIndexed { colNum, char ->
                val current = char.toString()
                val coord = Coord(colNum, lineNum)

                if (current == PLAYER_SYM) playerStartCoord = coord
                if (current == CRATE_SYM)
                    crates.add(Crate(coord))
                tiles.add(tileFor(coord, char.toString()))
            }
        }

        if (playerStartCoord == null)
            throw IllegalStateException("Could not find player start coordinates")

        return Level(tiles, playerStartCoord!!, crates)
    }

    private fun tileFor(coord: Coord, char: String): Tile {
        return when (char) {
            CRATE_SYM -> Tile.floor(coord)
            FLOOR_SYM -> Tile.floor(coord)
            WALL_SYM -> Tile.wall(coord)
            TARGET_SYM -> Tile.target(coord)
            PLAYER_SYM -> Tile.floor(coord)

            else -> throw IllegalArgumentException("Unknown level symbol: $char")
        }
    }
}
