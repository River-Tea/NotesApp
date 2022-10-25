package com.example.notesapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ViewNoteActivity extends AppCompatActivity {
    MaterialToolbar materialToolbar;
    String id, noteTitle, noteSubTitle, noteContent, createTime, noteImages;
    TextView txtCreateTime;
    FloatingActionButton fabSave;
    EditText txtNoteTitle, txtNoteSubtitle, txtNoteContent;
    LinearLayout toolBigger, toolSmaller, toolUnderline, toolBold, toolItalic, toolStrike, toolAddPhoto, layoutAddImage, btnTakePhoto;
    ImageView newImgView, noteImage, displayImage;
    Uri imageUri;
    private static final int PERMISSION_CODE = 123;
    private static final int CAPTURE_CODE = 1001;
    AlertDialog alertDelete;

    // Upload img
    DatabaseReference noteDatabase;
    StorageReference imageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        setDatabase();
        setControl();
        setEvent();
    }

    private void setDatabase() {
        imageStorage = FirebaseStorage.getInstance().getReference("images");
        noteDatabase = FirebaseDatabase.getInstance().getReference("users").child(StaticUtilities.getUsername(ViewNoteActivity.this)).child("noteModels");
    }

    private void setControl() {
        setTitle("Edit Note");
        materialToolbar = findViewById(R.id.toolbarEdit);
        txtNoteTitle = findViewById(R.id.noteTitle);
        txtNoteSubtitle = findViewById(R.id.noteSubtitle);
        txtNoteContent = findViewById(R.id.noteContent);
        txtCreateTime = findViewById(R.id.createTime);
        noteImage = findViewById(R.id.imgCard);
        fabSave = findViewById(R.id.fabSavedNote);
        toolBigger = findViewById(R.id.btnBigger);
        toolSmaller = findViewById(R.id.btnSmaller);
        toolUnderline = findViewById(R.id.btnUnderline);
        toolBold = findViewById(R.id.btnBold);
        toolItalic = findViewById(R.id.btnItalic);
        toolStrike = findViewById(R.id.btnStrikethrough);
        toolAddPhoto = findViewById(R.id.btnAddPhoto);
        layoutAddImage = findViewById(R.id.layoutAddImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        displayImage = findViewById(R.id.displayImage);
    }

    private void setEvent() {
        turnBack();
        getNoteData();
        setNoteData();
        saveBtn();
        toolModified();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_view_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnDelete) {
            showAlertDelete();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDelete() {
        if (alertDelete == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewNoteActivity.this);

            View view = LayoutInflater.from(this).inflate(R.layout.delete_alert_dialog,
                    (ViewGroup) findViewById(R.id.deleteDialog));

            builder.setView(view);
            alertDelete = builder.create();
            if (alertDelete.getWindow() != null) {
                alertDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.txtDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ViewNoteActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewNoteActivity.this, MainActivity.class);
                    noteDatabase.child(id).removeValue();
                    startActivity(intent);
                }
            });

            view.findViewById(R.id.txtCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDelete.dismiss();
                }
            });
        }
        alertDelete.show();
    }

    private void toolModified() {
        toolBigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float textSize = txtNoteContent.getTextSize();
                txtNoteContent.setTextSize(0, txtNoteContent.getTextSize() + 2.0f);
//                Toast.makeText(CreateNoteActivity.this, "Size is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        toolSmaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float textSize = txtNoteContent.getTextSize();
                txtNoteContent.setTextSize(0, txtNoteContent.getTextSize() - 2.0f);
//                Toast.makeText(CreateNoteActivity.this, "Size is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        toolBold.setOnClickListener(new View.OnClickListener() {
            int flag3 = 0;

            @Override
            public void onClick(View view) {
                if (flag3 == 0) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_bold.otf");
                    txtNoteContent.setTypeface(tf);
                    flag3 = 1;
                } else if (flag3 == 1) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
                    txtNoteContent.setTypeface(tf);
                    flag3 = 0;
                }

            }
        });

        toolItalic.setOnClickListener(new View.OnClickListener() {
            int flag4 = 0;

            @Override
            public void onClick(View view) {
                if (flag4 == 0) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_mediumitalic.otf");
                    txtNoteContent.setTypeface(tf);
                    flag4 = 1;
                } else if (flag4 == 1) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
                    txtNoteContent.setTypeface(tf);
                    flag4 = 0;
                }
            }
        });

        toolUnderline.setOnClickListener(new View.OnClickListener() {
            int flag5 = 0;

            @Override
            public void onClick(View view) {
                if (flag5 == 0) {
                    txtNoteContent.setPaintFlags(txtNoteContent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    flag5 = 1;
                } else if (flag5 == 1) {
                    txtNoteContent.setPaintFlags(View.INVISIBLE);
                    flag5 = 0;
                }
            }
        });

        toolStrike.setOnClickListener(new View.OnClickListener() {
            int flag6 = 0;

            @Override
            public void onClick(View view) {
                if (flag6 == 0) {
                    txtNoteContent.setPaintFlags(txtNoteContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    flag6 = 1;
                } else if (flag6 == 1) {
                    txtNoteContent.setPaintFlags(View.INVISIBLE);
                    flag6 = 0;
                }
            }
        });

        toolAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newImgView = new ImageView(ViewNoteActivity.this);
                layoutAddImage.removeView(displayImage);
                layoutAddImage.addView(newImgView);
                openGallery();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newImgView = new ImageView(ViewNoteActivity.this);
                layoutAddImage.removeView(displayImage);
                layoutAddImage.addView(newImgView);
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                } else {
                    openCamera();
                }
            }
        });

    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "new image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(camIntent, CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    // select photo
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//mo ta thao tac get content
        //start activity
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

    }

    // nhan ket qua tra ve tu activity tren
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data.getData();
            newImgView.setImageURI(imageUri);
//            imageAddPhoto.clearColorFilter();
        }
        if (resultCode == RESULT_OK && requestCode == CAPTURE_CODE) {
            newImgView.setImageURI(imageUri);
        }
    }

    private void turnBack() {
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setNoteData() {
        txtNoteTitle.setText(noteTitle);
        txtNoteSubtitle.setText(noteSubTitle);
        txtNoteContent.setText(noteContent);
        txtCreateTime.setText(createTime);
        if (!Objects.equals(noteImages, "")) {
            Picasso.get()
                    .load(noteImages)
                    .into(displayImage);
        }
    }

    private void getNoteData() {
        id = getIntent().getStringExtra(Constants.id);
        noteTitle = getIntent().getStringExtra(Constants.noteTitle);
        noteSubTitle = getIntent().getStringExtra(Constants.noteSubtitle);
        noteContent = getIntent().getStringExtra(Constants.noteContent);
        createTime = getIntent().getStringExtra(Constants.createTime);
        noteImages = getIntent().getStringExtra(Constants.imageURL);
    }

    private void saveBtn() {
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNoteDetail();
            }
        });
    }

    private void saveNoteDetail() {
        // photo from gallery
        if (imageUri != null) {
            StorageReference storageReference1 = imageStorage.child(System.currentTimeMillis() + "." + GetFileExtension(imageUri));
            storageReference1.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    @SuppressLint("SimpleDateFormat") NoteModel noteModel = new NoteModel(id,
                                            txtNoteTitle.getText().toString(),
                                            txtNoteSubtitle.getText().toString(),
                                            txtNoteContent.getText().toString(),
                                            new SimpleDateFormat("MMM dd yyyy").format(new Date()),
                                            uri.toString());

                                    noteDatabase.child(id).setValue(noteModel);
                                    startActivity(new Intent(ViewNoteActivity.this, MainActivity.class));
                                }
                            });
                        }
                    });
        } else if(!Objects.equals(noteImages, "")) {
            @SuppressLint("SimpleDateFormat") NoteModel noteModel = new NoteModel(id,
                    txtNoteTitle.getText().toString(),
                    txtNoteSubtitle.getText().toString(),
                    txtNoteContent.getText().toString(),
                    new SimpleDateFormat("MMM dd yyyy HH:mm").format(new Date()),
                    noteImages.toString());
            noteDatabase.child(id).setValue(noteModel);
            startActivity(new Intent(ViewNoteActivity.this, MainActivity.class));
        }
        else {
            // set data for new note model without image
            @SuppressLint("SimpleDateFormat") NoteModel noteModel = new NoteModel(id,
                    txtNoteTitle.getText().toString(),
                    txtNoteSubtitle.getText().toString(),
                    txtNoteContent.getText().toString(),
                    new SimpleDateFormat("MMM dd yyyy HH:mm").format(new Date()),
                    "");
            noteDatabase.child(id).setValue(noteModel);
            startActivity(new Intent(ViewNoteActivity.this, MainActivity.class));
        }
    }

    private String GetFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}