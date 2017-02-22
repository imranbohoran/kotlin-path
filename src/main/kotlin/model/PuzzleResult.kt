package model

sealed class PuzzleResult(path: List<PuzzleDirection>) {
    class Success(val path: List<PuzzleDirection>) : PuzzleResult(path)
    class Fail(val path: List<PuzzleDirection>) : PuzzleResult(path)

    val resultPath = path
}
