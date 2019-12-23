package com.example.foodorderserver;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.foodorderserver.Adapter.FoodAdapter;
import com.example.foodorderserver.model.AddFood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodActivity extends AppCompatActivity {

    private static final int ImageRequest = 1;
    private Uri imageUri;

    ImageButton addImage;
    ImageView foodImage;
    EditText name,price,discount;

    // Firebase Database........................//
    private FirebaseAuth firebaseAuth;
    private DatabaseReference foodDatabase,foodList;
    private StorageReference fStorageRef;
    private FirebaseStorage fStorage;
    //-----------------------------------//

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private List<AddFood> addFoods;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        // Receive Data.................//
        Intent intent = getIntent();
        final String selectedKey = intent.getStringExtra(HomeActivity.EXTRA_TEXT);
        //-----------------------------------//

        //.........................................//
        firebaseAuth = FirebaseAuth.getInstance();
        foodDatabase = FirebaseDatabase.getInstance().getReference().child("Food Order").child("Food").child(selectedKey);
        fStorageRef = FirebaseStorage.getInstance().getReference().child("Food Order").child("Food");
        //---------------------------------------------------------------------//

        //RecyclerView & progressBar...............................//
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        progressBar = findViewById(R.id.progressBar);
        //--------------------------------------------------------------//
        // Adapter & List............................//
        addFoods = new ArrayList<>();
        foodAdapter = new FoodAdapter(this,addFoods);

        //------------------------------------------------//
        //Food List From Database......................//
        foodList = foodDatabase.child("new Food");
        foodList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                addFoods.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    AddFood addFood = dataSnapshot1.getValue(AddFood.class);
                    addFood.setKey(dataSnapshot1.getKey());
                    addFoods.add(addFood);
                }

                //List Reverss Function..................
                Collections.reverse(addFoods);
                //-----------------------------------//

                foodAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(foodAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(FoodActivity.this , "Error "+databaseError.getMessage() , Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        //---------------------------------------------------//
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view ,selectedKey, Snackbar.LENGTH_LONG)
                        .setAction("Action" , null).show();
                // Add Food //
                AddFoodDialog(selectedKey);


            }
        });
    }

    // AddFoodDialog..............................//
    private void AddFoodDialog(final String selectedKey)
    {
        AlertDialog.Builder foodDialog = new AlertDialog.Builder(this);
        View fLayout = getLayoutInflater().inflate(R.layout.add_food,null);
        
        addImage = fLayout.findViewById(R.id.addFood);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFileChooser();
            }
        });
        foodImage = fLayout.findViewById(R.id.foodImage);
        name = fLayout.findViewById(R.id.nameText);
        price = fLayout.findViewById(R.id.priceText);
        discount = fLayout.findViewById(R.id.discountText);
        
        foodDialog.setView(fLayout);
        foodDialog.setTitle("Add Food");
        foodDialog.setPositiveButton("Save" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface , int i) {
                
                saveFood(selectedKey);
            }
        });
        foodDialog.show();
    }

    //---------------------------------------------------//

    // Save Data With Image............................//
    public String getFileExtention(Uri imageUri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void saveFood(String selectedKey)
    {

        final String foodName = name.getText().toString().trim();
        final String foodPrice = price.getText().toString().trim();
        final String foodDiscount = discount.getText().toString().trim();

        StorageReference ref = fStorageRef.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Get a URL to the uploaded content
                Toast.makeText(FoodActivity.this , "Image Is save Successfully" , Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                AddFood addFood = new AddFood(foodName,foodPrice,foodDiscount,downloadUri.toString());
                String foodID = foodDatabase.push().getKey();
                foodDatabase.child("new Food").child(foodID).setValue(addFood);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // Handle unsuccessful uploads
                Toast.makeText(FoodActivity.this , "Image Is not save Successfully" , Toast.LENGTH_SHORT).show();

            }
        });


    }
    //-------------------------------------------------------//

    // Open File For choose image.............................//

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
            Picasso.with(this).load(imageUri).into(foodImage);
            Glide.with(this).load(imageUri).into(foodImage);


        }
    }
    //....................................................................//

}
