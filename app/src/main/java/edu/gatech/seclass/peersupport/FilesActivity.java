package edu.gatech.seclass.peersupport;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilesActivity extends AppCompatActivity {
    FirebaseStorage storage;
    Bundle bundle;
    String username;
    Context context;
    List<String> imageFilenames;
    List<Uri> imageUris;
    ArrayAdapter<String> imageArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString().toLowerCase();
        }
        // button to take user to main menu ---------------------------------------------------------------------------
        Button mainButton = (Button) findViewById(R.id.mainmenu);
        mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(FilesActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------

        context = getApplicationContext();
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        imageFilenames = new ArrayList<>();
        imageUris = new ArrayList<>();
        ListView imageFilesListView = findViewById(R.id.imageFilesList);
        imageArrayAdapter = new ArrayAdapter<String>(
                FilesActivity.this,
                android.R.layout.simple_list_item_1,
                imageFilenames);

        StorageReference imagesRef = storageRef.child("PeerSupport_images");
        //username.toLowerCase()
        StorageReference userImagesRef = imagesRef.child(username.toLowerCase());
        userImagesRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                Log.v("list results", Arrays.toString(task.getResult().getPrefixes().toArray()));
                for (StorageReference ref : task.getResult().getPrefixes()) {
                    final int[] size = new int[1];
                    ref.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ListResult> task) {
                            size[0] = task.getResult().getItems().size();
                          for (StorageReference itemRef : task.getResult().getItems()) {
                              Log.v("item ref", itemRef.getName()+ "|||" +itemRef.getPath()+ "|||" +itemRef.getBucket());
                              itemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Uri> task) {
                                      imageFilenames.add(itemRef.getName() + " - " + itemRef.getPath().replace(itemRef.getName(), "").replace(username,""));
                                      imageUris.add(task.getResult());
                                      Log.v("FilesActivity line 91", LocalDateTime.now()+"");
                                  }
                              }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Uri> task) {
                                      if (imageFilenames.size() == size[0]) {
                                          imageFilesListView.setAdapter(imageArrayAdapter);

                                          imageFilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                              @Override
                                              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                  Uri uri = imageUris.get(i);
                                                  File file = new File(uri.getPath());
                                                  Log.v("uri here", uri+"");
                                                  DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                                  DownloadManager.Request request = new DownloadManager.Request(uri);
                                                  request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
                                                  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                  downloadmanager.enqueue(request);
                                              }
                                          });
                                          Log.v("FilesActivity line 119", LocalDateTime.now()+"");
                                      }
                                  }
                              }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Uri> task) {
                                      imageArrayAdapter.notifyDataSetChanged();
                                  }
                              });

                          }

                        }
                    });
                    Log.v("FilesActivity line 127", LocalDateTime.now()+"");
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                Log.v("FilesActivity line 122", LocalDateTime.now()+"");
            }
        });
        Log.v("FilesActivity line 131", LocalDateTime.now()+"");
    }
}