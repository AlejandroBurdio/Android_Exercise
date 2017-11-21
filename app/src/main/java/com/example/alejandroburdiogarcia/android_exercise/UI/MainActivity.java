package com.example.alejandroburdiogarcia.android_exercise.UI;
/**
 * Created by Alejandro Burdío García on 21/11/2017.
 */

import com.example.alejandroburdiogarcia.android_exercise.Adapters.ListViewAdapter;
import  com.example.alejandroburdiogarcia.android_exercise.Constants.Urls;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alejandroburdiogarcia.android_exercise.Models.User;
import com.example.alejandroburdiogarcia.android_exercise.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Diseño
    private android.support.v7.app.ActionBar bar;
    Button getAll, getId, create, update, remove;
    ListView list;
    Spinner spnIds;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //Datos
    List<User> usuarios = new ArrayList<>();
    ArrayList<String> spnnumIds = new ArrayList<>();
    ArrayAdapter<String> numIds;

    //Variables
    String id_elegido, date, nombrecreado, fechacreada;
    Boolean borrar = false, updatear = false;
    int cday, cmonth, cyear, posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Funciones
        crearMenuSuperior();
        asociarVistas();
        botones();
    }

    private void asociarVistas() {
        //Inicializamos todas las variales de diseño
        //Botones
        getAll = findViewById(R.id.btn_get1);
        getId = findViewById(R.id.btn_getId);
        create = findViewById(R.id.btn_create);
        update = findViewById(R.id.btn_update);
        remove = findViewById(R.id.btn_remove);

        //Listview
        list = findViewById(R.id.listview);
    }
    private void botones(){
        //Añadimos funciones a los botones
        getAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getAll().execute();
            }
        });

        getId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuarios.size() == 0){
                    dialog_error();
                }
                else{
                    dialog_ids();
                }
            }});

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_crear();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuarios.size() == 0){
                    dialog_error();
                }
                else{
                    updatear = true;
                    dialog_updatear();                }
            }});

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuarios.size() == 0){
                    dialog_error();
                }
                else{
                    borrar = true;
                    dialog_ids();
                }
            }});
    }

    //GET y POST
    private class getAll extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        protected Void doInBackground(Void... arg0) {
            //Iniciamos la list de usuarios
            usuarios.clear();
            spnnumIds.clear();
            String data = "";
            try {

                progress.setMessage(getResources().getString(R.string.espere_porfavor));
                URL url = new URL(Urls.URL_GET_ALL);
                Log.i("URL",""+url);

                //Descargamos los datos de la URL indicada
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Metemos en data todo el contenido
                String line = "";
                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }
                //Guardamos los datos en la clase USER
                JSONArray JA = new JSONArray(data);
                for(int i =0 ;i <JA.length(); i++){
                    JSONObject JO = (JSONObject) JA.get(i);
                    int id = JO.getInt("id");
                    String name = JO.getString("name");
                    String birthdate = JO.getString("birthdate");

                    Log.i("ID",""+id+" Name:"+name+" Birthdate:"+birthdate);
                    User user = new User(id, name, birthdate);
                    usuarios.add(user);

                    //Guardamos los ids para buscar más adelante por ID
                    spnnumIds.add(String.valueOf(id));
                }

             //Excepciones
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            progress.dismiss();
            //Enviamos los datos de los usuarios al listview
            ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, usuarios);
            list.setAdapter(adapter);
        }
    }
    private class getId extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        protected Void doInBackground(Void... arg0) {
            //Iniciamos la list de usuarios
            usuarios.clear();
            String data = "";
            try {
                progress.setMessage(getResources().getString(R.string.espere_porfavor));
                URL url = new URL(Urls.URL_GET+id_elegido);
                Log.i("URL",""+url);
                //Descargamos los datos de la URL indicada
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                //Metemos en data todo el contenido
                String line = "";
                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                //Guardamos los datos en la clase USER
                JSONObject jsonObject = new JSONObject(data);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                String birthdate = jsonObject.getString("birthdate");
                Log.i("ID",""+id+" Name:"+name+" Birthdate:"+birthdate);
                User user = new User(id, name, birthdate);
                usuarios.add(user);

                //Excepciones
            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), "URL no válida", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            progress.dismiss();
            //Enviamos los datos de los usuarios al listview
            ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, usuarios);
            list.setAdapter(adapter);
        }
    }
    private class create extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        protected Void doInBackground(Void... arg0) {
            //Iniciamos la list de usuarios
            usuarios.clear();
            try {
                progress.setMessage(getResources().getString(R.string.espere_porfavor));
                URL url = new URL(Urls.URL_POST_CREATE);

                //Se introducen los datos anteriormente rellenados y se añade
                Map<String,Object> params = new LinkedHashMap<>();
                params.put("name", nombrecreado);
                params.put("birthdate", fechacreada);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    Log.i("param.getKey()",""+URLEncoder.encode(param.getKey(), "UTF-8"));
                    Log.i("param.getValue()",""+URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                //Creamos la conexión
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                httpURLConnection.setDoOutput(true);
                httpURLConnection.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

                for (int c; (c = in.read()) >= 0;)
                    System.out.print((char)c);

                //Excepciones
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.id_creado) ,Toast.LENGTH_LONG).show();

            return null;
        }
        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            progress.dismiss();
            //Enviamos los datos de los usuarios al listview
            new getAll().execute();
        }
    }
    private class update extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        protected Void doInBackground(Void... params) {
            //Iniciamos la list de usuarios
            try {
                progress.setMessage(getResources().getString(R.string.espere_porfavor));
                String data = "";
                URL url = new URL(Urls.URL_GET_ALL);
                Log.i("URL",""+url);

                //Descargamos los datos de la URL indicada
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Metemos en data todo el contenido
                String line = "";
                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                url = new URL (Urls.URL_POST_UPDATE);
                Log.i("URL",""+url);

                //Creando conexión
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoOutput (true);
                urlConn.setUseCaches (false);
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Content-Type","application/json");
                urlConn.connect();

                //Creando JSONObject y modificamos los campos
                JSONArray JA = new JSONArray(data);
                JSONObject jsonParam = new JSONObject(JA.get(posicion).toString());
                jsonParam.put("name", nombrecreado);
                jsonParam.put("birthdate", fechacreada);

                //Reescribimos el JSON
                OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
                wr.write(jsonParam.toString());
                Log.i("Lo que envía",""+jsonParam.toString());
                wr.flush();

                BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String line2;
                while ((line2 = serverAnswer.readLine()) != null) {
                    System.out.println("LINE: " + line2);
                }

                wr.close();
                serverAnswer.close();

                //Excepciones
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            progress.dismiss();
            new getAll().execute();
        }

    }
    private class remove extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        protected Void doInBackground(Void... arg0) {
            //Iniciamos la list de usuarios
            usuarios.clear();
            String data = "";
            try {
                progress.setMessage(getResources().getString(R.string.espere_porfavor));
                URL url = new URL(Urls.URL_GET_REMOVE+id_elegido);
                Log.i("URL",""+url);
                //Descargamos los datos de la URL indicada
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                //Metemos en data todo el contenido
                String line = "";
                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }
                //Excepciones
            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.url_no_valida), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            progress.dismiss();
            new getAll().execute();
        }
    }

    //DIÁLOGOS
    private void dialog_ids(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_elegir_ids, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getResources().getString(R.string.elige_ids));
        alert.setView(promptView);

        id_elegido = "";
        spnIds = promptView.findViewById(R.id.spnIds);
        numIds = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spnnumIds);
        spnIds.setAdapter(numIds);
        spnIds.setSelection(0);
        Log.i("ID ELEGIDO",""+id_elegido);

        alert.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                id_elegido =  spnIds.getSelectedItem().toString();
                if(borrar){
                    new remove().execute();
                    new getAll().execute();
                }
                else{
                    new getId().execute();
                }
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        final AlertDialog dialog = alert.create();
        dialog.show();
    }
    private void dialog_updatear(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_elegir_ids, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getResources().getString(R.string.updatear_usuario));
        alert.setView(promptView);

        posicion = 0;
        spnIds = promptView.findViewById(R.id.spnIds);
        numIds = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spnnumIds);
        spnIds.setAdapter(numIds);
        spnIds.setSelection(0);

        alert.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Cogemos la posicion para modificar los campos
                posicion =  spnIds.getSelectedItemPosition();
                Log.i("POS",""+posicion);
                if(updatear){
                    dialog_crear();
                }
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        final AlertDialog dialog = alert.create();
        dialog.show();
    }
    private void dialog_crear(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_crear, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getResources().getString(R.string.crear_usuario));
        alert.setView(promptView);

        final EditText nombre = promptView.findViewById(R.id.etCreaName);
        final TextView fecha = promptView.findViewById(R.id.txtFechaCreada);

        //Introducir la fecha
        fecha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final Calendar cal = Calendar.getInstance();
                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        cday = day;
                        cmonth = month + 1;
                        cyear = year;
                        date = cday + " - " + cmonth + " - " + cyear;
                        fecha.setText(date);
                    }
                };
                new DatePickerDialog(MainActivity.this, mDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();

                return false;
            }
        });

        alert.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                nombrecreado = nombre.getText().toString();
                fechacreada = fecha.getText().toString();

                if(updatear){
                    //Modificar
                    new update().execute();
                }
                else{
                    //Crear
                    new create().execute();
                }
                Log.i("Fecha",""+fechacreada+" nombre: "+nombrecreado);
                //Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_datos), Toast.LENGTH_LONG).show();
            }});

        alert.setNegativeButton(getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        final AlertDialog dialog = alert.create();
        dialog.show();
    }
    private void dialog_error(){
        AlertDialog.Builder noDatos = new AlertDialog.Builder(MainActivity.this);
        noDatos.setCancelable(false);
        noDatos.setMessage(getResources().getString(R.string.dialogo_error_no_datos));
        noDatos.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = noDatos.create();
        alert.show();
    }

    //Toolbar Superior
    void crearMenuSuperior() {
        bar = getSupportActionBar();

        LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);

        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);

        TextView texto = (TextView) mCustomView.findViewById(R.id.texto);
        texto.setText(getResources().getString(R.string.titulo));

        android.support.v7.app.ActionBar.LayoutParams layoutParams = new android.support.v7.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setCustomView(mCustomView, layoutParams);

        bar.setCustomView(mCustomView);
        bar.setDisplayShowCustomEnabled(true);
    }
}
