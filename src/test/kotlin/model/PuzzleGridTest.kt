package model

import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test

class PuzzleGridTest {

    @Test
    fun gridDimensionsAreSetCorrectlyForSingleLineContent() : Unit {
        val grid = PuzzleGrid("1,2,3,4")
        assertThat(grid.xLength, CoreMatchers.`is`(4));
        assertThat(grid.yLength, CoreMatchers.`is`(4));
    }
}
