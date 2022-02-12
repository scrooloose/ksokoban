import UI.UIAction
import com.googlecode.lanterna.Symbols
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.gui2.BasicWindow
import com.googlecode.lanterna.gui2.Button
import com.googlecode.lanterna.gui2.DefaultWindowManager
import com.googlecode.lanterna.gui2.EmptySpace
import com.googlecode.lanterna.gui2.GridLayout
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.Terminal

interface UI {
    sealed class UIAction {
        object MoveUp : UIAction()
        object MoveDown : UIAction()
        object MoveLeft : UIAction()
        object MoveRight : UIAction()
        object Quit : UIAction()
        object RestartLevel : UIAction()
        class PickLevel(val levelName: String) : UIAction()
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

        renderInstructions(level)
        screen.refresh()
    }

    private fun renderTile(tile: Tile) {
        if (tile.symbol == LevelParser.TARGET_SYM)
            putChar(Symbols.BLOCK_SPARSE.toString(), tile.coord, fg = TextColor.ANSI.YELLOW)
        else
            putChar(tile.symbol, tile.coord, fg = TextColor.ANSI.WHITE)
    }

    private fun renderCrate(crate: Crate, level: Level) {
        if (level.isTargetTile(crate.coord))
            putChar(Symbols.BLOCK_SOLID.toString(), crate.coord, fg = TextColor.ANSI.GREEN)
        else
            putChar(Symbols.BLOCK_MIDDLE.toString(), crate.coord, fg = TextColor.ANSI.RED)
    }

    private fun renderPlayer(coord: Coord) {
        putChar("@", coord)
    }

    private fun renderInstructions(level: Level) {
        var currentLine = level.height + 3
        textGraphics.putString(0, currentLine, "Controls")
        textGraphics.putString(0, ++currentLine, "  Arrow keys: Move around, push crates")
        textGraphics.putString(0, ++currentLine, "  r: Restart level")
        textGraphics.putString(0, ++currentLine, "  l: Pick a new level")
        textGraphics.putString(0, ++currentLine, "  q: quit")
    }

    private fun putChar(
        c: String,
        coord: Coord,
        fg: TextColor.ANSI = TextColor.ANSI.WHITE,
        bg: TextColor.ANSI = DEFAULT_BG_COLOR
    ) {
        textGraphics.setCharacter(coord.x, coord.y, TextCharacter.fromString(c, fg, bg)[0])
    }

    override fun getNextUIAction(): UIAction {
        while (true) {
            val keyStroke: KeyStroke = terminal.readInput()

            if (keyStroke.character == 'q') {
                screen.stopScreen()
                return UIAction.Quit
            }
            if (keyStroke.character == 'r') return UIAction.RestartLevel
            if (keyStroke.character == 'l') return pickLevel()
            if (keyStroke.keyType == KeyType.ArrowLeft) return UIAction.MoveLeft
            if (keyStroke.keyType == KeyType.ArrowRight) return UIAction.MoveRight
            if (keyStroke.keyType == KeyType.ArrowUp) return UIAction.MoveUp
            if (keyStroke.keyType == KeyType.ArrowDown) return UIAction.MoveDown
        }
    }

    private fun pickLevel(): UIAction {
        val panel = Panel().setLayoutManager(GridLayout(2))
        val panelWindow = BasicWindow()
        panel.addComponent(Label("Enter level number (1 - 50)"))
        val levelInput = TextBox().addTo(panel)
        Button("OK") { panelWindow.close() }.addTo(panel)
        panelWindow.component = panel

        val gui = MultiWindowTextGUI(screen, DefaultWindowManager(), EmptySpace(DEFAULT_BG_COLOR))
        gui.addWindowAndWait(panelWindow)

        return UIAction.PickLevel("level_${levelInput.text}")
    }

    override fun displayLevelComplete() {
        screen.clear()
        textGraphics.putString(0, 0, "Level complete!")
        textGraphics.putString(0, 2, "Push 'l' to select another level!")
        screen.refresh()
    }
}
