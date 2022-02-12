import io.kotest.core.spec.style.DescribeSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

class LevelTest : DescribeSpec({
    describe("tileAt") {
        val simpleLevel = createLevel(
            tiles = listOf(
                Tile.floor(Coord(0, 0)),
                Tile.wall(Coord(0, 1)),
            )
        )

        it("returns the Tile at the given coord") {
            assertThat(simpleLevel.tileAt(Coord(0, 0)).symbol).isEqualTo(LevelParser.FLOOR_SYM)
            assertThat(simpleLevel.tileAt(Coord(0, 1)).symbol).isEqualTo(LevelParser.WALL_SYM)
        }

        it("throws an IllegalArgumentException when no tile is found") {
            assertThrows<IllegalArgumentException> {
                simpleLevel.tileAt(Coord(100, 100))
            }
        }
    }

    describe("crateAt") {
        val simpleLevel = createLevel(
            tiles = listOf(
                Tile.floor(Coord(0, 0)),
                Tile.wall(Coord(0, 1)),
            ),
            crates = listOf(Crate(Coord(0, 0))),
        )

        it("returns the Crate at the given coord") {
            assertThat(simpleLevel.crateAt(Coord(0, 0))).isNotNull
        }

        it("throws an IllegalArgumentException when no Crate is found") {
            assertThrows<IllegalArgumentException> {
                simpleLevel.tileAt(Coord(100, 100))
            }
        }
    }

    describe("isComplete") {
        it("returns true if all crates are on a target") {
            val completeLevel = createLevel(
                tiles = listOf(
                    Tile.target(Coord(0, 0)),
                ),
                crates = listOf(Crate(Coord(0, 0))),
            )

            assertThat(completeLevel.isComplete()).isTrue
        }

        it("returns false if not all crates are on a target") {
            val completeLevel = createLevel(
                tiles = listOf(
                    Tile.target(Coord(0, 0)),
                    Tile.target(Coord(0, 1)),
                ),
                crates = listOf(Crate(Coord(0, 0))),
            )

            assertThat(completeLevel.isComplete()).isFalse
        }
    }

    describe("isTargetTile") {
        val level = createLevel(
            tiles = listOf(
                Tile.target(Coord(0, 0)),
                Tile.floor(Coord(1, 0)),
            ),
        )
        it("returns true if the given coord contains a target") {
            assertThat(level.isTargetTile(Coord(0, 0))).isTrue
        }

        it("returns false if the given coord doesnt contain a target") {
            assertThat(level.isTargetTile(Coord(1, 0))).isFalse
        }
    }
})

fun createLevel(
    tiles: List<Tile> = emptyList(),
    crates: List<Crate> = emptyList(),
    playerStart: Coord = Coord(0, 0),
    name: String = "test-level"
) = Level(
    tiles = tiles,
    crates = crates,
    playerStart = playerStart,
)
