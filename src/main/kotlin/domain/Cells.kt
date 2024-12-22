package domain

class Cells(private val initialRows: List<Row>) {
    val rows: List<Row> get() = _rows
    private val _rows: MutableList<Row> = initialRows.toMutableList()

    fun addNumberHints(): Cells {
        val updatedRows =
            _rows.mapIndexed { rowIndex, row ->
                row.addNumberHints(rowIndex, this)
            }
        return Cells(updatedRows)
    }

    operator fun get(row: Int): Row = _rows[row]

    val size: Int
        get() = _rows.size

    companion object {
        fun create(
            height: Int,
            width: Int,
            minePositions: Set<Position>,
        ): Cells {
            val rows =
                List(height) { row ->
                    Row.create(width) { col ->
                        val isMine = minePositions.contains(Position(row, col))
                        Cell.create(isMine)
                    }
                }
            return Cells(rows)
        }
    }
}
