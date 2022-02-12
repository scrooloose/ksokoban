import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

fun main() {
    val defaultTerminalFactory = DefaultTerminalFactory()
    val terminal: Terminal = defaultTerminalFactory.createTerminal()

    val levelResPath = "levels/level_01"
    val level = LevelParser.forResourcePath(levelResPath).invoke()
    val ui = TerminalUI(terminal)
    val engine = Engine(level, levelResPath, ui)
    engine.run()
}
