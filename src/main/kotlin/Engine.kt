class Engine(
    val level: Level,
    val ui: UI
) {
    private var quit: Boolean = false

    var playerCoord: Coord = level.playerStart
        private set

    fun run() {
        while (!this.quit) {
            ui.render(level, playerCoord)
            processInput()
            if(level.isComplete()) {
                ui.displayLevelComplete()
                this.quit = true
            }
        }
    }

    private fun processInput() {
        when(ui.getNextUIAction()) {
            UI.UIAction.QUIT -> this.quit = true
            UI.UIAction.MOVE_LEFT ->  moveTo(playerCoord.left())
            UI.UIAction.MOVE_RIGHT -> moveTo(playerCoord.right())
            UI.UIAction.MOVE_UP -> moveTo(playerCoord.up())
            UI.UIAction.MOVE_DOWN ->  moveTo(playerCoord.down())
        }
    }

    private fun moveTo(coord: Coord) {
        tryToMoveAndPushCrate(coord) || tryToMoveToEmptyTile(coord)
    }

    private fun tryToMoveToEmptyTile(moveTo: Coord): Boolean {
        if (level.tileAt(moveTo).walkable && level.crateAt(moveTo) == null) {
            playerCoord = moveTo
            return true
        }
        return false
    }

    private fun tryToMoveAndPushCrate(moveTo: Coord): Boolean {
        val crate = level.crateAt(moveTo)
        if (crate != null) {
            val nextCoordOver =
                when(moveTo) {
                    playerCoord.right() -> playerCoord.right().right()
                    playerCoord.left() -> playerCoord.left().left()
                    playerCoord.up() -> playerCoord.up().up()
                    playerCoord.down() -> playerCoord.down().down()
                    else -> throw IllegalStateException("Destination coord is not adjacent to player")
                }

            if (level.tileAt(nextCoordOver).walkable && level.crateAt(nextCoordOver) == null) {
                playerCoord = moveTo
                crate.coord = nextCoordOver
                return true
            }
        }

        return false
    }
}