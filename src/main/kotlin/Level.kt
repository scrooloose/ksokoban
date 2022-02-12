class Level(val tiles: List<Tile>, val playerStart: Coord, val crates: List<Crate>) {
    val height: Int
    val width: Int
    private val targetTiles = tiles.filter { it.symbol == LevelParser.TARGET_SYM }

    init {
        height = tiles.maxOf { it.coord.y } + 1
        width = tiles.maxOf { it.coord.x } + 1
    }

    fun tileAt(coord: Coord): Tile = tiles.find { it.coord == coord }
        ?: throw IllegalArgumentException("No tile found at $coord")

    fun crateAt(coord: Coord): Crate? = crates.find { it.coord == coord }

    fun isTargetTile(coord: Coord) = targetTiles.any { it.coord == coord }

    fun isComplete(): Boolean {
        // FIXME: this is a hack for some tests that have rooms with no targets
        if (targetTiles.isEmpty()) return false

        return targetTiles.all { target ->
            crates.any { crate -> crate.coord == target.coord  }
        }
    }
}