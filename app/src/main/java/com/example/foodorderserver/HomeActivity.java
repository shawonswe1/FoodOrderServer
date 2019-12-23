package com.example.foodorderserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodorderserver.Adapter.CategoryAdepter;
import com.example.foodorderserver.model.AddCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,CategoryAdepter.OnItemClickListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    private TextView AdminName,AdminEmail;

    private static final int ImageRequest = 1;
    private Uri imageUri;

    private ImageView imageView;
    private EditText imageName;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference,vDatabaseReference;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;

    private ValueEventListener valueEventListener;

    private RecyclerView recyclerView;
    private CategoryAdepter categoryAdapter;
    private List<AddCategory> addCategories;
    private ProgressBar progressBar;

    //send Data to another Activity
    public static final String EXTRA_TEXT = "com.android.example.EXTRA_TEXT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Firebase Instance ..........

        mStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Food Order").child("Category");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Food Order").child("Category");

        //------------------------------------------------------------------//

        //Navigation Bar .................................................//

        drawerLayout = findViewById(R.id.drawerID);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.navigationID);
        navigationView.setNavigationItemSelectedListener(this);

        //-----------------------------------------------------//

        View header = navigationView.getHeaderView(0);
        AdminName = header.findViewById(R.id.adminName);
        AdminEmail = header.findViewById(R.id.adminEmail);

        Profile();

        //RecyclerView & ProgressBar ...............................

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.recyclerViewProgressBer);
        //--------------------------------------------------------//
        //Adapter & List...........................................//
        addCategories = new ArrayList<>();

        categoryAdapter = new CategoryAdepter(HomeActivity.this,addCategories);

        categoryAdapter.setOnItemClickListener(HomeActivity.this);

        //---------------------------------------------------------//
        //Receive Data From DataBase....................................//

        vDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food Order").child("Category");
        valueEventListener = vDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                addCategories.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    AddCategory addCategory = dataSnapshot1.getValue(AddCategory.class);
                    addCategory.setKey(dataSnapshot1.getKey());
                    addCategories.add(addCategory);
                }

                //List Reverss Function..................
                Collections.reverse(addCategories);
                //-----------------------------------//

                categoryAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(categoryAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(HomeActivity.this , "Error "+databaseError.getMessage() , Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        //--------------------------------------------------------------------//


    }

    //View Profile Method ....................
    private void Profile()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            String Name = user.getDisplayName();
            String Email = user.getEmail();

            AdminName.setText(Name);
            AdminEmail.setText(Email);
        }
    }

    //.........................................................

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Navigation Item Selected Method................
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id)
        {
            case R.id.logout:
                signoutMethod();
                break;
            case R.id.home:
                break;
            case R.id.catagory:
                addCategoryDialog();
                break;
        }
        return false;
    }

    // Add Category Dialog...............
    private void addCategoryDialog()
    {
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        View vLayout = getLayoutInflater().inflate(R.layout.add_category,null);
        ImageButton chooseImage = vLayout.findViewById(R.id.imageButtonID);
        imageView = vLayout.findViewById(R.id.imageID);
        imageName = vLayout.findViewById(R.id.categoryNameID);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFileChooser();
            }
        });
        myDialog.setView(vLayout);
        myDialog.setTitle("Choose Category Image");
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("Save" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface , int i) {

                SaveData();
            }
        });
        myDialog.setNegativeButton("Cancel" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface , int i) {

            }
        });
        myDialog.show();

    }

    // Open File For choose image......

    private void OpenFileChooser()
    {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageIntent,ImageRequest);
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode , @Nullable Intent data) {
        super.onActivityResult(requestCode , resultCode , data);

        if (requestCode == ImageRequest && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(imageView);


        }
    }
    //....................................................................//

    // Save Data With Image............................
    public String getFileExtention(Uri imageUri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void SaveData()
    {
        final String CategoryName = imageName.getText().toString().trim();
        if (CategoryName.isEmpty())
        {
            imageName.setError("Enter Category Name");
            imageName.requestFocus();
            return;
        }

        StorageReference ref = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));

        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                Toast.makeText(HomeActivity.this , "Image Is save Successfully" , Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful());

                Uri downloadUri = uriTask.getResult();

                AddCategory addCategory = new AddCategory(CategoryName,downloadUri.toString());
                String uploadId = databaseReference.push().getKey();
                databaseReference.child(uploadId).setValue(addCategory);


            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(HomeActivity.this , "Image Is not save Successfully" , Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //---------------------------------------------------/

    // Sign Out Method--------------------------
    private void signoutMethod()
    {
        firebaseAuth.signOut();
        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    // One Click Method........................
    @Override
    public void onItemClick(int position) {

        AddCategory selectedItem = addCategories.get(position);
        String selectedKey = selectedItem.getKey();

        Intent intent = new Intent(HomeActivity.this,FoodActivity.class);
        intent.putExtra(EXTRA_TEXT,selectedKey);
        startActivity(intent);

        Toast.makeText(this , "On Click Listener" + selectedKey, Toast.LENGTH_SHORT).show();

    }

    //-------------------------------------------------//

   //Update Method....................................
    @Override
    public void update(int position) {

        Toast.makeText(this , "Click Do Whate Ever" , Toast.LENGTH_SHORT).show();
        AddCategory selectedItem = addCategories.get(position);
        final String selectedKey = selectedItem.getKey();

        //Delete Old Image from FireStore..........................

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mStorageRef.child(selectedKey).delete();
                Toast.makeText(HomeActivity.this , "Old Image Deleted" , Toast.LENGTH_SHORT).show();
            }
        });
        //.....................................................

        updateDialog(selectedKey);
        imageName.setText(selectedItem.getImageName());
    }
  //Data Update Method........................
    private void updateDialog(final String selectedKey) {
        final AlertDialog.Builder updateDialog = new AlertDialog.Builder(HomeActivity.this);
        View vLayout = getLayoutInflater().inflate(R.layout.update_layout,null);
        ImageButton chooseImage = vLayout.findViewById(R.id.imageButtonID);
        imageView = vLayout.findViewById(R.id.imageID);
        imageName = vLayout.findViewById(R.id.categoryNameID);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFileChooser();
            }
        });

        updateDialog.setView(vLayout);
        updateDialog.setTitle("Update Category");

        updateDialog.setPositiveButton("Update" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface , int i) {
                Toast.makeText(HomeActivity.this , "Okk" , Toast.LENGTH_SHORT).show();

                updateData(selectedKey);

            }
        });


        updateDialog.show();
    }

    private void updateData(final String Key)
    {
        final String CategoryName = imageName.getText().toString().trim();
        if (CategoryName.isEmpty())
        {
            imageName.setError("Enter Category Name");
            imageName.requestFocus();
            return;
        }

        StorageReference ref = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));

        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                Toast.makeText(HomeActivity.this , "Image Is save Successfully" , Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful());

                Uri downloadUri = uriTask.getResult();

                AddCategory addCategory = new AddCategory(CategoryName,downloadUri.toString());
                databaseReference.child(Key).setValue(addCategory);


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(HomeActivity.this , "Image Is not save Successfully" , Toast.LENGTH_SHORT).show();
                    }
                });
    }
  //...........................................................//

  //Delete Method................................................//
    @Override
    public void delete(int position) {

        AddCategory selectedItem = addCategories.get(position);
        final String selectedKey = selectedItem.getKey();


        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                vDatabaseReference.child(selectedKey).removeValue();
                DatabaseReference foodData = FirebaseDatabase.getInstance().getReference().child("Food Order").child("Food").child(selectedKey);
                StorageReference foodStorage = FirebaseStorage.getInstance().getReference().child("Food Order").child("Food").child(selectedKey);
                foodData.removeValue();
                foodStorage.delete();

                Toast.makeText(HomeActivity.this , "Delete Seccessfully" , Toast.LENGTH_SHORT).show();
            }
        });
    }
   //----------------------------------------------------------------------------------------//
}
