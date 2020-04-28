package ru.spbstu.gyboml.lobby

import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.gyboml.core.net.SessionInfo

class ButtonAdapter : RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {
    class ButtonViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    private var touchEnabled = true
    lateinit var onClickListener: OnClickListener

    private var sessions = listOf<SessionInfo>()
    val positionMapping = mutableMapOf<Int, Int>()
    var chosenSessionId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val button = Button(parent.context)

        val lp = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        button.layoutParams = lp
        button.isEnabled = touchEnabled
        button.setOnClickListener(onClickListener)
        return ButtonViewHolder(button)
    }
    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val info = sessions[position]
        with (holder.button) {
            isEnabled = touchEnabled
            text = "#${info.id} ${info.name} (${2 - info.spaces} / 2)"
            id = info.id
        }
    }
    override fun getItemCount() = sessions.size

    fun update(sessions: List<SessionInfo>) {
        this.sessions = sessions
        notifyDataSetChanged()
    }
    fun enableTouch() { touchEnabled = true; notifyDataSetChanged() }
    fun disableTouch() { touchEnabled = false; notifyDataSetChanged() }
    fun getSession(id: Int) : SessionInfo? = sessions.find {s -> s.id == id}
}