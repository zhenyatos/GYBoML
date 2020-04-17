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
import android.text.Layout;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

import main.java.ru.spbstu.gyboml.MainActivity;
import main.java.ru.spbstu.gyboml.clientmenu.MainMenu;
import ru.spbstu.gyboml.R;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.Network;
import ru.spbstu.gyboml.core.net.Requests;


public class Lobby extends AppCompatActivity {

    //Objects that handle the visual representation of the session list
    private RecyclerView gameSessionsView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton createNewSessionButton;
    private ImageButton refreshButton;
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
    Player player;

    private Client client;

    //Initializes the Lobby, establishes a connection with the server (not done yet), etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // network client creation
        client = new Client();
        client.start();

        Network.register(client);

        Thread clientThread = new Thread("Connect") {
            @Override
            public void run() {
                try { client.connect(5000, Network.serverAddress, Network.tcpPort/*, Network.udpPort*/); }
                catch (IOException error) { error.printStackTrace(); }
            }
        };

        client.addListener(new SessionListener(this));

        //Get user info from the main menu
        Intent intent = getIntent();
        chosenPlayerName = intent.getStringExtra(MainMenu.PLAYER_NAME);

        gameSessionsView = findViewById(R.id.gameSessions);
        createNewSessionButton = findViewById(R.id.createSession);
        readyButton = findViewById(R.id.ready);
        exitButton = findViewById(R.id.exit);
        refreshButton = findViewById(R.id.refreshButton);

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
        refreshButton.setOnClickListener(getRefreshButtonListener());

        //Set up the listener for game session buttons
        sessionsAdapter.setOnClickListener(getSessionButtonListener());

        clientThread.start();
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

                sendTCP(createSession);
                sendTCP(new Requests.GetSessions());
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
                sendTCP(request);
            }
        };
    }

    //EXIT BUTTON LISTENER
    private View.OnClickListener getExitButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //tell server to remove player from session
                Requests.ExitSession request = new Requests.ExitSession();
                request.player = player;
                sendTCP(request);
            }
        };
    }

    //REFRESH SESSIONS LISTENER
    private View.OnClickListener getRefreshButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                sendTCP(new Requests.GetSessions());
            }
        };
    }

    //READY BUTTON LISTENER
    private ToggleButton.OnCheckedChangeListener getReadyButtonListener() {
        return new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Requests.Ready request = new Requests.Ready();
                request.player = player;
                sendTCP(request);
            }
        };
    }

    //Starts up the game, duh
    private void gameStartUp() {
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

    private void sendTCP( Object object ) {
        new Thread("Handle") {
            @Override
            public void run() {
                client.sendTCP(object);
            }
        }.start();
    }

}
