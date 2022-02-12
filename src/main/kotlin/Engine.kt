import UI.UIAction

class Engine(
    var level: Level,
    var currentLevelResourcePath: String,
    val ui: UI,
) {
    private var quit: Boolean = false

    var playerCoord: Coord = level.playerStart
        private set

    fun run() {
        while (!this.quit) {
            renderUI()
            if(level.isComplete()) {
                ui.displayLevelComplete()
            }
            processNextUIAction()
        }
    }

    private fun renderUI() = ui.render(level, playerCoord)

    private fun processNextUIAction() {
        when(val nextUIAction = ui.getNextUIAction()) {
            UIAction.Quit -> this.quit = true
            UIAction.MoveLeft ->  moveTo(playerCoord.left())
            UIAction.MoveRight -> moveTo(playerCoord.right())
            UIAction.MoveUp -> moveTo(playerCoord.up())
            UIAction.MoveDown ->  moveTo(playerCoord.down())
            UIAction.RestartLevel -> restartLevel()
            is UIAction.PickLevel -> changeLevel(nextUIAction)
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

    private fun restartLevel() {
        level = LevelParser.forResourcePath(currentLevelResourcePath).invoke()
        playerCoord = level.playerStart
    }

    private fun changeLevel(pickLevelAction: UIAction.PickLevel) {
        val newLevelResourcePath = "levels/${pickLevelAction.levelName}"
        val newLevel = try {
            LevelParser.forResourcePath(newLevelResourcePath).invoke()
        } catch (e: IllegalArgumentException) {
            // couldn't find level resource
            return
        }
        level = newLevel
        playerCoord = level.playerStart
        currentLevelResourcePath = newLevelResourcePath
    }
}