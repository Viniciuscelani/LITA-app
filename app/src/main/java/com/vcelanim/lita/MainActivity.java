package com.vcelanim.lita;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonTlCad, buttonRel;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTlCad = findViewById(R.id.buttonCadastrar);

        buttonRel = findViewById(R.id.buttonRelatorio);

        buttonRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RelatorioActivity.class);
                startActivity(intent);
            }
        });


        buttonTlCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), TlCadastroActivity.class);
                startActivity(intent);

            }
        });

        try {

            //criar banco
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null );

            //criar tabela
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS passageiros (embarcacao VARCHAR, data_viagem DATE, destino VARCHAR, nome_passageiro VARCHAR, rg  INT(8), data_nascimento DATE, sexo VARCHAR, telefone INT(11), pais VARCHAR )");

            //inserir dados
            //bancoDados.execSQL("INSERT INTO passageiros(embarcacao, data_viagem, destino, nome_passageiro, rg, data_nascimento, sexo, telefone, pais) VALUES('Dona lita','05/03/2023', 'Manaus/Fonte Boa', 'vinicius', 32352964, '11/12/2000', 'masculino', 92992498996, 'Brasil' )");

            //recuperar dados
            Cursor cursor = bancoDados.rawQuery("SELECT nome_passageiro, sexo, embarcacao, destino, rg, pais, data_nascimento, telefone, data_viagem FROM passageiros", null);

            //indices tabela
            int indiceNome = cursor.getColumnIndex("nome_passageiro");
            int indiceSexo = cursor.getColumnIndex("sexo");
            int indiceEmbarcacao = cursor.getColumnIndex("embarcacao");
            int indiceDestino = cursor.getColumnIndex("destino");
            int indiceRg = cursor.getColumnIndex("rg");
            int indicePais = cursor.getColumnIndex("pais");
            int indiceDataNasc = cursor.getColumnIndex("data_nascimento");
            int indiceTelefone = cursor.getColumnIndex("telefone");
            int indiceDataViagem = cursor.getColumnIndex("data_viagem");


            cursor.moveToFirst();
            while (cursor != null){
                Log.i("RESULTADO - nome:", cursor.getString(indiceNome));
                Log.i("RESULTADO - sexo:", cursor.getString(indiceSexo));
                Log.i("RESULTADO - Embarcacao:", cursor.getString(indiceEmbarcacao));
                Log.i("RESULTADO - destino:", cursor.getString(indiceDestino));
                Log.i("RESULTADO - rg:", cursor.getString(indiceRg));
                Log.i("RESULTADO - pais:", cursor.getString(indicePais));
                Log.i("RESULTADO - data nascimento:", cursor.getString(indiceDataNasc));
                Log.i("RESULTADO - telefone:", cursor.getString(indiceTelefone));
                Log.i("RESULTADO - data viagem:", cursor.getString(indiceDataViagem));
                cursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}