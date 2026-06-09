package com.example.myapplicationworld.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplicationworld.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.cert.CertPathBuilderSpi;

public class PrestamoFragment extends Fragment
{
    Button btnTestWS, btnGuardarHerramienta, btnResetHerramienta;
    RequestQueue requestQueue; // Cola de solicitudes

    String condicion = "", tipo = ""; // RadioButton

    private final String URL = "http://192.168.101.62:3000/api/herramientas/"; //EndPoint

    EditText edtNombre, edtMarca, edtDescripcion;
    RadioButton rbtBueno, rbtRegular, rbtMalo; // Condicion
    RadioButton rbtManual, rbtElectrica; // Tipo

    // Constructor
    public PrestamoFragment(){}

    // Asociar el fragment con el XML
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prestamo, container, false);
    }

    /**
     * Este metodo retornara TRUE CUANDO EL FORMULARIO ESTE LISTO PARA EL REGISTRO(TODOS LOS CAMPOS ESTAN LLENOS
     * @return
     */
    private boolean readyUI(){
        boolean ready = true;

        if(edtNombre.getText().toString().isEmpty())ready = false;
        if(edtMarca.getText().toString().isEmpty())ready = false;
        if(edtDescripcion.getText().toString().isEmpty())ready = false;

        if(!rbtBueno.isChecked()
                && !rbtRegular.isChecked()
                && !rbtMalo.isChecked())ready = false;

        if(!rbtManual.isChecked()
                && !rbtElectrica.isChecked())ready = false;

        return ready;
    }

    /**
     * Regresa la UI(formulario) a su estado original
     */
    private void resetUI(){
        // desactivamos los editText
        edtDescripcion.setText("");
        edtNombre.setText("");
        edtMarca.setText("");

        // desactivamos los radioButton
        if(rbtBueno.isChecked())rbtBueno.setChecked(false);
        if(rbtRegular.isChecked())rbtRegular.setChecked(false);
        if(rbtMalo.isChecked())rbtMalo.setChecked(false);

        if(rbtManual.isChecked())rbtManual.setChecked(false);
        if(rbtElectrica.isChecked())rbtElectrica.setChecked(false);
    }
    private void registrarHerramienta(){
        // 0. Preparar el JSON
        // definr que condicion tiene?
        condicion = "";
        if(rbtBueno.isChecked())condicion = "Bueno";
        if(rbtRegular.isChecked())condicion = "Regular";
        if(rbtMalo.isChecked())condicion = "Malo";

        tipo = "";
        if(rbtManual.isChecked())tipo = "Manual";
        if(rbtElectrica.isChecked())tipo = "Electrica";

        JSONObject datosEnviar = new JSONObject();
        try {
            datosEnviar.put("nombre", edtNombre.getText().toString()); // EdtText
            datosEnviar.put("marca", edtMarca.getText().toString());
            datosEnviar.put("descripcion", edtDescripcion.getText().toString());
            datosEnviar.put("condicion", condicion);
            datosEnviar.put("tipo", tipo);
        }catch (Exception e){
            Log.e("ErrorJSON", e.toString());
        }

        // 1. Canal de comunicacion
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        // 2. Consumir WS >  Lectura de datos(JSON resultados)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                datosEnviar,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");
                            int id = jsonObject.getInt("id");

                            if(success){
                                resetUI();
                                Toast.makeText(getContext(), message+" - ID: "+id, Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            Log.e("ERRORJSON", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Cuando no realiza la operacion..
                        NetworkResponse response = volleyError.networkResponse;

                        if(response != null && response.data != null){
                            // STATUS CODE
                            int statusCode = response.statusCode;
                            // MESSAGE DETAILS
                            String errorJSON = new String(response.data);
                            // SHOW THE ERROR
                            Log.e("ErrorStatusCode", String.valueOf(statusCode)); // Codigo de error
                            //Log.e("ErrorWs", errorJSON);    // Mensaje de error

                            // Mostramos un dialogo
                        }
                    }
                }
        );

        // 3. Ejecucion
        requestQueue.add(jsonObjectRequest);

    }
    private void testWS(){
        //
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            Log.e("TEST", "Entró a onResponse");
                            boolean success = jsonObject.getBoolean("success");
                            String herramientas = "";

                            if(success){
                                // JSONArrayRequest = solicitud/ pedido
                                // JSONArray = contenedor

                                // Iterrar la clave data = []
                                JSONArray listaHerramientas = jsonObject.getJSONArray("data");

                                Toast.makeText(getContext(), listaHerramientas.toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.e("ErrorJSON", "No podemos leer JSON");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ErrorWs", volleyError.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // REFERENCIAS
        btnTestWS = view.findViewById(R.id.btnTestWS);
        btnGuardarHerramienta = view.findViewById(R.id.btnGuardarHerramientas);
        btnResetHerramienta = view.findViewById(R.id.btnResetHerramientas);

        edtNombre = view.findViewById(R.id.edtNombre);
        edtMarca = view.findViewById(R.id.edtMarca);
        edtDescripcion = view.findViewById(R.id.edtDescripcion);

        rbtBueno = view.findViewById(R.id.rbtBueno);
        rbtRegular = view.findViewById(R.id.rbtRegular);
        rbtMalo = view.findViewById(R.id.rbtMalo);

        rbtManual = view.findViewById(R.id.rbtManual);
        rbtElectrica = view.findViewById(R.id.rbtElectrica);

        btnTestWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWS();
                Toast.makeText(getContext(), "hellou", Toast.LENGTH_LONG).show();
            }
        });

        btnGuardarHerramienta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readyUI()){
                    // Confirmacion del proceso
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("App Herramientas");
                    builder.setMessage("¿Seguro de proceder con el registro?");
                    builder.setPositiveButton("Si",(a,b) -> {
                        registrarHerramienta();
                    });
                    builder.setNegativeButton("No", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    Toast.makeText(getContext(), "Complete el formulario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnResetHerramienta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUI();
            }
        });
    }
}
