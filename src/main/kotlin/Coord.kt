data class Coord(val x: Int, val y: Int) {
    init {
        require(x >= 0)
        require(y >= 0)
    }

    fun up() = Coord(x, y - 1)
    fun down() = Coord(x, y + 1)
    fun left() = Coord(x - 1, y)
    fun right() = Coord(x + 1, y)
}