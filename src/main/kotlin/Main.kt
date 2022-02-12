

fun main() {
    val levelResPath = "levels/level_01"
    val level = LevelParser.forResourcePath(levelResPath).invoke()
    val ui = TerminalUI()
    val engine = Engine(level, levelResPath, ui)
    engine.run()
}
