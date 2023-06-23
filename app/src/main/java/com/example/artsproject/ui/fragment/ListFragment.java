package com.example.artsproject.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.artsproject.R;
import com.example.artsproject.databinding.FragmentListBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ListFragment extends Fragment {

    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    private FragmentListBinding fragmentListBinding;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        registerLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);

        fragmentListBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)) {
                            Snackbar.make(v,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                                }
                            }).show();
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }        } else {
                        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activityResultLauncher.launch(intentToGallery);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Snackbar.make(v,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                                }
                            }).show();
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }        } else {
                        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activityResultLauncher.launch(intentToGallery);
                    }
                }
            }
        });

        fragmentListBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });

        return fragmentListBinding.getRoot();
    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                   imageData = intentFromResult.getData();
                                try {

                                    if (Build.VERSION.SDK_INT >= 28) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);
                                        fragmentListBinding.imageView.setImageBitmap(selectedImage);

                                    } else {
                                        selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),imageData);
                                        fragmentListBinding.imageView.setImageBitmap(selectedImage);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });

        permissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            //permission granted
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            //startActivityForResult(intentToGallery,2);
                            activityResultLauncher.launch(intentToGallery);

                        } else {
                            //permission denied
                            Toast.makeText(requireActivity(),"Permisson needed!",Toast.LENGTH_LONG).show();
                        }
                    }

                });


    }
    public void save(View view) {

        //FireBase
        if (imageData != null) {

            //Universal unique id
            UUID uuid = UUID.randomUUID();
            String imageName = "images/" + uuid + ".png";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    StorageReference newReferance = firebaseStorage.getReference(imageName);
                    newReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String name = fragmentListBinding.editTextName.getText().toString();
                            String artistName = fragmentListBinding.editTextPainterName.getText().toString();
                            String year = fragmentListBinding.editTextYear.getText().toString();
                            String downloadUrl = uri.toString();

                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("Date", FieldValue.serverTimestamp());
                            postData.put("Name",name);
                            postData.put("ArtistName",artistName);
                            postData.put("Year",year);
                            postData.put("DownloadUrl",downloadUrl);


                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Navigation.findNavController(view).navigate(R.id.action_listFragment_to_anasayfaFragment);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }


    }

}