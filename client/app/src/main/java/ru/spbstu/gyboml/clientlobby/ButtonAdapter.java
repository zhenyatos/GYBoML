package main.java.ru.spbstu.gyboml.clientlobby;

import static android.view.ViewGroup.LayoutParams;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

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

    void add(int ID, String sessionName) {
        sessions.add(new SessionInfo(ID, sessionName));
        IDToPosMap.put(ID, sessions.size() - 1);
        notifyItemInserted(sessions.size() - 1);
    }

    void remove(int ID) {
        int pos = IDToPosMap.get(ID);
        sessions.remove(pos);
        IDToPosMap.remove(ID);
        notifyItemRemoved(pos);
    }

    ButtonAdapter() {
       sessions = new ArrayList<>();
    }

    public ButtonAdapter(int[] IDs, String[] names) {
        this();
        for (int i = 0; i < IDs.length; i++) {
            sessions.add(new SessionInfo(IDs[i], names[i]));
            IDToPosMap.put(IDs[i], i);
        }

    }

    public ButtonAdapter(ArrayList<SessionInfo> sessions) {
        this.sessions = sessions;
        for (int i = 0; i < sessions.size(); i++) {
            IDToPosMap.put(sessions.get(i).ID, i);
        }
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
        holder.sessionButton.setText(session.name);
        holder.sessionButton.setId(session.ID);
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
