package com.example.testinlogin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class HelpToRecycleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Button button_reconhecimento;
    private String capacidade;
    private String tipo;
    private String descricao;
    private int capacidadePos, descricaoPos, tipPos;
    private String collectionName;
    private ImageView imageView;
    private  String contentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_recycle);

        imageView = findViewById(R.id.photo_imageView);

        button_reconhecimento=findViewById(R.id.button_reconhecimento);
        button_reconhecimento.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        contentUri = bundle.getString("contentUri");
        Uri contentUri2 = Uri.parse(contentUri);
        Picasso.get().load(contentUri2).into(imageView);



        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        //CapacitySpinner
        Spinner capacitySpinner = findViewById(R.id.spinner_capacidade);
        ArrayAdapter<CharSequence> schoolSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_capacidade, android.R.layout.simple_spinner_item);
        capacitySpinner.setPrompt("Capacidade");
        schoolSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        capacitySpinner.setAdapter(schoolSpinnerAdapter);
        capacitySpinner.setOnItemSelectedListener(this);


        //TypeSpinner
        Spinner typeSpinner = findViewById(R.id.spinner_tipo);
        ArrayAdapter<CharSequence> typeOfUserSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_tipo, android.R.layout.simple_spinner_item);
        typeOfUserSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeOfUserSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(this);

        //DescriptionSpinner
        Spinner descriptionSpinner = findViewById(R.id.spinner_descrição);
        ArrayAdapter<CharSequence> typeOfMaterialSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_descrição, android.R.layout.simple_spinner_item);
        typeOfMaterialSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        descriptionSpinner.setAdapter(typeOfMaterialSpinnerAdapter);
        descriptionSpinner.setOnItemSelectedListener(this);

        /*Toolbar toolbar = findViewById(R.id.auxiliar_reconhecimento_ToolBar);
        toolbar.setTitle("Auxiliar Reconhecimento");
        setSupportActionBar(toolbar);*/

        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Estatísticas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        */
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId()== R.id.spinner_capacidade) {
            capacidade = parent.getItemAtPosition(position).toString();
            capacidadePos = position;

        } else if (parent.getId()== R.id.spinner_tipo) {
            tipo = parent.getItemAtPosition(position).toString();
            tipPos = position;

            //Set collectionName based on user selection
            if(position == 1){
                collectionName = "garrafa_de_agua";
            }else {
                collectionName = "garrafa_de_refrigerante";
            }

        } else if (parent.getId( )== R.id.spinner_descrição) {
            descricao = parent.getItemAtPosition(position).toString();
            descricaoPos = position;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_reconhecimento) {

            //Validate selected data from spinners
            if(capacidadePos == 0 || tipPos == 0 || descricaoPos == 0){
                Toast.makeText(this, "Por favor, selecione novamente!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(HelpToRecycleActivity.this, RegisterRecycle.class);
            intent.putExtra("capacidade", capacidade);
            intent.putExtra("tipo", tipo);
            intent.putExtra("descricao", descricao);
            intent.putExtra("collectionName", collectionName);
            intent.putExtra("contentUri", contentUri);
            startActivity(intent);
        }
    }
}


