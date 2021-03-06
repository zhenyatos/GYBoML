package ru.spbstu.gyboml.menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.spbstu.gyboml.MainActivityOffline;
import ru.spbstu.gyboml.lobby.Lobby;
import ru.spbstu.gyboml.R;

public class MainMenu extends AppCompatActivity {
    //Buttons
    private Button startButton;
    private Button exitButton;
    private Button aboutButton;

    //Name of the field that contains player's name and gets transferred on Lobby's startup
    public static final String PLAYER_NAME = "ru.spbstu.gyboml.clientmenu.PLAYER_NAME";

    //Initializes the MainMenu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        startButton = findViewById(R.id.startButton);
        exitButton = findViewById(R.id.exitButton);
        aboutButton = findViewById(R.id.aboutButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createDialogueWindow();
                Intent intent = new Intent(getApplicationContext(), MainActivityOffline.class);
                startActivity(intent);
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAboutWindow();
            }
        });
    }

    private void createDialogueWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Pick a username:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playerName = input.getText().toString();
                Intent intent = new Intent(getApplicationContext(), Lobby.class);
                intent.putExtra(PLAYER_NAME, playerName);
                startActivity(intent);
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

    private void createAboutWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("About GYBoML");
        builder.setMessage("A two-player game for Android. The goal is the destruction of " +
                "your opponent's base by bombarding it with various missiles, and the protection of " +
                "your own base by shielding it with constructions made of destructible blocks.");

        builder.setPositiveButton("OK", null);

        builder.show();
    }
}
