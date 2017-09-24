package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_split.*
import kotlinx.android.synthetic.main.content_split.*
import org.butterbrot.heve.ubill.util.NumberUtil

class SplitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split)
        setSupportActionBar(toolbar)

        val existingSplits: IntArray = intent.getIntArrayExtra(InterfaceConstants.PARAM_SPLITS)
        val participantNames: Array<String> = intent.getStringArrayExtra(InterfaceConstants.PARAM_FELLOWS)
        val totalAmount: Int = intent.getIntExtra(InterfaceConstants.PARAM_AMOUNT, 0)

        val allSplits = existingSplits.reduceRight { i, acc -> acc + i }

        participantNames.forEachIndexed { index, participantName ->
            val amount: Int = existingSplits[index]

            val nameView = TextView(this)
            nameView.text = participantName
            nameView.gravity = Gravity.START
            nameView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            val top = LinearLayout(this@SplitActivity)

            top.setPadding(NumberUtil.getDimension(R.dimen.padding_split_table_top_left, this),
                    NumberUtil.getDimension(R.dimen.padding_split_table_top_top, this),
                    NumberUtil.getDimension(R.dimen.padding_split_table_top_right, this),
                    NumberUtil.getDimension(R.dimen.padding_split_table_top_bottom, this))
            top.addView(nameView)

            val editView = EditText(this@SplitActivity)
            editView.setText(NumberUtil.toText(amount))
            editView.gravity = Gravity.END
            top.addView(editView)

            val bottom = LinearLayout(this@SplitActivity)
            val distributeButton = Button(this)
            distributeButton.setText(R.string.label_split_distribute)
            distributeButton.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f)
            val claimButton = Button(this)
            claimButton.setText(R.string.label_split_claim)
            claimButton.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f)

            bottom.addView(distributeButton)
            bottom.addView(claimButton)

            val separator: ImageView = ImageView(this)
            separator.setImageResource(R.drawable.separator)
            separator.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val wrapper = LinearLayout(this@SplitActivity)
            wrapper.orientation = LinearLayout.VERTICAL
            wrapper.addView(separator)
            wrapper.addView(top)
            wrapper.addView(bottom)
            wrapper.setPadding(0,
                    NumberUtil.getDimension(R.dimen.padding_split_table_bottom_top, this),
                    0,
                    0)
            splits.addView(wrapper)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds itemsParam to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_split, menu)
        return true
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
