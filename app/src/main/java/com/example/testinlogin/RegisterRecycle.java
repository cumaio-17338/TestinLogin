package com.example.testinlogin;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RegisterRecycle extends AppCompatActivity implements NumberPicker.OnValueChangeListener, View.OnClickListener {
    private String capacidade;
    private String tipo;
    private String descricao;
    private NumberPicker quantity_picker;
    private int quantity;
    private Button register_recycle_button;
    private TextView type_textView, capacity_textView, description_textView;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseFirestore mStore;
    private List<QueryDocumentSnapshot>  plasticReferenceSavingList;
    private String collectionName;
    private ImageView imageView;
    private Uri contentUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_recycle);



        //References Firebase
        mAuth = FirebaseAuth.getInstance();
        mStore  = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        imageView = findViewById(R.id.photo_imageView_register_recycle);

        Bundle bundle = getIntent().getExtras();
        contentUri = Uri.parse(bundle.getString("contentUri"));
        Picasso.get().load(contentUri).into(imageView); //Put image on imageView

        capacidade = bundle.getString("capacidade");
        tipo = bundle.getString("tipo");
        descricao = bundle.getString("descricao");
        collectionName = bundle.getString("collectionName");

        //Quantity picker
        quantity = 1;
        quantity_picker= findViewById(R.id.quantity_picker);
        quantity_picker.setValue(1);
        quantity_picker.setMinValue(1);
        quantity_picker.setMaxValue(100);
        quantity_picker.setOnValueChangedListener(this);

        register_recycle_button = findViewById(R.id.register_recycle_button);
        register_recycle_button.setOnClickListener(this);


        //Set data to textView
        type_textView = findViewById(R.id.type_textView);
        type_textView.setText(tipo);

        capacity_textView = findViewById(R.id.capacity_textView);
        capacity_textView.setText(capacidade);

        description_textView = findViewById(R.id.description_textView);
        description_textView.setText(descricao);


        getReferenceSavingData();

       //TOOLBAR LAYOUT
        /*
        Toolbar toolbar = findViewById(R.id.registar_reciclagem_ToolBar);
        toolbar.setTitle("Registar Reciclagem");
        setSupportActionBar(toolbar);  */

        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Estatísticas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        */

    }

    private void getReferenceSavingData() {

        plasticReferenceSavingList = new ArrayList<>();

        mStore.collection(collectionName)
                //.whereEqualTo("capacidade", Double.parseDouble(capacidade))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    plasticReferenceSavingList.add(document);
                            }
                        } else {
                            //Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker.getId() == R.id.quantity_picker) {
            quantity = newVal;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.register_recycle_button){

            if(plasticReferenceSavingList.size() > 0){
                uplodaRecyclingDataToFireBase();
                //Toast.makeText(this, "Existem dados!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Por favor registe novamente!!", Toast.LENGTH_SHORT).show();
            }

          /*  if(plasticReferenceSavingList.size() <= 0){
                Toast.makeText(this, "Lista vazia!", Toast.LENGTH_SHORT).show();
            }
            else {
                uplodaRecyclingDataToFireBase();
            }*/
        }
    }

    private void uplodaRecyclingDataToFireBase() {
        double impactoCO2 = Double.parseDouble(plasticReferenceSavingList.get(0).get("economizacaoCO2").toString()) * quantity;
        double impactoEnergia = Double.parseDouble(plasticReferenceSavingList.get(0).get("economizacaoEnergia").toString()) * quantity;
        double impactoPetroleo = Double.parseDouble(plasticReferenceSavingList.get(0).get("economizacaoPetroleo").toString()) * quantity;


        for (int i = 0; i < plasticReferenceSavingList.size(); i++) {
            Toast.makeText(this, "Capacidade "+(i+1)+" : "+plasticReferenceSavingList.get(i).get("capacidade"), Toast.LENGTH_LONG).show();

            if(Double.parseDouble(capacidade) == Double.parseDouble(plasticReferenceSavingList.get(i).get("capacidade").toString())){
                //Toast.makeText(this, "TTTT", Toast.LENGTH_SHORT).show();
                sendDataToFireBase(plasticReferenceSavingList.get(i), contentUri);
            }
        }
/*        Toast.makeText(this, "Impacto CO2 :"+impactoCO2, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Impacto Energia:"+impactoEnergia, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Impacto Petroleo :"+impactoPetroleo, Toast.LENGTH_LONG).show();*/
    }

    private void sendDataToFireBase(QueryDocumentSnapshot queryDocumentSnapshot, Uri contentUri) {
        Map<String , Object> map = new HashMap<>();
        map.put("idUsuario", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("impactoCO2", Double.parseDouble(plasticReferenceSavingList.get(0).get("economizacaoCO2").toString()) * quantity);
        map.put("impactoEnergia", Double.parseDouble(plasticReferenceSavingList.get(0).get("economizacaoEnergia").toString()) * quantity);
        map.put("impactoPetroleo", Double.parseDouble(plasticReferenceSavingList.get(0).get("economizacaoPetroleo").toString()) * quantity);
        map.put("idFotografia", contentUri.toString());


        mStore.collection("reciclagens").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                //Start new activity to show user savings
                Toast.makeText(RegisterRecycle.this, "Reciclagem guardada!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
