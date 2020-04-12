package main.java.ru.spbstu.gyboml.clientlobby;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.spbstu.gyboml.R;


public class Lobby extends AppCompatActivity {

    private RecyclerView gameSessionsView;
    private ButtonAdapter sessionsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button createNewSessionButton;
    private Button readyButton;
    private Button exitButton;
    private String chosenSessionName;
    private int chosenSessionID;

    private PlayerStatus playerStatus = PlayerStatus.FREE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        gameSessionsView = findViewById(R.id.gameSessions);
        createNewSessionButton = findViewById(R.id.createSession);
        readyButton = findViewById(R.id.ready);
        exitButton = findViewById(R.id.exit);

        readyButton.setOnClickListener(getReadyButtonListener());
        exitButton.setOnClickListener(getExitButtonListener());
        createNewSessionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    createDialogueWindow();
                }
        });

        layoutManager = new LinearLayoutManager(this);
        gameSessionsView.setLayoutManager(layoutManager);

        sessionsAdapter = new ButtonAdapter();
        sessionsAdapter.setOnClickListener(getSessionButtonListener());
        sessionsAdapter.add(0, "game_1"); //temporary
        sessionsAdapter.add(0, "game_2"); //temporary
        gameSessionsView.setAdapter(sessionsAdapter);


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
                chosenSessionName = input.getText().toString();
                //Send message to server, retrieve session's ID
                playerStatus = PlayerStatus.SESSIONCREATED;
                hideNewSessionButton();
                sessionsAdapter.add(0, chosenSessionName); //temporary
                sessionsAdapter.disableTouch();
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

    private void hideNewSessionButton() {
        createNewSessionButton.setVisibility(View.INVISIBLE);
      //  gameSessionsView.setVisibility(View.INVISIBLE);
        readyButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);
    }

    private void showNewSessionButton() {
        createNewSessionButton.setVisibility(View.VISIBLE);
      //  gameSessionsView.setVisibility(View.VISIBLE);
        readyButton.setVisibility(View.INVISIBLE);
        exitButton.setVisibility(View.INVISIBLE);
    }

    private enum PlayerStatus {
        FREE,
        SESSIONCREATED,
        SESSIONJOINED
    }

    private View.OnClickListener getSessionButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //Send message to server
                playerStatus = PlayerStatus.SESSIONJOINED;
                sessionsAdapter.disableTouch();
                chosenSessionID = v.getId();
                hideNewSessionButton();
            }
        };
    }

    private View.OnClickListener getExitButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                switch(playerStatus) {
                    case SESSIONJOINED:
                        //tell server to remove player from session
                        break;
                    case SESSIONCREATED:
                        //tell server to destroy session
                        sessionsAdapter.remove(chosenSessionID); //temporary
                        break;
                }
                playerStatus = PlayerStatus.FREE;
                sessionsAdapter.enableTouch();
                showNewSessionButton();
            }
        };
    }

    private View.OnClickListener getReadyButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //send message to server
            }
        };
    }

    void JoinSession(int ID) {
        hideNewSessionButton();
        playerStatus = PlayerStatus.SESSIONJOINED;
    }

    void leaveSession() {
        showNewSessionButton();
        playerStatus = PlayerStatus.FREE;
    }

}
