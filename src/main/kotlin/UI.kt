import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.Terminal

interface UI {
    enum class UIAction {
        MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, QUIT
    }

    fun render(level: Level, playerCoord: Coord)
    fun getNextUIAction(): UIAction
    fun displayLevelComplete()
}

class TerminalUI(val terminal: Terminal) : UI {
    private val screen: TerminalScreen = TerminalScreen(terminal)
    private val textGraphics: TextGraphics = screen.newTextGraphics()
    private val DEFAULT_BG_COLOR = TextColor.ANSI.BLACK

    init {
        screen.startScreen()
        screen.clear()
        screen.cursorPosition = null

        textGraphics.foregroundColor = TextColor.ANSI.WHITE
        textGraphics.backgroundColor = DEFAULT_BG_COLOR
    }

    override fun render(level: Level, playerCoord: Coord) {
        level.tiles.forEach { renderTile(it) }
        level.crates.forEach { renderCrate(it, level) }
        renderPlayer(playerCoord)
        screen.refresh()
    }

    private fun renderTile(tile: Tile) {
        val color =
            if (tile.symbol == LevelParser.TARGET_SYM)
                TextColor.ANSI.YELLOW
            else
                TextColor.ANSI.WHITE

        putChar(tile.symbol, tile.coord, fg = color)
    }

    private fun renderCrate(crate: Crate, level: Level) {
        val color =
            if (level.isTargetTile(crate.coord))
                TextColor.ANSI.GREEN
            else
                TextColor.ANSI.RED

        putChar(LevelParser.CRATE_SYM, crate.coord, fg = color)
    }

    private fun renderPlayer(coord: Coord) {
        putChar("@", coord)
    }

    private fun putChar(
        c: String,
        coord: Coord,
        fg: TextColor.ANSI = TextColor.ANSI.WHITE,
        bg: TextColor.ANSI = DEFAULT_BG_COLOR
    ) {
        textGraphics.setCharacter(coord.x, coord.y, TextCharacter.fromString(c, fg, bg)[0])
    }

    override fun getNextUIAction(): UI.UIAction {
        while (true) {
            val keyStroke: KeyStroke = terminal.readInput()

            if (keyStroke.character == 'q') return UI.UIAction.QUIT
            if (keyStroke.keyType == KeyType.ArrowLeft) return UI.UIAction.MOVE_LEFT
            if (keyStroke.keyType == KeyType.ArrowRight) return UI.UIAction.MOVE_RIGHT
            if (keyStroke.keyType == KeyType.ArrowUp) return UI.UIAction.MOVE_UP
            if (keyStroke.keyType == KeyType.ArrowDown) return UI.UIAction.MOVE_DOWN
        }
    }

    override fun displayLevelComplete() {
        terminal.clearScreen()
        textGraphics.putString(0, 0, "Level complete!", SGR.BOLD)
        terminal.flush()
    }
}