package com.example.soap_rest_alonso;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
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

public class ComprobarEmail {

    public String[] comprobarSoap(String email,int timeout){

        //Es el campo que viene seguido del XMLNS = xmlns="http://ws.cdyne.com/"
        String NAMESPACE = "http://ws.cdyne.com/";
        // La url desde donde vamos a usar el servicio : http://ws.cdyne.com/emailverify/Emailvernotestemail.asmx?op=AdvancedVerifyEmail
        String URL = "http://ws.cdyne.com/emailverify/Emailvernotestemail.asmx?op=AdvancedVerifyEmail";
        // El nombre del método que hemos escogido entre todos los que hay en el web service.
        String METODO = "AdvancedVerifyEmail";
        // Etiqueta que viene en la URL que nos indica la acción del SOAP
        String SOAPACTION = "http://ws.cdyne.com/AdvancedVerifyEmail";

        //Objecto SOAP para reaalizar la solicitud del servicio
        SoapObject respuesta = new SoapObject(NAMESPACE, METODO);

        // Le introducimos los parámetros
        // POST /emailverify/Emailvernotestemail.asmx/AdvancedVerifyEmail HTTP/1.1
        // Con ayuda de la URL(HTTP POST) vemos claramente como se introducen los campos
        // email=string&timeout=string&LicenseKey=string
        respuesta.addProperty("email",email);
        respuesta.addProperty("timeout", String.valueOf(timeout));
        //respuesta.addProperty("timeout","4000");
        respuesta.addProperty("LicenseKey", "0");

        // Creamos el "envoltorio" de nuestro objecto Soap que es la respuesta del servidor
        SoapSerializationEnvelope envoltorio = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envoltorio.dotNet = true;
        //Metemos nuestro objeto en el envoltorio
        envoltorio.setOutputSoapObject(respuesta);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            // Llamamos al servicio, le pasamos nuestro Soap_action y el envoltorio que contiene nuestro objecto Soap serializado
            transporte.call(SOAPACTION, envoltorio);
            // Podriamos usar tambien el método getResponse o getObject , realmente siempre devuelve un tipo SoapPrimitive
            SoapObject resultado_soap = (SoapObject) envoltorio.bodyIn;
            // Sacamos la propiedad de nuestro objeto , a partir de esta sacamos las demás.
            // Si tuviese un esquema más complejo deberíamos recorrerla como un arraymultidimensional, por suerte devuelve sólo uno.
            SoapObject propiedad =(SoapObject) resultado_soap.getProperty(0);
            // Lo pasamos a un array de strings
            String [] resultados = new String[4];
            // Voy recogiendo las propiedades , muestro el nombre de la propiedadd y el texto correspondiente de esta.
            for(int i = 0; i <=3 ; i ++){
                // Ej:  Responsetext = "Invalid Email Address" , aunque es más facil trabajar solo con el texto
                // y no con el nombre de la propiedad, por eso no lo recojo en el array
                PropertyInfo pi = new PropertyInfo();
                propiedad.getPropertyInfo(i,pi);
                resultados[i] = pi.getName() + " = "+ propiedad.getProperty(i).toString();

                //resultados[i] =  propiedad.getProperty(i).toString();
            }
            return resultados;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public String[] comprobarRest(final String email, final int timeout){

        // Esta vez usaremos el XMLPULLPARSER
        // Esta vez le pasamos los parámetros con el http GET como indica la página
        String resultados[] = new String[4];

        try{
            // Host: ws.cdyne.com
            //GET /emailverify/Emailvernotestemail.asmx/AdvancedVerifyEmail?email=string&timeout=string&LicenseKey=string HTTP/1.1
            // Simplemente formamos la URL y metemos nuestros parámetros.
            URL url = new URL("http://ws.cdyne.com/" +
                    "emailverify/Emailvernotestemail.asmx/AdvancedVerifyEmail?email="+ email + "&timeout="+timeout+"&LicenseKey=0");

            //URL url = new URL("http://ws.cdyne.com/" +
              //      "emailverify/Emailvernotestemail.asmx/AdvancedVerifyEmail?email="+ email + "&timeout=400&LicenseKey=0");

            // Establecemos la conexion con el servidor
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            // indicamos al servidor que queremos que nos envie datos
            conexion.setRequestMethod("GET");
            // Solicitamos que tipo de servicio queremos , en este caso el servicio sólo proporciona XML
            // realmente no afecta en este caso, ya que por defecto al tener solo un tipo, devuelve XML
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


            int i = 0;

            while (tipo_de_evento != fin_doc){
                if(tipo_de_evento == inicio_doc){
                    System.out.println("Inicio del Documento");
                }else if(tipo_de_evento == inicio_etiqueta){
                    System.out.println("Inicio de Etiqueta");
                }else if(tipo_de_evento == fin_etiqueta){
                    System.out.println("Fin del Etiqueta");
                }else if(tipo_de_evento == texto_etiqueta){
                    // Guardamos el texto en nuestra variable resultados
                    if(xpp.getText().toString().equals("") || xpp.getText().toString().trim().equals("")){
                        // Cada vez que entra en este if, significa que existen etiquetas en blanco
                        // por lo cual no las recojo, sino me devolvería más datos en mi array.
                    }else{
                        // Por cada dato que añado, sumo 1 al contador hasta llegar a mi límite
                        resultados[i] = xpp.getText().toString();
                        i++;
                    }
                }
                tipo_de_evento = xpp.next();
            }

            return resultados;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }




}
