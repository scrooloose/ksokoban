import io.kotest.core.spec.style.DescribeSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

class LevelParserTests : DescribeSpec({
    fun assertLevelHasTileAtCoord(level: Level, tile: Tile) {
        val tileInLevel = level.tileAt(tile.coord)
        assertThat(tileInLevel).isEqualTo(tile)
    }

    describe("invoke()") {
        it("returns the level from the given file") {
            val level = LevelParser.forResourcePath("levels/simpleLevel").invoke()

            // top wall
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(0, 0)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(1, 0)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(2, 0)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(3, 0)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(4, 0)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(5, 0)))

            // bottom wall
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(0, 4)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(1, 4)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(2, 4)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(3, 4)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(4, 4)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(5, 4)))

            // left wall
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(0, 0)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(0, 1)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(0, 2)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(0, 3)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(0, 4)))

            // right wall
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(5, 0)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(5, 1)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(5, 2)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(5, 3)))
            assertLevelHasTileAtCoord(level, Tile.wall(Coord(5, 4)))

            // the single crate
            assertThat(level.crateAt(Coord(3, 2))).isNotNull

            // There are many floor tiles. Just test one for now
            assertLevelHasTileAtCoord(level, Tile.floor(Coord(1, 1)))

            assertThat(level.playerStart).isEqualTo(Coord(2, 2))
        }

        it("throws an error for levels with unknown tile symbols") {
            assertThrows<IllegalArgumentException> {
                LevelParser.forResourcePath("levels/levelWithUnknownTileSymbol").invoke()
            }
        }
    }
})
