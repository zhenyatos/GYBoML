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

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

import main.java.ru.spbstu.gyboml.MainActivity;
import main.java.ru.spbstu.gyboml.clientmenu.MainMenu;
import ru.spbstu.gyboml.R;
import ru.spbstu.gyboml.core.net.Network;
import ru.spbstu.gyboml.core.net.Requests;


public class Lobby extends AppCompatActivity {

    //Objects that handle the visual representation of the session list
    private RecyclerView gameSessionsView;
    ButtonAdapter sessionsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //Buttons
    private ImageButton createNewSessionButton;
    private ToggleButton readyButton;
    ImageButton exitButton;
    private ImageButton refreshButton;

    //User info
    private String chosenSessionName;
    private String username;

    PlayerStatus playerStatus = PlayerStatus.FREE;

    private Client client;

    //Initializes the Lobby, establishes a connection with the server (not done yet), etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // network client creation
        client = new Client();
        client.start();

        client.addListener(new SessionListener(this));

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

        new Thread(() -> {
            try {
                client.connect(5000, Network.serverAddress, Network.tcpPort, Network.udpPort);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }).start();
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
                Requests.CreateSession request = new Requests.CreateSession();
                request.sessionName = input.getText().toString().trim();
                client.sendTCP(request);
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
        createNewSessionButton.setVisibility(View.GONE);
        readyButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);
    }

    //Removes ready and exit buttons, adds creation button
    void notInSessionView() {
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
                client.sendTCP(request);
            }
        };
    }

    //EXIT BUTTON LISTENER
    private View.OnClickListener getExitButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //tell server to remove player from session
                Requests.ExitSession request = new Requests.ExitSession();
                request.sessionId = sessionsAdapter.chosenSessionID;
                client.sendTCP(request);
            }
        };
    }

    //REFRESH SESSIONS LISTENER
    private View.OnClickListener getRefreshButtonListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                client.sendTCP(new Requests.GetSessions());
            }
        };
    }

    //READY BUTTON LISTENER
    private ToggleButton.OnCheckedChangeListener getReadyButtonListener() {
        return new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Requests.Ready request = new Requests.Ready();
                    request.isReady = false;
                    client.sendTCP(request);
                }
                else {
                    Requests.Ready request = new Requests.Ready();
                    request.isReady = true;
                    client.sendTCP(request);
                }
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

}
