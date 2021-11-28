package com.example.blessflag.ui.listar;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.blessflag.R;
import com.example.blessflag.databinding.FragmentListarBinding;
import com.example.blessflag.model.Restaurante;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListarFragment extends Fragment implements View.OnClickListener {

    private ListarViewModel mViewModel;
    private FragmentListarBinding binding;
    private List<Restaurante> restauranteList = new ArrayList<>();

    ArrayAdapter<Restaurante> restaurantesArrayAdapter;

    Restaurante restauranteSelected;

    FirebaseDatabase database;
    DatabaseReference dbref;
    StorageReference storageRef;

    ListView lista;

    public static ListarFragment newInstance() {
        return new ListarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar, container, false);
        Componentes(root);
        restauranteList.clear();
        initFirebase();
        listar();
        return root;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        database = FirebaseDatabase.getInstance();
        //storageRef = FirebaseStorage.getInstance().getReference("Restaurante");
        dbref = database.getReference();
    }

    private void Componentes(View root) {
        lista = root.findViewById(R.id.lvListaRestaurantes);
        ButtonComponent(root);

    }

    private void listar() {
        dbref.child("Restaurante").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restauranteList.clear();
                for (DataSnapshot objsnapshot : snapshot.getChildren()){
                    Restaurante res = objsnapshot.getValue(Restaurante.class);
                    restauranteList.add(res);

                    restaurantesArrayAdapter = new ArrayAdapter<Restaurante>(getContext(), android.R.layout.simple_list_item_1, restauranteList);
                    lista.setAdapter(restaurantesArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ButtonComponent(View root) {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListarViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }

    }

}