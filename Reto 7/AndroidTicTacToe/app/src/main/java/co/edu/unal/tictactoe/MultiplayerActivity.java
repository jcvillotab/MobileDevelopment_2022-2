package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultiplayerActivity extends AppCompatActivity {

    List gameList = new ArrayList<Game>();
    List keyGameList = new ArrayList<String>();
    EditText playerName;
    String uuid = UUID.randomUUID().toString();
    RecyclerView reyclerViewGame;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference gamesRef = database.getReference("games");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        playerName = (EditText) findViewById(R.id.player_name);

        final Context myContext = this;

        Button buttonNewGame = (Button) findViewById(R.id.button_newgame);

        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myContext, MainActivity.class);
                if(playerName.getText().toString().trim().length() > 0){
                    uuid = playerName.getText().toString();
                }
                Game game = new Game(uuid);
                String keyGame = gamesRef.push().getKey();
                gamesRef.child(keyGame).setValue(game);
                intent.putExtra("uuidPlayer", uuid);
                intent.putExtra("keyGame", keyGame);
                intent.putExtra("isChallengingPlayer", "0");
                myContext.startActivity(intent);
            }
        });

        gamesRef.orderByChild("state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                gameList.clear();
                keyGameList.clear();

                for (DataSnapshot gameSnapshot: snapshot.getChildren()) {
                    Game game = gameSnapshot.getValue(Game.class);


                    if(!game.state.equals("finalized")){
                        gameList.add(game);
                        keyGameList.add(gameSnapshot.getKey());
                        System.out.println(game.uuidDefendingPlayer);
                    }
                }

                reyclerViewGame = (RecyclerView) findViewById(R.id.recyclerViewGame);
                reyclerViewGame.setHasFixedSize(true);
                reyclerViewGame.setLayoutManager(new LinearLayoutManager(myContext));

                GameAdapter mAdapter = new GameAdapter(uuid, gameList, keyGameList);
                reyclerViewGame.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }
}