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

            // have to add QUIT otherwise the engine run method never returns
            val actionsWithQuit = actions + UIAction.QUIT

            // FIXME: this sucks, but the method signature of thenReturn requires it AFAICT
            whenever(ui.getNextUIAction()).thenReturn(
                actionsWithQuit.first(),
                *actionsWithQuit.slice(1 until actionsWithQuit.size).toTypedArray()
            )
            return ui
        }

        it("stops running when the UI returns the quit action") {
            val ui: UI = mock()
            whenever(ui.getNextUIAction()).thenReturn(UIAction.QUIT)

            // FIXME: run this in a coroutine and check that it exits or similar
            Engine(emptyRoomLevel, ui).run()

            assertThat("We should get to this assertion. Need to test this a better way...").isNotNull()
        }

        it("moves the player right when instructed by the UI") {
            val engine = Engine(emptyRoomLevel, mockUI(listOf(UIAction.MOVE_RIGHT)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.right())
        }

        it("moves the player left when instructed by the UI") {
            val engine = Engine(emptyRoomLevel, mockUI(listOf(UIAction.MOVE_LEFT)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.left())
        }

        it("moves the player up when instructed by the UI") {
            val engine = Engine(emptyRoomLevel, mockUI(listOf(UIAction.MOVE_UP)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.up())
        }

        it("moves the player down when instructed by the UI") {
            val engine = Engine(emptyRoomLevel, mockUI(listOf(UIAction.MOVE_DOWN)))
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.down())
        }

        it("does nothing when the player tries to move into a wall") {
            // move left once, then try to move into the wall twice
            val ui = mockUI(listOf(UIAction.MOVE_LEFT, UIAction.MOVE_LEFT, UIAction.MOVE_LEFT))
            val engine = Engine(emptyRoomLevel, ui)
            engine.run()

            assertThat(engine.playerCoord).isEqualTo(emptyRoomLevel.playerStart.left())
        }

        it("pushes crates when the player moves into one") {
            val level = LevelParser.forResourcePath("levels/emptyRoomWithCrate").invoke()
            // check the map is as we expect ... this is fairly paranoid...
            assertThat(level.playerStart).isEqualTo(Coord(2, 2))
            assertThat(level.crateAt(Coord(3, 2))).isNotNull
            val engine = Engine(level, mockUI(listOf(UIAction.MOVE_RIGHT)))

            engine.run()

            assertThat(engine.playerCoord).isEqualTo(Coord(3, 2))
            assertThat(level.crateAt(Coord(4, 2))).isNotNull
        }

        it("tells the UI when the level is complete and terminates the loop") {
            val level = LevelParser.forResourcePath("levels/trivialCompletableLevel").invoke()
            val ui = mockUI(listOf(UIAction.MOVE_RIGHT))
            val engine = Engine(level, ui)

            engine.run()

            verify(ui, times(1)).displayLevelComplete()
        }
    }
})