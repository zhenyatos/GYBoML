package main.java.ru.spbstu.gyboml.clientlobby;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import main.java.ru.spbstu.gyboml.GybomlClient;
import main.java.ru.spbstu.gyboml.MainActivity;
import main.java.ru.spbstu.gyboml.clientmenu.MainMenu;
import ru.spbstu.gyboml.R;
import ru.spbstu.gyboml.core.net.Requests;


public class Lobby extends AppCompatActivity {

    //Objects that handle the visual representation of the session list
    private RecyclerView gameSessionsView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton createNewSessionButton;
    private ConstraintLayout inSessionLayout;

    ButtonAdapter sessionsAdapter;
    ToggleButton readyButton;
    ImageButton exitButton;
    ImageView firstPlayerReady;
    ImageView secondPlayerReady;
    TextView firstPlayerName;
    TextView secondPlayerName;

    //User info
    String chosenSessionName;
    String chosenPlayerName;

    // player info
    PlayerStatus playerStatus = PlayerStatus.FREE;

    //Constants describing names of the extras transferred to the game client
    public static final String clientExtraName = "CLIENT_JSON";
    public static final String playerExtraName = "PLAYER_JSON";

    //Initializes the Lobby, establishes a connection with the server (not done yet), etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //Get user info from the main menu
        Intent intent = getIntent();
        chosenPlayerName = intent.getStringExtra(MainMenu.PLAYER_NAME);

        gameSessionsView = findViewById(R.id.gameSessions);
        createNewSessionButton = findViewById(R.id.createSession);
        readyButton = findViewById(R.id.ready);
        exitButton = findViewById(R.id.exit);

        firstPlayerReady = findViewById(R.id.firstPlayerReady);
        secondPlayerReady = findViewById(R.id.secondPlayerReady);
        firstPlayerName = findViewById(R.id.firstPlayerName);
        secondPlayerName = findViewById(R.id.secondPlayerName);

        //Set up the game session list
        layoutManager = new LinearLayoutManager(this);
        gameSessionsView.setLayoutManager(layoutManager);
        sessionsAdapter = new ButtonAdapter();
        gameSessionsView.setAdapter(sessionsAdapter);

        inSessionLayout = findViewById(R.id.inSessionLayout);

        //Set up button listeners
        readyButton.setOnCheckedChangeListener(getReadyButtonListener());
        exitButton.setOnClickListener(getExitButtonListener());
        createNewSessionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    createDialogueWindow();
                }
        });

        //Set up the listener for game session buttons
        sessionsAdapter.setOnClickListener(getSessionButtonListener());

        GybomlClient.connect(new SessionListener(this));
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
                Requests.CreateSession createSession = new Requests.CreateSession();
                createSession.sessionName = input.getText().toString().trim();

                GybomlClient.sendTCP(createSession);
                GybomlClient.sendTCP(new Requests.GetSessions());
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
    void inSessionView() {
        gameSessionsView.setVisibility(View.INVISIBLE);
        inSessionLayout.setVisibility(View.VISIBLE);
        createNewSessionButton.setVisibility(View.GONE);
        readyButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);
    }

    //Removes ready and exit buttons, adds creation button
    void notInSessionView() {
        gameSessionsView.setVisibility(View.VISIBLE);
        inSessionLayout.setVisibility(View.INVISIBLE);
        createNewSessionButton.setVisibility(View.VISIBLE);
        readyButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
    }

    enum PlayerStatus {
        FREE,
        SESSIONJOINED
    }

    //JOIN SESSION BUTTON LISTENER
    private View.OnClickListener getSessionButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                sessionsAdapter.chosenSessionID = v.getId();
                Requests.ConnectSession request = new Requests.ConnectSession();
                request.sessionId = sessionsAdapter.chosenSessionID;
                GybomlClient.sendTCP(request);
            }
        };
    }

    //EXIT BUTTON LISTENER
    private View.OnClickListener getExitButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //tell server to remove player from session
                Requests.ExitSession request = new Requests.ExitSession();
                request.player = GybomlClient.getPlayer();
                GybomlClient.sendTCP(request);
            }
        };
    }


    //READY BUTTON LISTENER
    private ToggleButton.OnCheckedChangeListener getReadyButtonListener() {
        return new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Requests.Ready request = new Requests.Ready();
                request.player = GybomlClient.getPlayer();
                GybomlClient.sendTCP(request);
            }
        };
    }

    //Starts up the game, duh
    void gameStartUp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
