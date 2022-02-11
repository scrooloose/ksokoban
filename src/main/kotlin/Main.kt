import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
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
    println(
        """
            Usage: java -jar sokoban.jar level_<number>

                <number> - any number between 01 and 50. For example level_09
                to play level 9.
        """.trimIndent()
    )
    exitProcess(1)
}