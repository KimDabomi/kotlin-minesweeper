package domain

sealed class Cell {
    abstract val id: CellId
    private var _isOpen: Boolean = false
    val isOpen: Boolean
        get() = _isOpen

    abstract fun addNumberHint(
        row: Int,
        col: Int,
        allCells: Cells,
    ): Cell

    open fun isMine(): Boolean = false

    open fun open(
        row: Int,
        col: Int,
        allCells: Cells,
    ): List<Position> {
        _isOpen = true
        return emptyList()
    }

    data object Empty : Cell() {
        override val id = CellId.EMPTY

        override fun addNumberHint(
            row: Int,
            col: Int,
            allCells: Cells,
        ): Cell {
            val adjacentMineCount = countAdjacentMines(row, col, allCells)
            return if (adjacentMineCount > 0) NumberCell(adjacentMineCount) else this
        }

        private fun countAdjacentMines(
            row: Int,
            col: Int,
            allCells: Cells,
        ): Int {
            return DIRECTIONS.count { (dr, dc) ->
                val newRow = row + dr
                val newCol = col + dc

                if (newRow in 0 until allCells.size && newCol in 0 until allCells[newRow].size) {
                    allCells[newRow][newCol].isMine()
                } else {
                    false
                }
            }
        }

        override fun open(
            row: Int,
            col: Int,
            allCells: Cells,
        ): List<Position> {
            if (isOpen) return emptyList()
            super.open(row, col, allCells)
            return DIRECTIONS.flatMap { (dr, dc) ->
                val newRow = row + dr
                val newCol = col + dc

                if (newRow in 0 until allCells.size && newCol in 0 until allCells[newRow].size) {
                    val adjacentCell = allCells[newRow][newCol]
                    if (!adjacentCell.isOpen && adjacentCell is Empty) {
                        listOf(Position(newRow, newCol)) + adjacentCell.open(newRow, newCol, allCells)
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }
        }
    }

    data object MineCell : Cell() {
        override val id = CellId.MINE

        override fun isMine(): Boolean = true

        override fun addNumberHint(
            row: Int,
            col: Int,
            allCells: Cells,
        ): Cell = this
    }

    data class NumberCell(val count: Int) : Cell() {
        override val id = CellId.NUMBER

        override fun addNumberHint(
            row: Int,
            col: Int,
            allCells: Cells,
        ): Cell = this

        override fun open(
            row: Int,
            col: Int,
            allCells: Cells,
        ): List<Position> {
            super.open(row, col, allCells)
            return emptyList()
        }
    }

    companion object {
        private val DIRECTIONS =
            listOf(
                -1 to -1,
                -1 to 0,
                -1 to 1,
                0 to -1,
                0 to 1,
                1 to -1,
                1 to 0,
                1 to 1,
            )

        fun create(isMine: Boolean): Cell = if (isMine) MineCell else Empty
    }
}
