package main.java.ru.spbstu.gyboml.clientlobby;

import static android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {
    private ArrayList<String> sessions;

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        public Button sessionButton;
        public ButtonViewHolder(Button button) {
            super(button);
            sessionButton = button;
        }
    }

    public void add(String sessionName) {
        sessions.add(sessionName);
    }

    public ButtonAdapter() {
       sessions = new ArrayList<>();
    }



    @Override
    public ButtonAdapter.ButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Button button = new Button(parent.getContext());

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(lp);

        return new ButtonViewHolder(button);
    }

    @Override
    public void onBindViewHolder(ButtonViewHolder holder, int position) {
        holder.sessionButton.setText(sessions.get(position));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
