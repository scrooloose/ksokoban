import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import java.lang.System.exit
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val defaultTerminalFactory = DefaultTerminalFactory()
    val terminal: Terminal = defaultTerminalFactory.createTerminal()

    if (args.isEmpty()) usage()

    val level = LevelParser.forResourcePath("levels/${args[0]}").invoke()
    val ui = TerminalUI(terminal)
    val engine = Engine(level, ui)
    engine.run()
}

private fun usage() {
    println("Usage: java -jar sokoban.jar <level-name>")
    exitProcess(1)
}