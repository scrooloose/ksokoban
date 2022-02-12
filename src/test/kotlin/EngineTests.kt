import UI.UIAction
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalKotest::class, ExperimentalCoroutinesApi::class)
class EngineTests : DescribeSpec({
    describe("run") {
        val emptyRoomLevel = LevelParser.forResourcePath("levels/emptyRoom").invoke()

        fun mockUI(actions: List<UIAction>): UI {
            val ui: UI = mock()

            // have to add Quit otherwise the engine run method never returns
            val actionsWithQuit = actions + UIAction.Quit

            // FIXME: this sucks, but the method signature of thenReturn requires it AFAICT
            whenever(ui.getNextUIAction()).thenReturn(
                actionsWithQuit.first(),
                *actionsWithQuit.slice(1 until actionsWithQuit.size).toTypedArray()
            )
            return ui
        }

        it("stops running when the UI returns the quit action") {
            val ui: UI = mock()
            whenever(ui.getNextUIAction()).thenReturn(UIAction.Quit)

            // FIXME: run this in a coroutine and check that it exits or similar
            createEngine(emptyRoomLevel, ui).run()

            assertThat("We should get to this assertion. Need to test this a better way...").isNotNull()
        }

        it("moves the player right when instructed by the UI") {
            val engine = createEngine(emptyRoomLevel, mockUI(listOf(UIAction.MoveRight)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.right())
        }

        it("moves the player left when instructed by the UI") {
            val engine = createEngine(emptyRoomLevel, mockUI(listOf(UIAction.MoveLeft)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.left())
        }

        it("moves the player up when instructed by the UI") {
            val engine = createEngine(emptyRoomLevel, mockUI(listOf(UIAction.MoveUp)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.up())
        }

        it("moves the player down when instructed by the UI") {
            val engine = createEngine(emptyRoomLevel, mockUI(listOf(UIAction.MoveDown)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.down())
        }

        it("does nothing when the player tries to move into a wall") {
            // move left once, then try to move into the wall twice
            val ui = mockUI(listOf(UIAction.MoveLeft, UIAction.MoveLeft, UIAction.MoveLeft))
            val engine = createEngine(emptyRoomLevel, ui)
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.left())
        }

        it("pushes crates when the player moves into one") {
            val level = LevelParser.forResourcePath("levels/emptyRoomWithCrate").invoke()
            // check the map is as we expect ... this is fairly paranoid...
            assertThat(level.playerStart).isEqualTo(Coord(2, 2))
            assertThat(level.crateAt(Coord(3, 2))).isNotNull
            val engine = createEngine(level, mockUI(listOf(UIAction.MoveRight)))

            engine.run()

            assertThat(engine.playerCoord).isEqualTo(Coord(3, 2))
            assertThat(level.crateAt(Coord(4, 2))).isNotNull
        }

        it("tells the UI when the level is complete and terminates the loop") {
            val level = LevelParser.forResourcePath("levels/trivialCompletableLevel").invoke()
            val ui = mockUI(listOf(UIAction.MoveRight))
            val engine = createEngine(level, ui)

            engine.run()

            verify(ui, times(1)).displayLevelComplete()
        }

        it("resets the level when the reset UI action is given") {
            val levelResPath = "levels/emptyRoomWithCrate"
            val level = LevelParser.forResourcePath(levelResPath).invoke()
            // this will move the player and push a crate
            val ui = mockUI(listOf(UIAction.MoveRight, UIAction.RestartLevel))
            val engine = createEngine(level, ui, levelResPath)

            engine.run()

            assertThat(engine.playerCoord).isEqualTo(Coord(2, 2))
            assertThat(engine.level.crateAt(Coord(3, 2))).isNotNull
        }

        it("changes the level when the PickLevel UI action is given") {
            val levelResPath = "levels/emptyRoom"
            val level = LevelParser.forResourcePath(levelResPath).invoke()
            val ui = mockUI(listOf(UIAction.PickLevel("trivialCompletableLevel")))
            val engine = createEngine(level, ui, levelResPath)

            engine.run()

            // assert a couple of elements about the new level are present
            assertThat(engine.playerCoord).isEqualTo(Coord(2, 1))
            assertThat(engine.level.crateAt(Coord(3, 1))).isNotNull
        }
    }
})

private fun createEngine(
    level: Level,
    ui: UI,
    currentLevelResourcePath: String = "test",
) = Engine(
    level = level,
    currentLevelResourcePath = currentLevelResourcePath,
    ui = ui,
)
