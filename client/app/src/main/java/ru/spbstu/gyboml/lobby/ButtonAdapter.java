package main.java.ru.spbstu.gyboml.lobby;

import static android.view.ViewGroup.LayoutParams;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.spbstu.gyboml.core.net.SessionInfo;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {
    List<SessionInfo> sessions;
    private final HashMap<Integer, Integer> IDToPosMap = new HashMap<>();
    private boolean touchEnabled = true;
    private View.OnClickListener onClickListener;

    Integer chosenSessionID = null;

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
        holder.sessionButton.setText(session.name +" : " + (2 - session.spaces) + " / 2");
        holder.sessionButton.setId(session.sessionId);
    }

    @Override
    public int getItemCount() {
        if (sessions == null) return 0;

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

    public SessionInfo findSessionByID(int ID) {
        return sessions.stream().filter(session -> session.sessionId  == ID).findFirst().get();
    }
}
