package com.example.blessflag.ui.editar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blessflag.R;
import com.example.blessflag.model.Restaurante;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditarViewModel mViewModel;
    private Button btnbuscar, btnactualizar, btnlimpiar;
    EditText etid,etnombre, etsucursal, etubicacion, etcp, ettelefono, etnomgerente;
    long maxid=0;

    private ImageView imgRestaurante;

    private Uri photouri;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static String currentPath, img="";

    public static int bnd=0, inp;

    FirebaseDatabase database;
    DatabaseReference dbref;
    private StorageReference mStorageRef;

    public static EditarFragment newInstance() {
        return new EditarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar, container, false);
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
    }

    private void ButtonComponent(View root) {
        btnbuscar = root.findViewById(R.id.btnEditarID);
        btnbuscar.setOnClickListener( this);

        btnlimpiar=root.findViewById(R.id.btnEditarLimpiar);
        btnlimpiar.setOnClickListener( this);

        btnactualizar = root.findViewById(R.id.btnEditarActualizar);
        btnactualizar.setEnabled(false);
        btnactualizar.setOnClickListener( this);

        imgRestaurante = root.findViewById(R.id.iveditarFoto);
        imgRestaurante.setOnClickListener(this);
    }

    private void EditTextComponent(View root) {
        etid = root.findViewById(R.id.etEditID);
        etnombre = root.findViewById(R.id.etEditarNombre);
        etsucursal = root.findViewById(R.id.etEditarSucursal);
        etubicacion = root.findViewById(R.id.etEditarUbicacion);
        etcp = root.findViewById(R.id.etEditarCP);
        ettelefono =root.findViewById(R.id.etEditarTelefono);
        etnomgerente =root.findViewById(R.id.etEditarNomGerente);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btnEditarLimpiar:
                mLimpiar();
                break;

            case R.id.btnEditarID:
                dbref.child("Restaurante").child(String.valueOf(etid.getText())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()==null){
                            mLimpiar();
                            Toast.makeText(getContext(), "No se encontraron registros, intenta nuevamente", Toast.LENGTH_LONG).show();
                        }else{
                            String link = snapshot.child("imagen").getValue().toString();
                            Picasso.get().load(link).into(imgRestaurante);
                            etnombre.setText(snapshot.child("nombre").getValue().toString());
                            etcp.setText(snapshot.child("cp").getValue().toString());
                            ettelefono.setText(snapshot.child("telefono").getValue().toString());
                            etnomgerente.setText(snapshot.child("gerente").getValue().toString());
                            etsucursal.setText(snapshot.child("sucursal").getValue().toString());
                            etubicacion.setText(snapshot.child("ubicacion").getValue().toString());
                            bnd=1;
                            btnactualizar.setEnabled(true);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;

            case R.id.iveditarFoto:
                PictureTakerAction();
                break;

            case R.id.btnEditarActualizar:
                if(etnombre.getText().toString().equals("")|| etsucursal.getText().toString().equals("")|| etubicacion.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Porfavor llena los campos", Toast.LENGTH_LONG).show();
                }else{
                    uploadToFirebase(photouri);
                }
                break;


        }
    }

    private void uploadToFirebase(Uri photouri) {
        String nombre = etnombre.getText().toString();
        String sucursal = etsucursal.getText().toString();
        String ubicacion = etubicacion.getText().toString();
        String cp = etcp.getText().toString();
        String telefono = ettelefono.getText().toString();
        String nomgerente = etnomgerente.getText().toString();
        StorageReference fileRef = mStorageRef.child("Restaurante_"+nombre);
        //Toast.makeText(getContext(), ""+img, Toast.LENGTH_LONG).show();
        fileRef.putFile(photouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Restaurante restaurante = new Restaurante();
                        restaurante.setId(Integer.parseInt(etid.getText().toString()));
                        restaurante.setNombre(nombre);
                        restaurante.setCp(Integer.parseInt(cp));
                        restaurante.setTelefono(Integer.parseInt(telefono));
                        restaurante.setUbicacion(ubicacion);
                        restaurante.setGerente(nomgerente);
                        restaurante.setSucursal(sucursal);
                        restaurante.setImagen(uri.toString());
                        dbref.child("Restaurante").child(etid.getText().toString()).setValue(restaurante);
                        Toast.makeText(getContext(), "Restaurante Actualizado Correctamente", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void PictureTakerAction() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},101);
        }else{
            Intent tomarfoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (tomarfoto.resolveActivity(getActivity().getPackageManager())!=null){
                File photofile;
                try {
                    photofile = crearImageFile();
                    if (photofile!=null)
                    {
                        currentPath = photofile.getAbsolutePath();
                        photouri= FileProvider.getUriForFile(getActivity(),"com.example.blessflag",photofile);
                        tomarfoto.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                        startActivityForResult(tomarfoto,1);
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(),"Error de fotografia", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private File crearImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "FP_"+timeStamp+"_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(timeStamp, ".jpg",storageDir);

        return image;
    }

    @Override
    public void onActivityResult(int requestcode, int resulcode, Intent data){
        super.onActivityResult(requestcode, resulcode, data);
        if(requestcode==REQUEST_TAKE_PHOTO&&resulcode== Activity.RESULT_OK){
            Bitmap imagebitmap = BitmapFactory.decodeFile(currentPath);
            imgRestaurante.setImageBitmap(imagebitmap);
            try {
                imgRestaurante.setImageURI(photouri);
                img=currentPath;
                Toast.makeText(getContext(),"img"+img, Toast.LENGTH_LONG).show();
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getContext(),"Fallo en Activity"+img, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static int obtenerPos(Spinner spinner, String item){
        int pos=0;
        for (int i=0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)){
                pos = i;
            }
        }
        return pos;
    }

    private void mLimpiar() {
        etid.setText("");
        btnactualizar.setEnabled(false);
        etnombre.setText("");
        etsucursal.setText("");
        etubicacion.setText("");
        imgRestaurante.setImageResource(R.drawable.ic_menu_camera);
        etcp.setText("");
        ettelefono.setText("");
        etnomgerente.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}