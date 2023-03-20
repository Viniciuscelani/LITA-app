package com.vcelanim.lita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.cursoradapter.widget.SimpleCursorAdapter;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.ListFormatter;
import android.icu.text.ListFormatter.Width;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class RelatorioActivity extends AppCompatActivity {

    private Button botaoPDF;
    public String data_selecionada;

    private Spinner spinner;
    private List<String> listaDeDados;
    private ArrayAdapter<String> adapter;

    @SuppressLint({"LongLogTag", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        botaoPDF = findViewById(R.id.buttonGerarRelatorio);
        botaoPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gerarPDF();
            }
        });

        // 1. Criando um objeto de banco de dados SQLiteDatabase
        SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null );

        // 2. Executando uma consulta para buscar todas as datas distintas na coluna data_viagem da tabela passageiros
        Cursor cursor = bancoDados.rawQuery("SELECT DISTINCT data_viagem FROM passageiros", null);

        // 3. Armazenando as datas em um ArrayList
        ArrayList<String> datas = new ArrayList<>();

        while (cursor.moveToNext()) {
            String data = cursor.getString(cursor.getColumnIndexOrThrow("data_viagem"));
            datas.add(data);
        }

        cursor.close();

        // 4. Criando um ArrayAdapter para exibir as datas no editText
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, datas);

        // 5. Associando o ArrayAdapter ao editText usando o método setAdapter()
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner spinnerDatas = (Spinner) findViewById(R.id.spinner_datas);
        spinnerDatas.setAdapter(adapter);

        spinnerDatas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 data_selecionada = parent.getItemAtPosition(position).toString();
                // Faça algo com a data selecionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada selecionado
            }

        });

    }

    public void gerarPDF() {
        // Cria o objeto de documento com o tamanho da página e as margens
        Document document = new Document(PageSize.A4, 0, 0, 20, 20);

        Cursor cursor = null;
        SQLiteDatabase bancoDados = null;
        try {
            int numeroLinha = 1;
            // Abre o arquivo de saída para escrita
            File diretorio = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File arquivoPDF = new File(diretorio, "Relatorio.pdf");
            Uri uri = FileProvider.getUriForFile(this, "com.example.myapp.fileprovider", arquivoPDF);
            FileOutputStream fos = new FileOutputStream(arquivoPDF);

            // Cria o objeto de escrita do PDF
            PdfWriter writer = PdfWriter.getInstance(document, fos);

            // Adiciona as permissões de acesso ao arquivo
            writer.setStrictImageSequence(true);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            writer.setViewerPreferences(PdfWriter.PageModeUseThumbs);
            writer.createXmpMetadata();
            writer.setEncryption(null, null, PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);

            ////////////////////////////////////////////////////////////////////////////////////////



            // Abre o documento para escrita
            document.open();

            // Obtém o caminho absoluto da imagem


            // Cria a instância da imagem
            Image imagem = Image.getInstance(getClass().getClassLoader().getResource("res/drawable/brasil.png"));
            imagem.setAlignment(Element.ALIGN_CENTER);
            imagem.scaleAbsoluteWidth(200); // define a largura da imagem
            imagem.scaleAbsoluteHeight(imagem.getHeight() * 200 / imagem.getWidth());

            // Adiciona a imagem ao relatório
            document.add(imagem);

            // Cria o parágrafo com o título do documento
            Paragraph titulo = new Paragraph("Lista de Passageiros\n (Passanger List)");
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            document.add(titulo);

            Paragraph embarcacao_nome = new Paragraph("Nome da Embarcação: FB/DONA LITA");
            embarcacao_nome.setIndentationLeft(60);
            embarcacao_nome.setSpacingAfter((5f));


            document.add(embarcacao_nome);

            // Cria a tabela com as informações dos passageiros
            PdfPTable tabela = new PdfPTable(6);
            //tabela.setWidthPercentage(100);
            float[] columnWidths = {30f, 170f, 70f,70f,100f,100f}; // define a largura das colunas em pontos
            tabela.setWidths(columnWidths); // aplica as larguras definidas à tabela


            // Define os cabeçalhos da tabela
            PdfPCell cabecalho1 = new PdfPCell(new Phrase(" "));
            cabecalho1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cabecalho1.setFixedHeight(5);


            PdfPCell cabecalho2 = new PdfPCell(new Phrase("Nome"));
            cabecalho2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cabecalho2.setBackgroundColor(BaseColor.LIGHT_GRAY);

            PdfPCell cabecalho3 = new PdfPCell(new Phrase("Sexo"));
            cabecalho3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cabecalho3.setBackgroundColor(BaseColor.LIGHT_GRAY);

            //PdfPCell cabecalho4 = new PdfPCell(new Phrase("RG"));
            //cabecalho4.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cabecalho4.setBackgroundColor(BaseColor.LIGHT_GRAY);

            PdfPCell cabecalho4 = new PdfPCell(new Phrase("País"));
            cabecalho4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cabecalho4.setBackgroundColor(BaseColor.LIGHT_GRAY);

            PdfPCell cabecalho5 = new PdfPCell(new Phrase("Data de Nascimento"));
            cabecalho5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cabecalho5.setBackgroundColor(BaseColor.LIGHT_GRAY);

            PdfPCell cabecalho6 = new PdfPCell(new Phrase("Telefone"));
            cabecalho6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cabecalho6.setBackgroundColor(BaseColor.LIGHT_GRAY);

            // Adiciona os cabeçalhos à tabela
            tabela.addCell(cabecalho1);
            tabela.addCell(cabecalho2);
            tabela.addCell(cabecalho3);
            tabela.addCell(cabecalho4);
            tabela.addCell(cabecalho5);
            tabela.addCell(cabecalho6);
            //tabela.addCell(cabecalho7);

            // Preenche a tabela com os dados dos passageiros
            bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
            String[] args = {data_selecionada};
            cursor = bancoDados.rawQuery("SELECT nome_passageiro, sexo, pais, data_nascimento, telefone FROM passageiros where data_viagem = ? ", args);

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String nome = cursor.getString(cursor.getColumnIndex("nome_passageiro"));
                @SuppressLint("Range") String sexo = cursor.getString(cursor.getColumnIndex("sexo"));
               // @SuppressLint("Range") String rg = cursor.getString(cursor.getColumnIndex("rg"));
                @SuppressLint("Range") String pais = cursor.getString(cursor.getColumnIndex("pais"));
                @SuppressLint("Range") String dataNascimento = cursor.getString(cursor.getColumnIndex("data_nascimento"));
                @SuppressLint("Range") String telefone = cursor.getString(cursor.getColumnIndex("telefone"));

                tabela.addCell(new Phrase(Integer.toString(numeroLinha)));
                tabela.addCell(nome);
                tabela.addCell(sexo);
                //tabela.addCell(rg);
                tabela.addCell(pais);
                tabela.addCell(dataNascimento);
                tabela.addCell(telefone);

                numeroLinha++;
            }

            // Adiciona a tabela ao documento
            document.add(tabela);

            // Fecha o documento PDF
            document.close();

            Toast.makeText(this, "PDF gerado com sucesso!", Toast.LENGTH_SHORT).show();


            ////////////////////////////////////////////////////////////////////////////////////////



            // Cria um Intent para compartilhar o arquivo
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Inicia a atividade de compartilhamento
            startActivity(Intent.createChooser(shareIntent, "Compartilhar PDF"));

            // Cria o objeto de Intenção para abrir o arquivo PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(arquivoPDF), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // Cria o objeto de Intenção para fazer o download do arquivo PDF
            Intent intentDownload = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intentDownload.addCategory(Intent.CATEGORY_OPENABLE);
            intentDownload.setType("application/pdf");
            intentDownload.putExtra(Intent.EXTRA_TITLE, "Relatorio.pdf");

            // Inicia a atividade de escolha do usuário para abrir ou fazer o download do arquivo PDF
            //Intent chooserIntent = Intent.createChooser(intent, "Visualizar ou fazer download do PDF");
            //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentDownload});
            //startActivity(intentDownload);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar PDF: arquivo não encontrado.", Toast.LENGTH_SHORT).show();
        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar PDF: problema na criação do documento.", Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (bancoDados != null) {
                bancoDados.close();
            }
        }

    }

}

