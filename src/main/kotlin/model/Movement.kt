package model

data class Movement(val direction: PuzzleDirection, val distance: Int, val originatingLocation: Location)

enum class PuzzleDirection(val desc: String) {
    U("Up"),D("Down"),L("Left"),R("Right");

    val description = desc
}
