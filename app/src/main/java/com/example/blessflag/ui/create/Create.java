package com.example.blessflag.ui.create;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Create extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    public static final int CAMERA_REQUEST_CODE = 102;
    private CreateViewModel mViewModel;
    EditText etnombre, etsucursal, etubicacion, etcp, ettelefono, etnomgerente;
    private Button btnguardar, btnLimpiar;
    private ImageView imgRestaurante;

    long maxid=0;

    private Uri photouri;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static String currentPath, img="";

    FirebaseDatabase database;
    DatabaseReference dbref;
    private StorageReference mStorageRef;



    public static Create newInstance() {
        return new Create();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create, container, false);
        Componentes(root);
        initFirebase();

        return root;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        database = FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);
        dbref = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void Componentes(View root) {
        EditTextComponent(root);
        ButtonComponent(root);
    }


    private void ButtonComponent(View root) {
        btnguardar = root.findViewById(R.id.btnGuardar);
        btnguardar.setOnClickListener(this);

        btnLimpiar = root.findViewById(R.id.btnLimpiar);
        btnLimpiar.setOnClickListener(this);

        imgRestaurante =root.findViewById(R.id.ivcrearFoto);
        imgRestaurante.setOnClickListener(this);

    }

    private void EditTextComponent(View root) {
        etnombre = root.findViewById(R.id.etCrearNombre);
        etsucursal = root.findViewById(R.id.etCrearSucursal);
        etubicacion = root.findViewById(R.id.etCrearUbicacion);
        etnomgerente = root.findViewById(R.id.etCrearNomGerente);
        etcp = root.findViewById(R.id.etCrearCP);
        ettelefono = root.findViewById(R.id.etCrearTelefono);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btnLimpiar:
                mLimpiar();
                break;

            case R.id.ivcrearFoto:
                pictureTakerAction();
                break;

            case R.id.btnGuardar:
                dbref.child("Restaurante").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            maxid=(snapshot.getChildrenCount());
                            //Toast.makeText(getContext(), "max: "+maxid, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(etnombre.getText().toString().equals("")|| etsucursal.getText().toString().equals("")|| etubicacion.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Porfavor llena los campos", Toast.LENGTH_LONG).show();
                }else{
                    uploadToFirebase(photouri);
                }
                break;
        }

    }

    private void uploadToFirebase(Uri img) {
        String nombre = etnombre.getText().toString();
        String sucursal = etsucursal.getText().toString();
        String ubicacion = etubicacion.getText().toString();
        String nomgerente = etnomgerente.getText().toString();
        String cp = etcp.getText().toString();
        String telefono = ettelefono.getText().toString();
        StorageReference fileRef = mStorageRef.child("Restaurante_"+nombre);
        //Toast.makeText(getContext(), ""+img, Toast.LENGTH_LONG).show();
        fileRef.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Restaurante restaurante = new Restaurante();
                        restaurante.setId((int) (maxid+1));
                        restaurante.setNombre(nombre);
                        restaurante.setSucursal(sucursal);
                        restaurante.setUbicacion(ubicacion);
                        restaurante.setCp(Integer.parseInt(cp));
                        restaurante.setTelefono(Integer.parseInt(telefono));
                        restaurante.setGerente(nomgerente);
                        restaurante.setImagen(uri.toString());
                        dbref.child("Restaurante").child(String.valueOf(maxid+1)).setValue(restaurante);
                        Toast.makeText(getContext(), "Restaurante Agregado", Toast.LENGTH_LONG).show();
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

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uri));
    }



    private void pictureTakerAction(){
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

    private void mLimpiar() {
        etnombre.setText("");
        etsucursal.setText("");
        etubicacion.setText("");
        imgRestaurante.setImageResource(R.drawable.ic_menu_camera);
        etnomgerente.setText("");
        etcp.setText("");
        ettelefono.setText("");
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}