package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.Date;

public class ViewNoteActivity extends AppCompatActivity {
    String id, noteTitle, noteSubTitle, noteContent, createTime;
    TextView txtCreateTime;
    FloatingActionButton fabSave;
    EditText txtNoteTitle, txtNoteSubtitle, txtNoteContent;
    LinearLayout toolBigger, toolSmaller, toolUnderline, toolBold, toolItalic, toolStrike, toolAddPhoto, layoutAddImage;
    ImageView newImgView;
    Uri imageUri;
    MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        setControl();
        setEvent();
    }

    private void setControl() {
        setTitle("Edit Note");
        materialToolbar = findViewById(R.id.toolbarEdit);
        txtNoteTitle = findViewById(R.id.noteTitle);
        txtNoteSubtitle = findViewById(R.id.noteSubtitle);
        txtNoteContent = findViewById(R.id.noteContent);
        txtCreateTime = findViewById(R.id.createTime);
        fabSave = findViewById(R.id.fabSavedNote);
        toolBigger = findViewById(R.id.btnBigger);
        toolSmaller = findViewById(R.id.btnSmaller);
        toolUnderline = findViewById(R.id.btnUnderline);
        toolBold = findViewById(R.id.btnBold);
        toolItalic = findViewById(R.id.btnItalic);
        toolStrike = findViewById(R.id.btnStrikethrough);
        toolAddPhoto = findViewById(R.id.btnAddPhoto);
        layoutAddImage = findViewById(R.id.layoutAddImage);
    }

    private void setEvent() {
        turnBack();
        getNoteData();
        setNoteData();
        saveBtn();
        toolModified();
    }

    private void toolModified() {
        toolBigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolBigger.setBackgroundColor(getColor(R.color.OnSecondary));
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

        toolAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newImgView = new ImageView(ViewNoteActivity.this);
                layoutAddImage.addView(newImgView);
                openGallery();

//                Toast.makeText(CreateNoteActivity.this, "Add photo is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        toolBold.setOnClickListener(new View.OnClickListener() {
            int flag3 = 0;
            @Override
            public void onClick(View view) {
                if (flag3 == 0) {
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_bold.otf");
                    txtNoteContent.setTypeface(tf);
                    flag3 = 1;
                } else if (flag3 == 1) {
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
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
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_mediumitalic.otf");
                    txtNoteContent.setTypeface(tf);
                    flag4 = 1;
                } else if (flag4 == 1) {
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
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
    }

    //select photo
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//mo ta thao tac get content
        //start activity
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

    }

    //nhan ket qua tra ve tu activity tren
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data.getData();
            newImgView.setImageURI(imageUri);
//            imageAddPhoto.clearColorFilter();
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
    }

    private void getNoteData() {
        id = getIntent().getStringExtra(Constants.id);
        noteTitle = getIntent().getStringExtra(Constants.noteTitle);
        noteSubTitle = getIntent().getStringExtra(Constants.noteSubtitle);
        noteContent = getIntent().getStringExtra(Constants.noteContent);
        createTime = getIntent().getStringExtra(Constants.createTime);
    }

    private void saveBtn() {
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedNote(
                        id,
                        txtNoteTitle.getText().toString(),
                        txtNoteSubtitle.getText().toString(),
                        txtNoteContent.getText().toString());
            }
        });
    }

    private void savedNote(String id, String title, String subtitle, String content) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(StaticUtilities.getUsername(ViewNoteActivity.this)).child("noteModels");
        // set data for new note model
        NoteModel noteModel = new NoteModel(id, title, subtitle, content, new Date().toString());
        databaseReference.child(id).setValue(noteModel);
        startActivity(new Intent(ViewNoteActivity.this, MainActivity.class));
    }
}