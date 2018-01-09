package org.butterbrot.heve.scrolltable

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.*


class ScrollTable @JvmOverloads constructor(
        context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
    : LinearLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    private lateinit var northEast: TableLayout
    private lateinit var north: TableLayout
    private lateinit var northWest: TableLayout
    private lateinit var east: TableLayout
    private lateinit var center: TableLayout
    private lateinit var west: TableLayout
    private lateinit var southWest: TableLayout
    private lateinit var south: TableLayout
    private lateinit var southEast: TableLayout

    private val widths: MutableMap<Int, Int> = mutableMapOf()
    private val heights: MutableMap<Int, Int> = mutableMapOf()
    private val rows: MutableList<List<View>> = mutableListOf()

    var properties: Properties

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScrollTable, 0, 0)
        properties = Properties(typedArray)
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        north = findViewById(R.id.north)
        northWest = findViewById(R.id.northWest)
        northEast = findViewById(R.id.northEast)
        center = findViewById(R.id.center)
        west = findViewById(R.id.west)
        east = findViewById(R.id.east)
        south = findViewById(R.id.south)
        southWest = findViewById(R.id.southWest)
        southEast = findViewById(R.id.southEast)
    }

    fun addRow(row: List<View>) {
        rows.add(row)
    }

    private fun viewDimension(view: View): Pair<Int, Int> {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return Pair(view.measuredWidth, view.measuredHeight)
    }

    fun populate() {
        rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, view ->
                val dim = viewDimension(view)
                widths[columnIndex] = Math.max(widths[columnIndex] ?: 0, dim.first)
                heights[rowIndex] = Math.max(heights[rowIndex] ?: 0, dim.second)
            }
        }

        val northRowsIndex = Math.max(0, Math.min(rows.size, properties.northRows))
        val southRowsIndex = Math.max(northRowsIndex, rows.size - properties.southRows)
        val westColumnsIndex = Math.max(0, Math.min(widths.size, properties.westColumns))
        val eastColumnsIndex = Math.max(westColumnsIndex, widths.size - properties.eastColumns)
        handleRows(0, northRowsIndex, northWest, north, northEast, westColumnsIndex, eastColumnsIndex)
        handleRows(northRowsIndex, southRowsIndex, west, center, east, westColumnsIndex, eastColumnsIndex)
        handleRows(southRowsIndex, rows.size, southWest, south, southEast, westColumnsIndex, eastColumnsIndex)
        setEdgeSizes(northRowsIndex, southRowsIndex, westColumnsIndex, eastColumnsIndex)
        setGaps()
    }

    override fun removeAllViews() {
        listOf(northWest, north, northEast, west, center, east, southWest, south, southEast).forEach { table ->
            (0 until table.childCount).forEach { (table.getChildAt(it) as TableRow).removeAllViews() }
            table.removeAllViews()
        }
    }

    private fun setGaps() {
        listOf(R.id.eastHScroll, R.id.westHScroll).map { id ->
            rootView.findViewById<HorizontalScrollView>(id)
        }.forEach { view -> view.setPadding(view.paddingStart, view.paddingTop + properties.northRowsGap, view.paddingEnd, view.paddingBottom + properties.southRowsGap) }

        listOf(R.id.northHScroll, R.id.southHScroll).map { id ->
            rootView.findViewById<HorizontalScrollView>(id)
        }.forEach { view -> view.setPadding(view.paddingStart + properties.westColumnsGap, view.paddingTop, view.paddingEnd + properties.eastColumnsGap, view.paddingBottom) }

        val view = rootView.findViewById<HorizontalScrollView>(R.id.centerHScroll)
        view.setPadding(view.paddingStart + properties.westColumnsGap, view.paddingTop + properties.northRowsGap, view.paddingEnd + properties.eastColumnsGap, view.paddingBottom + properties.southRowsGap)
    }

    private fun setEdgeSizes(northRowsIndex: Int, southRowsIndex: Int, westColumnsIndex: Int, eastColumnsIndex: Int) {
        if (properties.shrinkNorth) {
            listOf(R.id.northEastVScroll, R.id.northVScroll, R.id.northWestVScroll).forEach { id ->
                rootView.findViewById<ScrollView>(id).layoutParams.height = maxSize(0, northRowsIndex - 1, heights) + (properties.northCellBorder + properties.southCellBorder) * northRowsIndex
            }
        }
        if (properties.shrinkSouth) {
            listOf(R.id.southEastVScroll, R.id.southVScroll, R.id.southWestVScroll).forEach { id ->
                rootView.findViewById<ScrollView>(id).layoutParams.height = maxSize(southRowsIndex, heights.size - 1, heights) + (properties.northCellBorder + properties.southCellBorder) * Math.max(heights.size - southRowsIndex, 0)
            }
        }
        if (properties.shrinkEast) {
            listOf(R.id.eastHScroll, R.id.northEastHScroll, R.id.southEastHScroll).forEach { id ->
                rootView.findViewById<HorizontalScrollView>(id).layoutParams.width = maxSize(eastColumnsIndex, widths.size - 1, widths) + (properties.eastCellBorder + properties.westCellBorder) * Math.max(widths.size - eastColumnsIndex, 0)
            }
        }
        if (properties.shrinkWest) {
            listOf(R.id.westHScroll, R.id.southWestHScroll, R.id.northWestHScroll).forEach { id ->
                rootView.findViewById<HorizontalScrollView>(id).layoutParams.width = maxSize(0, westColumnsIndex - 1, widths) + (properties.eastCellBorder + properties.westCellBorder) * westColumnsIndex
            }
        }
    }

    private fun maxSize(start: Int, end: Int, sizes: Map<Int, Int>): Int {
        return (start..end).map { sizes[it] ?: 0 }.foldRight(0, { value, acc -> Math.max(value, acc) })
    }

    private fun handleRows(fromRowIndex: Int, toRowIndex: Int, west: TableLayout, center: TableLayout, east: TableLayout, westColumnsIndex: Int, eastColumnsIndex: Int) {
        if (fromRowIndex < toRowIndex) {
            rows.subList(fromRowIndex, toRowIndex).forEachIndexed { index, it ->
                val missingViewCount = widths.size - it.size
                handleRow(it.plus(createDummyViews(missingViewCount)), index, west, center, east, westColumnsIndex, eastColumnsIndex)
            }
        }
    }

    private fun handleRow(row: List<View>, rowIndex: Int, left: TableLayout, middle: TableLayout, right: TableLayout, westColumnsIndex: Int, eastColumnsIndex: Int) {
        left.addView(createTableRow(row.subList(0, westColumnsIndex), rowIndex, 0))
        middle.addView(createTableRow(row.subList(westColumnsIndex, eastColumnsIndex), rowIndex, westColumnsIndex))
        right.addView(createTableRow(row.subList(eastColumnsIndex, widths.size), rowIndex, eastColumnsIndex))
    }

    private fun createTableRow(row: List<View>, rowIndex: Int, colStartIndex: Int): TableRow {
        val tableRow = TableRow(context)
        tableRow.setBackgroundColor(properties.borderColor)

        row.forEachIndexed { colIndex, it ->
            val params = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
            params.setMargins(properties.westCellBorder, properties.northCellBorder, properties.eastCellBorder, properties.southCellBorder)
            params.width = widths[colIndex + colStartIndex] ?: 0
            params.height = heights[rowIndex] ?: 0
            it.layoutParams = params

            tableRow.addView(it)
        }
        return tableRow
    }

    private fun createDummyViews(number: Int): List<View> {
        return (1..number).map {
            val view = TextView(context)
            val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            params.setMargins(properties.westCellBorder, properties.northCellBorder, properties.eastCellBorder, properties.southCellBorder)
            view.layoutParams = params
            view
        }
    }

    class Properties(typedArray: TypedArray) {
        var northRows: Int = 0
        var southRows: Int = 0
        var westColumns: Int = 0
        var eastColumns: Int = 0
        var northRowsGap: Int = 0
        var southRowsGap: Int = 0
        var westColumnsGap: Int = 0
        var eastColumnsGap: Int = 0
        var borderColor: Int = 0
        var northCellBorder: Int = 0
        var southCellBorder: Int = 0
        var westCellBorder: Int = 0
        var eastCellBorder: Int = 0
        var shrinkNorth: Boolean = false
        var shrinkSouth: Boolean = false
        var shrinkWest: Boolean = false
        var shrinkEast: Boolean = false

        init {
            northRows = typedArray.getInt(R.styleable.ScrollTable_scrolltable_northRows, 0)
            southRows = typedArray.getInt(R.styleable.ScrollTable_scrolltable_southRows, 0)
            westColumns = typedArray.getInt(R.styleable.ScrollTable_scrolltable_westColumns, 0)
            eastColumns = typedArray.getInt(R.styleable.ScrollTable_scrolltable_eastColumns, 0)

            val shrinkMask = typedArray.getInt(R.styleable.ScrollTable_scrolltable_shrink, 0)
            shrinkNorth = SHRINK_NORTH and shrinkMask != 0
            shrinkSouth = SHRINK_SOUTH and shrinkMask != 0
            shrinkWest = SHRINK_WEST and shrinkMask != 0
            shrinkEast = SHRINK_EAST and shrinkMask != 0

            northCellBorder = typedArray.getInt(R.styleable.ScrollTable_scrolltable_northCellBorder, 0)
            southCellBorder = typedArray.getInt(R.styleable.ScrollTable_scrolltable_southCellBorder, 0)
            westCellBorder = typedArray.getInt(R.styleable.ScrollTable_scrolltable_westCellBorder, 0)
            eastCellBorder = typedArray.getInt(R.styleable.ScrollTable_scrolltable_eastCellBorder, 0)

            borderColor = typedArray.getColor(R.styleable.ScrollTable_scrolltable_borderColor, Color.TRANSPARENT)

            northRowsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_northRowsGap, 0)
            southRowsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_southRowsGap, 0)
            westColumnsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_westColumnsGap, 0)
            eastColumnsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_eastColumnsGap, 0)
        }
    }

    companion object {
        val SHRINK_NORTH = 1
        val SHRINK_EAST = 2
        val SHRINK_SOUTH = 4
        val SHRINK_WEST = 8
    }

}

