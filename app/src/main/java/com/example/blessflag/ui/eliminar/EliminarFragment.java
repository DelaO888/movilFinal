package com.example.blessflag.ui.eliminar;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blessflag.R;
import com.example.blessflag.model.Restaurante;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EliminarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener  {

    private EliminarViewModel mViewModel;

    private Button btnbuscar, btneliminarlogica, btneliminarcompleto, btnlimpiar;
    TextView txid,txnombre, txsucursal, txubicacion, txcp, txtelefono, txtgerente;
    ImageView imgRestaurante;

    FirebaseDatabase database;
    DatabaseReference dbref;
    private StorageReference mStorageRef;

    public static EliminarFragment newInstance() {
        return new EliminarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eliminar, container, false);
        Componentes(root);
        initFirebase();
        return root;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        database = FirebaseDatabase.getInstance();
        dbref = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void Componentes(View root) {
        EditTextComponent(root);
        ButtonComponent(root);
        imgRestaurante = root.findViewById(R.id.iveliminarFoto);
    }

    private void ButtonComponent(View root) {
        btnbuscar = root.findViewById(R.id.btnEliminarID);
        btnbuscar.setOnClickListener( this);

        btneliminarcompleto = root.findViewById(R.id.btnEliminarEC);
        btneliminarcompleto.setEnabled(false);
        btneliminarcompleto.setOnClickListener( this);


        btnlimpiar=root.findViewById(R.id.btnLimpiarE);
        btnlimpiar.setOnClickListener(this);





    }

    private void EditTextComponent(View root) {
        txid = root.findViewById(R.id.etEliminarID);
        txnombre = root.findViewById(R.id.elNombre);
        txcp = root.findViewById(R.id.elCP);
        txsucursal = root.findViewById(R.id.elSucursal);
        txubicacion = root.findViewById(R.id.elUbicacion);
        txtgerente = root.findViewById(R.id.elNomGerente);
        txtelefono = root.findViewById(R.id.elTelefono);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EliminarViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEliminarID:
                dbref.child("Restaurante").child(String.valueOf(txid.getText())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()==null) {
                            mLimpiar();
                            Toast.makeText(getContext(), "No se encontraron registros, intenta nuevamente", Toast.LENGTH_LONG).show();
                        }else{
                            String link = snapshot.child("imagen").getValue().toString();
                            Picasso.get().load(link).into(imgRestaurante);
                            txnombre.setText(snapshot.child("nombre").getValue().toString());
                            txtelefono.setText(snapshot.child("telefono").getValue().toString());
                            txsucursal.setText(snapshot.child("sucursal").getValue().toString());
                            txubicacion.setText(snapshot.child("ubicacion").getValue().toString());
                            txcp.setText(snapshot.child("cp").getValue().toString());
                            txtgerente.setText(snapshot.child("gerente").getValue().toString());
                            btneliminarcompleto.setEnabled(true);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;

            case R.id.btnEliminarEC:
                View dialogView =LayoutInflater.from(getContext()).inflate(R.layout.dialogo_jugador, null);
                ((TextView)dialogView.findViewById(R.id.info)).setText("¿Desea Eliminar COMPLETAMENTE el siguiente registro?\n"+
                        "ID: "+ txid.getText().toString() +" \n" + "Sucursal: "+ txsucursal.getText().toString() +" \n" + "Ubicacion: "+ txubicacion.getText().toString() +" \n" +"Nombre: "+ txnombre.getText().toString() +" \n" +
                        "Telefono: "+ txtelefono.getText().toString() +" \n" + "Gerente: "+ txtgerente.getText().toString() +" \n" + "Codigo Postal: "+ txcp.getText().toString() +" \n" );
                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                dialogo.setTitle("Importante");
                dialogo.setView(dialogView);
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbref.child("Restaurante").child(String.valueOf(txid.getText())).removeValue();
                        Toast.makeText(getContext(), "Restaurante Eliminado Completamente!", Toast.LENGTH_LONG).show();
                        mLimpiar();
                    }
                });
                dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(),"Registro activo aun!", Toast.LENGTH_LONG).show();
                    }
                });
                dialogo.show();

                break;


            case R.id.btnLimpiarE:
                mLimpiar();
                break;
        }
    }

    private void mLimpiar() {
        txid.setText("");
        btneliminarcompleto.setEnabled(false);
        txnombre.setText("Nombre: ");
        txcp.setText("Codigo Postal: ");
        txtgerente.setText("Gerente: ");
        imgRestaurante.setImageResource(R.drawable.ic_menu_camera);
        txtelefono.setText("Telefono: ");
        txsucursal.setText("Sucursal: ");
        txubicacion.setText("Ubicacion ");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}