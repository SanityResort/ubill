package org.butterbrot.heve.scrolltable

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TableLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.TableRow


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

    private var fixedTop: Int = 0
    private var fixedBottom: Int = 0
    private var fixedLeft: Int = 0
    private var fixedRight: Int = 0
    private var oddRowColor: Int = 0
    private var oddColumnColor: Int = 0
    private var borderColor: Int = 0
    private var topBorderHeight: Int = 0
    private var bottomBorderHeight: Int = 0
    private var leftBorderWidth: Int = 0
    private var rightBorderWidth: Int = 0
    private var maxColWidth: Int = 0

    private val widths: MutableMap<Int, Int> = mutableMapOf()
    private val heights: MutableMap<Int, Int> = mutableMapOf()
    private val rows: MutableList<List<View>> = mutableListOf()

    init {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.scrolltable, this)


        val typedArray: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScrollTable, 0, 0)

        fixedTop = typedArray.getResourceId(R.styleable.ScrollTable_fixedTop, 0)
        fixedBottom = typedArray.getResourceId(R.styleable.ScrollTable_fixedBottom, 0)
        fixedLeft = typedArray.getResourceId(R.styleable.ScrollTable_fixedLeft, 0)
        fixedRight = typedArray.getResourceId(R.styleable.ScrollTable_fixedRight, 0)

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
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return Pair(view.measuredWidth, view.measuredHeight);
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val topFixedIndex = Math.max(0, Math.min(rows.size, fixedTop))
        val bottomFixedIndex = Math.max(topFixedIndex, rows.size - fixedBottom)
        val leftFixedIndex = Math.max(0, Math.min(widths.size, fixedLeft))
        val rightFixedIndex = Math.max(leftFixedIndex, widths.size - fixedRight)
        handleRows(0, topFixedIndex, northWest, north, northEast, leftFixedIndex, rightFixedIndex)
        handleRows(topFixedIndex, bottomFixedIndex, west, center, east, leftFixedIndex, rightFixedIndex)
        handleRows(bottomFixedIndex, rows.size, southWest, south, southEast, leftFixedIndex, rightFixedIndex)
    }

    private fun handleRows(fromRowIndex: Int, toRowIndex: Int, left: TableLayout, middle: TableLayout, right: TableLayout, leftFixedIndex: Int, rightFixedIndex: Int) {
        if (fromRowIndex < toRowIndex) {
            rows.subList(fromRowIndex, toRowIndex).forEachIndexed { index, it ->
                val missingViewCount = widths.size - it.size
                handleRow(it.plus(createDummyViews(missingViewCount)), index, left, middle, right, leftFixedIndex, rightFixedIndex)
            }
        }
    }

    private fun handleRow(row: List<View>, rowIndex: Int,  left: TableLayout, middle: TableLayout, right: TableLayout, leftFixedIndex: Int, rightFixedIndex: Int) {
        left.addView(createTableRow(row.subList(0, leftFixedIndex), rowIndex, 0))
        middle.addView(createTableRow(row.subList(leftFixedIndex, rightFixedIndex), rowIndex, leftFixedIndex))
        right.addView(createTableRow(row.subList(rightFixedIndex, widths.size), rowIndex, rightFixedIndex))
    }

    private fun createTableRow(row: List<View>, rowIndex: Int, colStartIndex: Int): TableRow {
        val tableRow = TableRow(context)
//      TODO  tableRow.setBackgroundColor(borderColor)
        row.forEachIndexed { colIndex, it ->
            val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            params.width = widths[colIndex + colStartIndex]?:0
            params.height = heights[rowIndex]?:0
            it.layoutParams = params
            // TODO params.margins
            tableRow.addView(it)
        }
        return tableRow;
    }

    private fun createDummyViews(number: Int): List<View> {
        return (1..number).map { View(context) }
    }
}