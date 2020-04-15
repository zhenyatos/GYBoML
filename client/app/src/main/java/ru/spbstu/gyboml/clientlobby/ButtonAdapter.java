package main.java.ru.spbstu.gyboml.clientlobby;

import static android.view.ViewGroup.LayoutParams;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import ru.spbstu.gyboml.core.net.SessionInfo;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {
    private ArrayList<SessionInfo> sessions;
    private final HashMap<Integer, Integer> IDToPosMap = new HashMap<>();
    private boolean touchEnabled = true;
    private View.OnClickListener onClickListener;

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        public Button sessionButton;
        public ButtonViewHolder(Button button) {
            super(button);
            sessionButton = button;
        }
    }

    void update(ArrayList<SessionInfo> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }


    ButtonAdapter() {
       sessions = new ArrayList<>();
       sessions.add(new SessionInfo("EAT MY PANTS", 150, 2, null, null));
       sessions.add(new SessionInfo("DRINK MY PANTS", 151, 2, null, null));
       sessions.add(new SessionInfo("SNIFF MY PANTS", 152, 2, null, null));
    }


    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ButtonAdapter.ButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Button button = new Button(parent.getContext());

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(lp);
        button.setEnabled(touchEnabled);
        button.setOnClickListener(onClickListener);

        return new ButtonViewHolder(button);
    }

    @Override
    public void onBindViewHolder(ButtonViewHolder holder, int position) {
        SessionInfo session = sessions.get(position);
        holder.sessionButton.setEnabled(touchEnabled);
        holder.sessionButton.setText(session.name());
        holder.sessionButton.setId(session.sessionId());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void enableTouch() {
        touchEnabled = true;
        notifyDataSetChanged();
    }

    public void disableTouch() {
        touchEnabled = false;
        notifyDataSetChanged();
    }
}
