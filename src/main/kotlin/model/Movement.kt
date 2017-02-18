package model

data class Movement(val direction: PuzzleDirection, val distance: Int)

enum class PuzzleDirection(val desc: String) {
    U("Up"),D("Down"),L("Left"),R("Right");

    val description = desc
}
