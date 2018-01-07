package org.butterbrot.heve.scrolltable

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
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

    private var northRows: Int = 0
    private var southRows: Int = 0
    private var westColumns: Int = 0
    private var eastColumns: Int = 0
    private var northRowsGap: Int = 0
    private var southRowsGap: Int = 0
    private var westColumnsGap: Int = 0
    private var eastColumnsGap: Int = 0
    private var borderColor: Int = 0
    private var northCellBorder: Int = 0
    private var southCellBorder: Int = 0
    private var westCellBorder: Int = 0
    private var eastCellBorder: Int = 0
    private var shrinkNorth: Boolean = false
    private var shrinkSouth: Boolean = false
    private var shrinkWest: Boolean = false
    private var shrinkEast: Boolean = false

    private val widths: MutableMap<Int, Int> = mutableMapOf()
    private val heights: MutableMap<Int, Int> = mutableMapOf()
    private val rows: MutableList<List<View>> = mutableListOf()

    init {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.scrolltable, this)


        val typedArray: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScrollTable, 0, 0)

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

        borderColor = typedArray.getColor(R.styleable.ScrollTable_scrolltable_borderColor, -1)

        northRowsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_northRowsGap, 0)
        southRowsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_southRowsGap, 0)
        westColumnsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_westColumnsGap, 0)
        eastColumnsGap = typedArray.getInt(R.styleable.ScrollTable_scrolltable_eastColumnsGap, 0)

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
        val rowIndex = rows.size - 1
        row.forEachIndexed { columnIndex, view ->
            val dim = viewDimension(view)
            widths[columnIndex] = Math.max(widths[columnIndex] ?: 0, dim.first)
            heights[rowIndex] = Math.max(heights[rowIndex] ?: 0, dim.second)
        }
    }

    private fun viewDimension(view: View): Pair<Int, Int> {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return Pair(view.measuredWidth, view.measuredHeight)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val northRowsIndex = Math.max(0, Math.min(rows.size, northRows))
        val southRowsIndex = Math.max(northRowsIndex, rows.size - southRows)
        val westColumnsIndex = Math.max(0, Math.min(widths.size, westColumns))
        val eastColumnsIndex = Math.max(westColumnsIndex, widths.size - eastColumns)
        handleRows(0, northRowsIndex, northWest, north, northEast, westColumnsIndex, eastColumnsIndex)
        handleRows(northRowsIndex, southRowsIndex, west, center, east, westColumnsIndex, eastColumnsIndex)
        handleRows(southRowsIndex, rows.size, southWest, south, southEast, westColumnsIndex, eastColumnsIndex)
        setEdgeSizes(northRowsIndex, southRowsIndex, westColumnsIndex, eastColumnsIndex)
        setGaps()
    }

    private fun setGaps() {
        listOf(R.id.eastHScroll, R.id.westHScroll).map { id ->
            rootView.findViewById<HorizontalScrollView>(id)
        }.forEach { view -> view.setPadding(view.paddingStart, view.paddingTop + northRowsGap, view.paddingEnd, view.paddingBottom + southRowsGap) }

        listOf(R.id.northHScroll, R.id.southHScroll).map { id ->
            rootView.findViewById<HorizontalScrollView>(id)
        }.forEach { view -> view.setPadding(view.paddingStart + westColumnsGap, view.paddingTop, view.paddingEnd + eastColumnsGap, view.paddingBottom) }

        val view = rootView.findViewById<HorizontalScrollView>(R.id.centerHScroll)
        view.setPadding(view.paddingStart + westColumnsGap, view.paddingTop + northRowsGap, view.paddingEnd + eastColumnsGap,view.paddingBottom + southRowsGap)
    }

    private fun setEdgeSizes(northRowsIndex: Int, southRowsIndex: Int, westColumnsIndex: Int, eastColumnsIndex: Int) {
        if (shrinkNorth) {
            listOf(R.id.northEastVScroll, R.id.northVScroll, R.id.northWestVScroll).forEach { id ->
                rootView.findViewById<ScrollView>(id).layoutParams.height = maxSize(0, northRowsIndex - 1, heights)
            }
        }
        if (shrinkSouth) {
            listOf(R.id.southEastVScroll, R.id.southVScroll, R.id.southWestVScroll).forEach { id ->
                rootView.findViewById<ScrollView>(id).layoutParams.height = maxSize(southRowsIndex, heights.size - 1, heights)
            }
        }
        if (shrinkEast) {
            listOf(R.id.eastHScroll, R.id.northEastHScroll, R.id.southEastHScroll).forEach { id ->
                rootView.findViewById<HorizontalScrollView>(id).layoutParams.width = maxSize(eastColumnsIndex, widths.size - 1, widths)
            }
        }
        if (shrinkWest) {
            listOf(R.id.westHScroll, R.id.southWestHScroll, R.id.northWestHScroll).forEach { id ->
                rootView.findViewById<HorizontalScrollView>(id).layoutParams.width = maxSize(0, westColumnsIndex - 1, widths)
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
        if (borderColor >= 0) {
            tableRow.setBackgroundColor(borderColor)
        }
        row.forEachIndexed { colIndex, it ->
            val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            params.width = widths[colIndex + colStartIndex] ?: 0
            params.height = heights[rowIndex] ?: 0
            params.setMargins(westCellBorder, northCellBorder, eastCellBorder, southCellBorder)
            it.layoutParams = params
            tableRow.addView(it)
        }
        return tableRow
    }

    private fun createDummyViews(number: Int): List<View> {
        return (1..number).map { View(context) }
    }

    companion object {
        val SHRINK_NORTH = 1
        val SHRINK_EAST = 2
        val SHRINK_SOUTH = 4
        val SHRINK_WEST = 8
    }
}

