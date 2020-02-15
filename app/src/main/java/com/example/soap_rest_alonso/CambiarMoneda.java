package com.example.soap_rest_alonso;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class CambiarMoneda {

    //String fecha_actual = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    public double cambioSoap(String de , String a , float cantidad){

        //Es el campo que viene seguido del XMLNS =
        String NAMESPACE = "http://tempuri.org/";
        // La url desde donde vamos a usar el servicio
        String URL = "http://currencyconverter.kowabunga.net/converter.asmx?op=GetConversionAmount";
        // El nombre del método que hemos escogido entre todos los que hay en el web service.
        String METODO = "GetConversionAmount";
        // Etiqueta que viene en la URL que nos indica la acción del SOAP
        String SOAPACTION = "http://tempuri.org/GetConversionAmount";

        //Objecto SOAP para reaalizar la solicitud del servicio
        SoapObject respuesta = new SoapObject(NAMESPACE, METODO);

        // Le introducimos los parámetros
        //POST /converter.asmx/GetConversionAmount HTTP/1.1
        // Con ayuda de la URL(HTTP POST) vemos claramente como se introducen los campos
        // CurrencyFrom=string&CurrencyTo=string&RateDate=string&Amount=string
        respuesta.addProperty("CurrencyFrom",de);
        respuesta.addProperty("CurrencyTo", a);
        //respuesta.addProperty("RateDate", fecha_actual);
        // Por alguna razón no deja fecha actual
        respuesta.addProperty("RateDate", "2020-02-01");
        respuesta.addProperty("Amount",String.valueOf(cantidad));

        // Creamos el "envoltorio" de nuestro objecto Soap que es la respuesta del servidor
        SoapSerializationEnvelope envoltorio = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envoltorio.dotNet = true;
        //Metemos nuestro objeto en el envoltorio
        envoltorio.setOutputSoapObject(respuesta);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            // Llamamos al servicio, le pasamos nuestro Soap_action y el envoltorio que contiene nuestro objecto Soap serializado
           transporte.call(SOAPACTION, envoltorio);
           // Recogemos el tv_resultado, sólo nos devuelve un valor que es un objecto de tipo SoapPrimitive
            SoapPrimitive resultadoxml = (SoapPrimitive) envoltorio.getResponse();
            //SoapObject resultadoxml = (SoapObject) envoltorio.getResponse();


            // Lo pasamos a string y finalmente lo devolvemos como double.
            String resultado = resultadoxml.toString();
            return Double.parseDouble(resultado);


        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }

    public double cambioRest(final String de,final  String a , final float cantidad){
        // Esta vez usaremos el XMLPULLPARSER
        // Esta vez le pasamos los parámetros con el http GET como indica la página
        double resultado = 0;

        try{
            // Host: currencyconverter.kowabunga.net
            //GET /converter.asmx/GetConversionAmount?CurrencyFrom=string&CurrencyTo=string&RateDate=string&Amount=string HTTP/1.1
            // Simplemente formamos la URL y metemos nuestros parámetros.
            URL url = new URL("http://currencyconverter.kowabunga.net/" +
                    "converter.asmx/GetConversionAmount?CurrencyFrom="+de+"&CurrencyTo="+a+"&RateDate=2020-02-01&Amount="+cantidad);
            // Establecemos la conexion con el servidor
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            // indicamos al servidor que queremos que nos envie datos
            conexion.setRequestMethod("GET");
            // Solicitamos que tipo de servicio queremos , en este caso el servicio sólo proporciona XML
            // realmente no afecta, ya que por defecto al tener solo un tipo, devuelve XML
            conexion.setRequestProperty("Accept" , "application/xml");
            // Usamos un bufferedreader para leer los datos del xml que nos devuelve
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String linea="";
            // Vamos a volver todo el contenido en un StringBuilder , es decir, por cada linea que pueda leer se añadirá ahí.
            StringBuilder sb =  new StringBuilder();

            while ((linea = br.readLine()) != null){
                sb.append(linea);
            }
            // Creamos el string generado previamente
            String str_resultado = sb.toString();
            // Se crea una "Factoria" del objecto XMLPullparserFactory
            XmlPullParserFactory factory =  XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            // Creamos el objecto XMLpullparser
            XmlPullParser xpp = factory.newPullParser();
            // Le indicamos que los datos que se debe leer son los de nuestra String resultado de leer todo el XML
            xpp.setInput(new StringReader(str_resultado));
            /* Es similar al estilo SAX , van sucediendo eventos , sabemos que el xml acaba cuando ocurre el evento
            END_DOCUMENT , a partir de ahí cuando empieza con STAR_DOCUMENT , vamos controlando el inicio y el final
            de las etiquetas , aunque realmente lo único que nos interesa es almacenar el TEXTO en nuestra variable resultado
            */
            int tipo_de_evento = xpp.getEventType();

            // Los he almaceenado en variables para que quede un código mas legible
            int fin_doc = XmlPullParser.END_DOCUMENT;
            int inicio_doc = XmlPullParser.START_DOCUMENT;
            int inicio_etiqueta = XmlPullParser.START_TAG;
            int fin_etiqueta = XmlPullParser.END_TAG;
            int texto_etiqueta = XmlPullParser.TEXT;

            while (tipo_de_evento != fin_doc){
                if(tipo_de_evento == inicio_doc){
                    System.out.println("Inicio del Documento");
                }else if(tipo_de_evento == inicio_etiqueta){
                    System.out.println("Inicio de Etiqueta");
                }else if(tipo_de_evento == fin_etiqueta){
                    System.out.println("Fin del Etiqueta");
                }else if(tipo_de_evento == texto_etiqueta){
                    // Guardamos el texto en nuestra variable resultado
                    //String prueba = xpp.getText();
                    resultado = Double.parseDouble(xpp.getText());
                }
                tipo_de_evento = xpp.next();
            }


            return resultado;

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }
}
