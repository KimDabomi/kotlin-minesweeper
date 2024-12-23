package domain

sealed class Cell {
    abstract val id: CellId
    private var _isOpen: Boolean = false
    val isOpen: Boolean
        get() = _isOpen

    abstract fun addNumberHint(
        row: Int,
        column: Int,
        allCells: Cells,
    ): Cell

    open fun isMine(): Boolean = false

    open fun open(
        row: Int,
        column: Int,
        allCells: Cells,
    ): List<Position> {
        _isOpen = true
        return emptyList()
    }

    data object Empty : Cell() {
        override val id = CellId.EMPTY

        override fun addNumberHint(
            row: Int,
            column: Int,
            allCells: Cells,
        ): Cell {
            val adjacentMineCount = Directions.countMatching(row, column, allCells) { it.isMine() }
            return if (adjacentMineCount > 0) NumberCell(adjacentMineCount) else this
        }

        override fun open(
            row: Int,
            column: Int,
            allCells: Cells,
        ): List<Position> {
            if (isOpen) return emptyList()
            super.open(row, column, allCells)

            val positionsToOpen = Directions.findMatchingPositions(row, column, allCells) {
                it is Empty && !it.isOpen
            }
            val allOpenedPositions = mutableListOf(Position(row, column))

            positionsToOpen.forEach { position ->
                allOpenedPositions += allCells[position.row][position.column].open(position.row, position.column, allCells)
            }

            return allOpenedPositions
        }
    }

    data object MineCell : Cell() {
        override val id = CellId.MINE

        override fun isMine(): Boolean = true

        override fun addNumberHint(
            row: Int,
            column: Int,
            allCells: Cells,
        ): Cell = this

        override fun open(
            row: Int,
            column: Int,
            allCells: Cells,
        ): List<Position> {
            super.open(row, column, allCells)
            return listOf(Position(row, column))
        }
    }

    data class NumberCell(val count: Int) : Cell() {
        override val id = CellId.NUMBER

        override fun addNumberHint(
            row: Int,
            column: Int,
            allCells: Cells,
        ): Cell = this

        override fun open(
            row: Int,
            column: Int,
            allCells: Cells,
        ): List<Position> {
            super.open(row, column, allCells)
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
