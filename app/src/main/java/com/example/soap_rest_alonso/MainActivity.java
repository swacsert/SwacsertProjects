package com.example.soap_rest_alonso;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Spinner sp1;
    Spinner sp2;
    EditText et_cantidad;
    TextView tv_resultado;
    ProgressDialog dialogo_progreso;
    CambiarMoneda cambiar = new CambiarMoneda();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        sp1 = (Spinner) findViewById(R.id.sp1);
        sp2 = (Spinner) findViewById(R.id.sp2);
        et_cantidad = (EditText) findViewById(R.id.et_cantidad);
        tv_resultado = (TextView) findViewById(R.id.tv_resultado);


        // Mi adaptador con el array de divisas disponibles
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this,R.array.divisas, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp1.setAdapter(adaptador);
        sp2.setAdapter(adaptador);

        dialogo_progreso = new ProgressDialog(this);
        dialogo_progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogo_progreso.setMessage("Esperando respuesta del servidor...");
        dialogo_progreso.setCancelable(false);

    }

    public void onclickSoap(View v){

        String de = sp1.getSelectedItem().toString();
        String a = sp2.getSelectedItem().toString();
        String cantidad = et_cantidad.getText().toString();


        if(cantidad.equals("") || cantidad.trim().equals("")){
            Toast.makeText(this,"Debes introducir una cantidad",Toast.LENGTH_SHORT).show();
        }else {
            new AsyncTask<String, Void, Double>() {

                @Override
                protected Double doInBackground(String... params) {

                    Double resultado;
                    resultado = cambiar.cambioSoap(params[0], params[1], Float.parseFloat(params[2]));
                    return resultado;
                }

                protected void onPreExecute(){
                    // Muestro mi dialogo de progreso
                    dialogo_progreso.show();
                }

                protected void onPostExecute(Double resultado) {

                    if (resultado != -1) {
                        tv_resultado.setText("El resultado de la conversión es " + String.valueOf(resultado));
                        dialogo_progreso.dismiss();
                        //dialogo_progreso.dismiss();
                    } else {
                        tv_resultado.setText("Ha ocurrido un error durante la ejecución");
                    }
                }

            }.execute(de, a, cantidad);
        }

    }

    public void onclickRest(View v){

        String de = sp1.getSelectedItem().toString();
        String a = sp2.getSelectedItem().toString();
        String cantidad = et_cantidad.getText().toString();


        if(cantidad.equals("") || cantidad.trim().equals("")){
            Toast.makeText(this,"Debes introducir una cantidad",Toast.LENGTH_SHORT).show();
        }else {
            new AsyncTask<String, Void, Double>() {

                @Override
                protected Double doInBackground(String... params) {

                    Double resultado;
                    resultado = cambiar.cambioRest(params[0], params[1], Float.parseFloat(params[2]));
                    return resultado;

                }
                protected void onPreExecute(){
                    // Muestro mi dialogo de progreso
                    dialogo_progreso.show();
                }


                protected void onPostExecute(Double resultado) {

                    if (resultado != -1) {
                        tv_resultado.setText("El resultado de la conversión es " + String.valueOf(resultado));
                        dialogo_progreso.dismiss();
                    } else {
                        tv_resultado.setText("Ha ocurrido un error durante la ejecución");
                    }
                    //dialogo_progreso.dismiss();
                }

            }.execute(de, a, cantidad);
        }
    }

    public void onClickServiciomail(View v){

        Intent i = new Intent(this,EmailActivity.class);
        startActivity(i);
    }

}
