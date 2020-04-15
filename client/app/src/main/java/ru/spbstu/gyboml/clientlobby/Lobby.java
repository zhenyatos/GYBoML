package main.java.ru.spbstu.gyboml.clientlobby;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import main.java.ru.spbstu.gyboml.MainActivity;
import main.java.ru.spbstu.gyboml.clientmenu.MainMenu;
import ru.spbstu.gyboml.R;


public class Lobby extends AppCompatActivity {

    //Objects that handle the visual representation of the session list
    private RecyclerView gameSessionsView;
    private ButtonAdapter sessionsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //Buttons
    private ImageButton createNewSessionButton;
    private ToggleButton readyButton;
    private ImageButton exitButton;
    private ImageButton refreshButton;

    //User info
    private String chosenSessionName;
    private int chosenSessionID;
    private String username;

    private PlayerStatus playerStatus = PlayerStatus.FREE;

    //Initializes the Lobby, establishes a connection with the server (not done yet), etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //Get user info from the main menu
        Intent intent = getIntent();
        username = intent.getStringExtra(MainMenu.PLAYER_NAME);

        gameSessionsView = findViewById(R.id.gameSessions);
        createNewSessionButton = findViewById(R.id.createSession);
        readyButton = findViewById(R.id.ready);
        exitButton = findViewById(R.id.exit);
        refreshButton = findViewById(R.id.refreshButton);

        //Set up the game session list
        layoutManager = new LinearLayoutManager(this);
        gameSessionsView.setLayoutManager(layoutManager);
        sessionsAdapter = new ButtonAdapter();
        gameSessionsView.setAdapter(sessionsAdapter);

        //Set up button listeners
        readyButton.setOnCheckedChangeListener(getReadyButtonListener());
        exitButton.setOnClickListener(getExitButtonListener());
        createNewSessionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    createDialogueWindow();
                }
        });
        refreshButton.setOnClickListener(getRefreshButtonListener());

        //Set up the listener for game session buttons
        sessionsAdapter.setOnClickListener(getSessionButtonListener());

    }

    //Dialogue window for the createSession listener
    private void createDialogueWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Create new session");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //Player confirmed creation of a session
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chosenSessionName = input.getText().toString();
                //Send message to server, retrieve session's ID
                playerStatus = PlayerStatus.SESSIONJOINED; //Move to Lobby's listener
                sessionsAdapter.disableTouch();             //Move to Lobby's listener
                inSessionView();                              //Move to Lobby's listener
            }
        });

        //Player denied creation of a session
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //Removes creation button, makes ready and exit buttons visible
    private void inSessionView() {
        createNewSessionButton.setVisibility(View.GONE);
        readyButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);
    }

    //Removes ready and exit buttons, adds creation button
    private void notInSessionView() {
        createNewSessionButton.setVisibility(View.VISIBLE);
        readyButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
    }

    private enum PlayerStatus {
        FREE,
        SESSIONJOINED
    }

    //JOIN SESSION BUTTON LISTENER
    private View.OnClickListener getSessionButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                chosenSessionID = v.getId();
                //Send message to server
                playerStatus = PlayerStatus.SESSIONJOINED; //Move to Lobby's listener
                sessionsAdapter.disableTouch(); //Move to Lobby's listener
                inSessionView(); //Move to Lobby's listener
            }
        };
    }

    //EXIT BUTTON LISTENER
    private View.OnClickListener getExitButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //tell server to remove player from session
                playerStatus = PlayerStatus.FREE; //Move to Lobby's listener
                sessionsAdapter.enableTouch(); //Move to Lobby's listener
                notInSessionView(); //Move to Lobby's listener
            }
        };
    }

    //REFRESH SESSIONS LISTENER
    private View.OnClickListener getRefreshButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //send message to server
            }
        };
    }

    //READY BUTTON LISTENER
    private ToggleButton.OnCheckedChangeListener getReadyButtonListener() {
        return new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Send message to server, player is not ready anymore
                }
                else {
                    //Send message to server, player is ready now
                }
            }
        };
    }

    //Starts up the game, duh
    private void gameStartUp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class); //Move to Lobby's listener
        startActivity(intent);
    }

    void joinSession(int ID) {
        inSessionView();
        playerStatus = PlayerStatus.SESSIONJOINED;
    }

    void leaveSession() {
        notInSessionView();
        playerStatus = PlayerStatus.FREE;
    }

}
