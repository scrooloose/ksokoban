import LevelParser.Companion.LEGAL_SYMS

data class Tile(
    val coord: Coord,
    val symbol: String,
    val walkable: Boolean,
) {

    init {
        require(LEGAL_SYMS.contains(symbol)) {
            "Invalid tile symbol: $symbol"
        }
    }

    companion object {
        fun wall(coord: Coord) = Tile(coord, "#", false)
        fun floor(coord: Coord) = Tile(coord, " ", true)
        fun target(coord: Coord) = Tile(coord, "*", true)
    }
}