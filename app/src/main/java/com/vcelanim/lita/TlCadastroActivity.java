package com.vcelanim.lita;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class TlCadastroActivity extends AppCompatActivity {

    private EditText editTextEmbarcacao;
    private EditText editTextDataPartida;
    private EditText editTextNomePassageiro;
    private EditText editTextRg;
    private EditText editTextDataNascimento;
    private RadioButton sexoMasculino, sexoFeminino;
    private EditText editTextTelefone;
    private EditText editTextPais;

    private EditText editTextdestino;
    Button botaocad;

    private RadioGroup opcaoSexo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tl_cadastro);

        botaocad = findViewById(R.id.buttonSalvar);

        botaocad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarPassageiro();

            }

        });

        editTextEmbarcacao = findViewById(R.id.EditNomeEmbarcacao);
        editTextDataPartida = findViewById(R.id.EditPartida);
        editTextNomePassageiro = findViewById(R.id.EditNome);
        editTextRg = findViewById(R.id.EditRg);
        editTextDataNascimento = findViewById(R.id.editDataNascimento);
        sexoMasculino = findViewById(R.id.radioButtonMasculino);
        sexoFeminino = findViewById(R.id.radioButtonFeminino);
        editTextTelefone = findViewById(R.id.editTelefone);
        editTextPais = findViewById(R.id.EditPais);
        opcaoSexo = findViewById(R.id.RadioGroupSexo);
        editTextdestino = findViewById(R.id.EditDestino);
    }

    @SuppressLint("LongLogTag")
    public void cadastrarPassageiro(){

        if (!TextUtils.isEmpty(editTextEmbarcacao.getText().toString())){




            try {

                int opcaoSelecionada = opcaoSexo.getCheckedRadioButtonId();

                RadioButton rbSexoSelecionado = findViewById(opcaoSelecionada);


                Log.i("algo","entrou");
                SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null );
                String sql = "INSERT INTO passageiros(embarcacao, data_viagem, destino, nome_passageiro, rg, data_nascimento, sexo, telefone, pais) VALUES (?,?,?,?,?,?,?,?,?)";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1,editTextEmbarcacao.getText().toString());
                stmt.bindString(2,editTextDataPartida.getText().toString());
                stmt.bindString(3,editTextdestino.getText().toString());
                stmt.bindString(4,editTextNomePassageiro.getText().toString());
                stmt.bindString(5,editTextRg.getText().toString());
                stmt.bindString(6,editTextDataNascimento.getText().toString());
                stmt.bindString(7,rbSexoSelecionado.getText().toString());
                stmt.bindString(8,editTextTelefone.getText().toString());
                stmt.bindString(9,editTextPais.getText().toString());
                stmt.execute();
                Toast.makeText(this, "Passageiro Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();

                Cursor cursor = bancoDados.rawQuery("SELECT nome_passageiro, sexo, embarcacao,destino, rg, pais, data_nascimento, telefone, data_viagem FROM passageiros", null);

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
}