package com.example.shrey.donna;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.microsoft.cognitiveservices.luis.clientlibrary.LUISClient;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISEntity;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISIntent;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISResponse;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISResponseHandler;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    List<Message> input = new ArrayList<Message>();
    Message msg;
    EditText editText;
    String appID = "95292b89-fada-46fa-9832-16c66c35559e";
    String appKey = "5b48b6e986584718854fd590d4b2c836";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.messages_view);
        editText = (EditText) findViewById(R.id.edit_query);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MessageAdapter(input);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        recyclerView.setAdapter(mAdapter);
    }
    public void sendMessage(View view){
        if(editText.getText().toString().length()>0) {
            msg = new Message(editText.getText().toString(), true);
            input.add(msg);
            editText.getText().clear();

            try{
                LUISClient client = new LUISClient(appID, appKey, true);
                client.predict(msg.getText(), new LUISResponseHandler() {
                    @Override
                    public void onSuccess(LUISResponse response) {
                        processResponse(response);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Bulbasaur","failed");
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            msg = new Message("ok!", false);
            input.add(msg);
            recyclerView.swapAdapter(mAdapter, false);
        }
    }

    public void processResponse(LUISResponse response){
        LUISIntent topIntent = response.getTopIntent();
        String intent = topIntent.getName().toLowerCase();
        Message res;
        List <LUISEntity> entities = response.getEntities();
        switch(intent){
            case "play music":
                res = new Message("Playing Music", false);
                input.add(res);
                recyclerView.swapAdapter(mAdapter, false);
                Intent music = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                if(entities.size() == 1){
                    LUISEntity entity = entities.get(0);
                    if(entity.getType().equals("Song")){
                        music.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/audio");
                        music.putExtra(MediaStore.EXTRA_MEDIA_TITLE, entity.getName());
                        music.putExtra(SearchManager.QUERY, entity.getName());
                        if(music.resolveActivity(getPackageManager())!= null) {
                            startActivity(music);
                        }
                    }
                    else if(entity.getType().equals("Song Artist")){
                        music.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
                        music.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, entity.getName());
                        music.putExtra(SearchManager.QUERY, entity.getName());
                        if(music.resolveActivity(getPackageManager())!= null) {
                            startActivity(music);
                        }
                    }

                    else if(entity.getType().equals("Song Album")){
                        music.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE);
                        music.putExtra(MediaStore.EXTRA_MEDIA_ALBUM, entity.getName());
                        music.putExtra(SearchManager.QUERY, entity.getName());
                        if(music.resolveActivity(getPackageManager())!= null) {
                            startActivity(music);
                        }
                    }
                    else if(entity.getType().equals("Song Playlist")){
                        music.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE);
                        music.putExtra(SearchManager.QUERY, entity.getName());
                        if(music.resolveActivity(getPackageManager())!= null) {
                            startActivity(music);
                        }
                    }
                }
                else if(entities.size() == 2){
                    music.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/audio");
                    music.putExtra(MediaStore.EXTRA_MEDIA_TITLE, entities.get(0).getName());
                    music.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, entities.get(1).getName());
                    music.putExtra(SearchManager.QUERY, entities.get(0).getName());
                    if(music.resolveActivity(getPackageManager())!= null) {
                        startActivity(music);
                    }
                }
                break;

                //alarm
            case "create an alarm":

                break;
            case "call someone":
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{ ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor people = getContentResolver().query(uri, projection, null, null, null);
                int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name, number;
                people.moveToFirst();
                do{
                    name = people.getString(indexName);
                    number = people.getString(indexNumber);
                    if(name.equalsIgnoreCase(entities.get(0).getName())){
                        number = number.replace("-", "");
                        break;
                    }
                }while(people.moveToNext());
                res = new Message("Calling...", false);
                input.add(res);
                recyclerView.swapAdapter(mAdapter, false);
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:"+ number));
                if(call.resolveActivity(getPackageManager())!=null){
                    startActivity(call);
                }


                break;
        }

    }
}