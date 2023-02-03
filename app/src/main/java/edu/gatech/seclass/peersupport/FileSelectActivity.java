package edu.gatech.seclass.peersupport;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileSelectActivity extends AppCompatActivity {
    private File privateRootDir;
    private File imagesDir;
    private File pdfDir;
    File[] imageFiles;
    ArrayList<String> imageFilenames;
    File[] pdfFiles;
    Intent resultIntent;
    String username;
    Bundle bundle;
    String contactUsername;
    String contactUsernameTwo;
    ListView imageFilesListView;
    boolean isVersion11AndAbove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString().toLowerCase();
        }
        String[] names = bundle.get("Contact_Username").toString().split("`");
        contactUsername = names[0];
        if (bundle.get("Contact_Username_Two") != null) {
            contactUsernameTwo = bundle.get("Contact_Username_Two").toString();
        }

        // button to take user to main menu ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(FileSelectActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.v("Permissions", Environment.isExternalStorageManager() + "");
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
            isVersion11AndAbove = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 0);
            isVersion11AndAbove = false;
        }

        resultIntent =
                new Intent("edu.gatech.seclass.peersupport.FileSelectActivity.ACTION_RETURN_FILE");

        if (isVersion11AndAbove) {
            imagesDir = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/");

        } else {
            imagesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Camera/");
            //imagesDir = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/");
        }

        Log.v("Images Directory", imagesDir + "");
        imageFiles = imagesDir.listFiles();
        Log.v("Images under Directory", Arrays.toString(imageFiles) + "");
        imageFilenames = new ArrayList<>();
        //Log.v("trying something", imagesDir.list().length + "");

        imageFilesListView = (ListView) findViewById(R.id.imageFilesList);
        if (imageFiles != null) {
            for (File image : imageFiles) {
                imageFilenames.add(image.getAbsolutePath());
            }
        }

        pdfDir = new File(privateRootDir, "application/pdf");
        pdfFiles = pdfDir.listFiles();
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null); //Change this later maybe

        ArrayAdapter<String> imageArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                imageFilenames);
        imageFilesListView.setAdapter(imageArrayAdapter);

        imageFilesListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                    /*
                     * Get a File for the selected file name.
                     * Assume that the file names are in the
                     * imageFilename array.
                     */
                    File requestFile = new File(imageFilenames.get(position));
                    /*
                     * Most file-related method calls need to be in
                     * try-catch blocks.
                     */
                    // Use the FileProvider to get a content URI
                    try {
                        /*Uri fileUri = FileProvider.getUriForFile(FileSelectActivity.this,
                                "edu.gatech.seclass.peersupport.FileSelectActivity.fileprovider",
                                requestFile);*/
                        Uri fileUri = Uri.fromFile(requestFile);
                        if (fileUri != null) {
                            resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            resultIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));

                            Intent intent = new Intent(FileSelectActivity.this, ConfirmFileSelectActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("Contact_Username",contactUsername);
                            if (contactUsernameTwo != null) {
                                intent.putExtra("Contact_Username_Two", contactUsernameTwo);
                            }
                            intent.putExtra("uri", fileUri.toString());
                            startActivity(intent);
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e("File Selector",
                                "The selected file can't be shared: " + requestFile.toString());
                    }


                }
            });
    }

}
