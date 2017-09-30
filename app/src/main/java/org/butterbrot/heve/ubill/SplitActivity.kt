package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_split.*
import kotlinx.android.synthetic.main.content_split.*
import org.butterbrot.heve.ubill.util.NumberUtil
import org.butterbrot.heve.ubill.view.EditNumber

class SplitActivity : AppCompatActivity() {

    private var totalAmount: Int = 0
    private lateinit var splitValues: IntArray
    private lateinit var participantNames: Array<String>

    private var editAmounts: MutableList<EditNumber> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split)
        setSupportActionBar(toolbar)

        initValues()

        header.addView(createButtonRow(createDistributeToAllButton(), createDistributeToAllZerosButton()), 2)

        participantNames.forEachIndexed { index, participantName ->
            val wrapper = createWrapper(index, participantName)
            splits.addView(wrapper)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds itemsParam to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_split, menu)
        return true
    }

    // helpers to set values
    private fun initValues() {
        totalAmount = intent.getIntExtra(InterfaceConstants.PARAM_AMOUNT, 0)
        splitValues = intent.getIntArrayExtra(InterfaceConstants.PARAM_SPLITS)
        participantNames = intent.getStringArrayExtra(InterfaceConstants.PARAM_FELLOWS)

        sum.setNumber(totalAmount)

        updateRest()
    }

    private fun updateSplit(index: Int, newValue: Int) {
        splitValues[index] = newValue
        editAmounts[index].setNumber(newValue)
    }

    private fun updateRest() {
        val allSplits = splitValues.reduceRight { i, acc -> acc + i }
        rest.setNumber(totalAmount - allSplits)
    }

    // create buttons
    private fun createClaimButton(index: Int): Button {
        val listener: View.OnClickListener = View.OnClickListener {
            updateSplit(index, splitValues[index] + rest.getNumber())
            updateRest()
        }
        return createButton(R.string.label_split_claim, listener)
    }

    private fun createDistributeRestButton(index: Int): Button {

        val listener: View.OnClickListener = View.OnClickListener {
            if (participantNames.size > 1) {
                val splitRest = rest.getNumber() / (participantNames.size - 1)
                splitValues.forEachIndexed { innerIndex, splitValue ->
                    if (innerIndex != index) {
                        updateSplit(innerIndex, splitValue + splitRest)
                    }
                }
                updateRest()
            }
        }
        return createButton(R.string.label_split_distribute_rest, listener)
    }

    private fun createDistributeEverythingElseButton(index: Int): Button {

        val listener: View.OnClickListener = View.OnClickListener {
            if (participantNames.size > 1) {
                val splitEverythingElse = (totalAmount - splitValues[index]) / (participantNames.size - 1)
                splitValues.forEachIndexed { innerIndex, splitValue ->
                    if (innerIndex != index) {
                        updateSplit(innerIndex, splitEverythingElse)
                    }
                }
                updateRest()
            }
        }
        return createButton(R.string.label_split_distribute_everything, listener)
    }

    private fun createSetZeroButton(index: Int): Button {
        val listener: View.OnClickListener = View.OnClickListener {
            updateSplit(index, 0)
            updateRest()
        }
        return createButton(R.string.label_split_set_zero, listener)
    }

    private fun createDistributeToAllButton(): Button {
        val listener: View.OnClickListener = View.OnClickListener {
            val splitRest = rest.getNumber()/participantNames.size
            splitValues.forEachIndexed{ index, splitValue ->
                updateSplit(index, splitValue + splitRest)
            }
            updateRest()
        }
        return createButton(R.string.label_split_distribute_to_all, listener)
    }

    private fun createDistributeToAllZerosButton(): Button {
        val listener: View.OnClickListener = View.OnClickListener {
            val indexesWith0: MutableList<Int> = mutableListOf()
            splitValues.forEachIndexed { index, splitValue  ->
                if (splitValue == 0){
                    indexesWith0.add(index)
                }
            }

            val splitRest = rest.getNumber()/indexesWith0.size
            indexesWith0.forEach { zeroIndex ->
                updateSplit(zeroIndex, splitRest)
            }

            updateRest()
        }
        return createButton(R.string.label_split_distribute_to_all_0s, listener)
    }


    // create views
    private fun createEditAmount(amount: Int, existingSplits: IntArray, index: Int): EditNumber {
        val editAmount = EditNumber(this@SplitActivity)
        editAmount.setNumber(amount)
        editAmount.gravity = Gravity.END
        editAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // NOOP
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // NOOP
            }

            override fun afterTextChanged(editable: Editable) {
                existingSplits[index] = editAmount.getNumber()
                updateRest()
            }
        })
        editAmounts.add(editAmount)
        return editAmount
    }

    private fun createNameView(participantName: String): TextView {
        val nameView = TextView(this)
        nameView.text = participantName
        nameView.gravity = Gravity.START
        nameView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        return nameView
    }

    // view helpers
    private fun createWrapper(index: Int, participantName: String): LinearLayout {
        val top = createValueRow(index, participantName)
        val middle = createButtonRow(createDistributeRestButton(index), createClaimButton(index))
        val bottom = createButtonRow(createDistributeEverythingElseButton(index), createSetZeroButton(index))
        val separator: ImageView = createSeparator()
        val wrapper = LinearLayout(this@SplitActivity)
        wrapper.orientation = LinearLayout.VERTICAL
        wrapper.addView(separator)
        wrapper.addView(top)
        wrapper.addView(middle)
        wrapper.addView(bottom)
        wrapper.setPadding(0,
                NumberUtil.getDimension(R.dimen.padding_split_table_bottom_top, this),
                0,
                0)
        return wrapper
    }

    private fun createSeparator(): ImageView {
        val separator: ImageView = ImageView(this)
        separator.setImageResource(R.drawable.separator)
        separator.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        separator.scaleType = ImageView.ScaleType.FIT_XY
        return separator
    }

    private fun createButtonRow(left: Button, right: Button): LinearLayout {
        val bottom = LinearLayout(this@SplitActivity)
        bottom.addView(left)
        bottom.addView(right)
        return bottom
    }

    private fun createButton(textResId: Int, listener: View.OnClickListener): Button {
        val button = Button(this)
        button.setText(textResId)
        button.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f)
        button.setOnClickListener(listener)
        return button
    }

    private fun createValueRow(index: Int, participantName: String): LinearLayout {
        val amount: Int = splitValues[index]

        val nameView = createNameView(participantName)
        val top = LinearLayout(this@SplitActivity)

        top.setPadding(NumberUtil.getDimension(R.dimen.padding_split_table_top_left, this),
                NumberUtil.getDimension(R.dimen.padding_split_table_top_top, this),
                NumberUtil.getDimension(R.dimen.padding_split_table_top_right, this),
                NumberUtil.getDimension(R.dimen.padding_split_table_top_bottom, this))
        top.addView(nameView)

        val editAmount = createEditAmount(amount, splitValues, index)
        top.addView(editAmount)
        return top
    }

    companion object {

        fun call(context: Activity, participantNames: Array<String>, existingSplits: IntArray, amount: Int) {
            val intent = Intent(context, SplitActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_FELLOWS, participantNames)
            intent.putExtra(InterfaceConstants.PARAM_SPLITS, existingSplits)
            intent.putExtra(InterfaceConstants.PARAM_AMOUNT, amount)
            context.startActivityForResult(intent, InterfaceConstants.RC_SPLIT)
        }
    }
}
