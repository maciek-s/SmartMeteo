package com.masiad.smartmeteo.ui.info

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.utils.UP_SENSOR_SERIAL_NUMBER

/**
 * Info [Fragment]
 */
class InfoFragment : Fragment() {
    companion object {
        val TAG: String = InfoFragment::class.java.simpleName
    }

    private lateinit var infoViewModel: InfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        infoViewModel =
            ViewModelProviders.of(this).get(InfoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_info, container, false)

        val textView: TextView = root.findViewById(R.id.upInfoTextView)
        val upInfo = String.format(resources.getString(R.string.info_sensor_up), UP_SENSOR_SERIAL_NUMBER)
        textView.text = Html.fromHtml(upInfo, Html.FROM_HTML_MODE_COMPACT)

        return root
    }
}
