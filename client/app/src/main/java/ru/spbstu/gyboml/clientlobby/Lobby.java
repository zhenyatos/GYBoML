package main.java.ru.spbstu.gyboml.clientlobby;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import ru.spbstu.gyboml.R;

public class Lobby extends AppCompatActivity {

    private RecyclerView gameSessionsView;
    private ButtonAdapter sessionsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button createNewSessionButton;
    private String sessionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        gameSessionsView = findViewById(R.id.gameSessions);
        createNewSessionButton = findViewById(R.id.createSession);

        createNewSessionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context currentContext = createNewSessionButton.getContext();
                    createDialogueWindow();
                }
        });
        layoutManager = new LinearLayoutManager(this);
        gameSessionsView.setLayoutManager(layoutManager);

        sessionsAdapter = new ButtonAdapter();
        sessionsAdapter.add("game_1");
        sessionsAdapter.add("game_2");
        gameSessionsView.setAdapter(sessionsAdapter);


    }
    private Button createSessionButton() {
        Button sessionButton = new Button(this);
        sessionButton.setText("session");
        return sessionButton;
    }

    private void createDialogueWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Create new session");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sessionName = input.getText().toString();
                //Send message to server
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
