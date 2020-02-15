package com.example.soap_rest_alonso;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmailActivity  extends AppCompatActivity {

    ComprobarEmail comprobarEmail = new ComprobarEmail();
    EditText et_email;
    EditText et_timeout;
    TextView r_servidor;
    TextView r_pers;
    ProgressDialog dialogo_progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_activity);

        et_email = (EditText) findViewById(R.id.et_email);
        et_timeout = (EditText) findViewById(R.id.et_timeout);
        r_servidor = (TextView) findViewById(R.id.tv_rservidor);
        r_pers = (TextView) findViewById(R.id.tv_rpers);

        dialogo_progreso = new ProgressDialog(this);
        dialogo_progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogo_progreso.setMessage("Esperando respuesta del servidor...");
        dialogo_progreso.setCancelable(false);


    }

    public void onclickSOAP(View v){

        String email = et_email.getText().toString();
        String segundos = et_timeout.getText().toString();

        if(email.equals("") || email.trim().equals("") || segundos.equals("")){
            Toast.makeText(this,"No puedes dejar campos en blanco",Toast.LENGTH_SHORT).show();
        }else {
            new AsyncTask<String, Void, String[]>() {

                @Override
                protected String[] doInBackground(String... params) {
                    //Float.parseFloat(et_cantidad.getText().toString());
                    String[] resultado;
                    resultado = comprobarEmail.comprobarSoap(params[0], Integer.parseInt(params[1]));
                    return resultado;
                }
                protected void onPreExecute(){
                    // Muestro mi dialogo de progreso
                    dialogo_progreso.show();
                }

                protected void onPostExecute(String[] resultado) {

                    if (resultado != null) {
                        r_servidor.setText(resultado[0] + "\n" + resultado[1] + "\n" + resultado[2] + "\n" + resultado[3]);
                        // Realmente escribo un mensaje en base al tipo de código que me devuelve.
                        String msg = mensaje_personalizado(resultado[1]);
                        r_pers.setText(msg);
                        dialogo_progreso.dismiss();
                    } else {
                        r_servidor.setText("Ha ocurrido un error durante la ejecución");
                    }


                }

            }.execute(email, segundos);
        }
    }

    public void onclickREST(View v){

        String email = et_email.getText().toString();
        String segundos = et_timeout.getText().toString();

        if(email.equals("") || email.trim().equals("") || segundos.equals("")){
            Toast.makeText(this,"No puedes dejar campos en blanco",Toast.LENGTH_SHORT).show();
        }else {
            new AsyncTask<String, Void, String[]>() {

                @Override
                protected String[] doInBackground(String... params) {
                    //Float.parseFloat(et_cantidad.getText().toString());
                    String[] resultado;
                    resultado = comprobarEmail.comprobarRest(params[0], Integer.parseInt(params[1]));
                    return resultado;
                }
                protected void onPreExecute(){
                    // Muestro mi dialogo de progreso
                    dialogo_progreso.show();
                }

                protected void onPostExecute(String[] resultado) {

                    if (resultado != null) {
                        r_servidor.setText(resultado[0] + "\n" + resultado[1] + "\n" + resultado[2] + "\n" + resultado[3]);
                        // Realmente escribo un mensaje en base al tipo de código que me devuelve.
                        String msg = mensaje_personalizado(resultado[1]);
                        r_pers.setText(msg);
                        dialogo_progreso.dismiss();
                    } else {
                        r_servidor.setText("Ha ocurrido un error durante la ejecución");
                    }

                }

            }.execute(email, segundos);
        }

    }

    public String mensaje_personalizado(String msg){

        // No tengo controlado todos los código, debido a que tiene un límite de peticiones no he podido testearlas todas.
        if(msg.contains("9")){
            return "Este servicio tiene un límite de peticiones, debes esperar para volver a realizar más";
        }else if(msg.contains("0")){
            return  "El email está mal formado";
        }else if(msg.contains("2")){
            return "Email verificado correctamente y pertenece a un usuario.";
        }else if(msg.contains("3")){
            return  "El correo está bien formado y pertenece a un usuario";
        }else if(msg.contains("4")){
           return "El email está bien formado , pero no pertenece a ningún usuario, por lo que está libre !";
        }else if(msg.contains("5")){
            return  "No se ha encontrado ese dominio de email , prueba un timeout más grande para una respuesta más precisa";
        }else if(msg.contains("7")){
            return  "No es exacto el resultado, prueba un tiempo de espera mayor para resultados más precisos";
        }
        return "No existe mensaje personalizado para este código.";
    }


    }
