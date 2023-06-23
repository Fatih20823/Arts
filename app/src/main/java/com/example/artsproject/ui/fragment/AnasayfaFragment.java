package com.example.artsproject.ui.fragment;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.artsproject.Adapter.ArtsAdapter;
import com.example.artsproject.Model.Arts;
import com.example.artsproject.databinding.FragmentAnasayfaBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AnasayfaFragment extends Fragment {
    private FragmentAnasayfaBinding fragmentAnasayfaBinding;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Arts> ArtsArrayList;
    ArtsAdapter artsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentAnasayfaBinding = FragmentAnasayfaBinding.inflate(inflater, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        ArtsArrayList = new ArrayList<>();

        fragmentAnasayfaBinding.toolbar.setTitle("Arts");
        ((AppCompatActivity)getActivity()).setSupportActionBar(fragmentAnasayfaBinding.toolbar);

        getData();

        fragmentAnasayfaBinding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        artsAdapter = new ArtsAdapter(ArtsArrayList);
        fragmentAnasayfaBinding.recyclerView.setAdapter(artsAdapter);

        return fragmentAnasayfaBinding.getRoot();
    }

    private void getData(){

        firebaseFirestore.collection("Posts").orderBy("Date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Toast.makeText(requireActivity(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if (value != null) {

                    for (DocumentSnapshot document : value.getDocuments()) {

                        Map<String, Object> data = document.getData();

                        String Name = (String) data.get("Name");
                        String artistName = (String) data.get("ArtistName");
                        String year = (String) data.get("Year");
                        String downLoadUrl = (String) data.get("DownloadUrl");


                        Arts arts = new Arts(Name,artistName,year,downLoadUrl);
                        ArtsArrayList.add(arts);
                    }

                    artsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}