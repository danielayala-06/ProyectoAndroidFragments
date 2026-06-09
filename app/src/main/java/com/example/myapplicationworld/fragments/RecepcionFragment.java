package com.example.myapplicationworld.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplicationworld.R;

import org.json.JSONObject;

public class RecepcionFragment extends Fragment
{
    // Para la comnuicacion con el WS
    private final String URL = "http://192.168.101.62:3000/api/herramientas/"; //EndPoint
    RequestQueue requestQueue; // Cola de solicitudes


    // EdtText
    EditText resNombre, resMarca, resDescripcion;
    EditText edtBuscarID;

    // Button
    Button btnBuscarId, btnActualizar;

    // RadioButtons
    RadioButton resrbtBueno, resrbtRegular, resrbtMalo;
    RadioButton resrbtElectrico, resrbtManual;
    String condicion="", tipo="";
    //constructor
    public RecepcionFragment(){}

    // Asociar el fragment con el XML
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recepcion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resNombre = view.findViewById(R.id.resNombre);
        resMarca = view.findViewById(R.id.resMarca);
        resDescripcion = view.findViewById(R.id.resDescripcion);
        edtBuscarID = view.findViewById(R.id.edtBuscarID);

        // Radio Buttons
        resrbtBueno = view.findViewById(R.id.resrbtBueno);
        resrbtRegular = view.findViewById(R.id.resrbtRegular);
        resrbtMalo = view.findViewById(R.id.resrbtMalo);

        resrbtManual = view.findViewById(R.id.resrbtManual);
        resrbtElectrico = view.findViewById(R.id.resrbtElectrica);


        btnBuscarId = view.findViewById(R.id.btnBuscarHerramienta);
        btnActualizar = view.findViewById(R.id.btnActualizarHerramienta);

        btnBuscarId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarHerramienta();
            }
        });
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readyUI()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Actualizar Registro");
                    builder.setMessage("¿Estas seguro de actualizar?");
                    builder.setPositiveButton("Aceptar", (a,b)->{
                        actualizarHerramienta();
                    });
                    builder.setNegativeButton("Cancelar", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{
                    Toast.makeText(getContext(), "Ingrese todos los datos porfavor", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void buscarHerramienta(){
        int idHerramienta = Integer.parseInt(edtBuscarID.getText().toString());
        String endponint = URL+idHerramienta;
        if(idHerramienta<1){
            Toast.makeText(getContext(), "Inserte un ID valido", Toast.LENGTH_SHORT).show();
        }
        try {
            // Canal de comunicaciones
           requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

           // Consumimos el ws
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    endponint,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                boolean success = jsonObject.getBoolean("success");
                                if(success){
                                    JSONObject registro = jsonObject.getJSONObject("data");
                                    resNombre.setText(registro.getString("nombre"));
                                    resMarca.setText(registro.getString("marca"));
                                    resDescripcion.setText(registro.getString("descripcion"));

                                    seleccionarCondicion(registro.getString("condicion"));
                                    seleccionarTipo(registro.getString("tipo"));
                                }
                            } catch (Exception e)
                            {
                                Log.e("Error", e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("ErrorWS", volleyError.toString());
                            // Es posible qu eel WS nos envie un codigo de error 4xx, 5xx
                            NetworkResponse response = volleyError.networkResponse;
                            if(response != null && response.data != null){
                                // Codigo (int)
                                int statusCode = response.statusCode;

                                // Contenido (String)
                                String messageJSON = new String(response.data);

                                try {
                                    JSONObject jsonWS = new JSONObject(messageJSON);
                                    String message = jsonWS.getString("message");

                                    // Enviamos un mensaje
                                    if(statusCode == 404){
                                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                    }

                                } catch (Exception e) {
                                    Log.e("ErrorJSON", e.toString());
                                }
                            }
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.e("ErrorWS", e.toString());
        }
    }

    public void actualizarHerramienta(){
        try {
            // Preparamos los datos a enviar
            String tipo="", condicion="";
            String endpoint = URL+edtBuscarID.getText().toString();

            if(resrbtBueno.isChecked())condicion = "bueno";
            if(resrbtRegular.isChecked())condicion = "regular";
            if(resrbtMalo.isChecked())condicion = "malo";

            if(resrbtElectri co.isChecked())tipo = "electrica";
            if(resrbtManual.isChecked())tipo = "manual";

            JSONObject data = new JSONObject();

            data.put("nombre", resNombre.getText().toString());
            data.put("marca", resMarca.getText().toString());
            data.put("descripcion", resDescripcion.getText().toString());
            data.put("condicion", condicion);
            data.put("tipo", tipo);

            requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    endpoint,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            try {
                                boolean success = jsonObject.getBoolean("success");
                                String message = jsonObject.getString("message");

                                if(!success){
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                Log.e("ErrorJSON", e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("ErrorREQUEST", volleyError.toString());
                        }
                    }
            );

            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            Log.e("ErrorJSON", e.toString());
        }
    }
    public void seleccionarCondicion(String condicion){
        if(condicion.equals("bueno"))resrbtBueno.setChecked(true);
        if(condicion.equals("regular"))resrbtRegular.setChecked(true);
        if(condicion.equals("malo"))resrbtMalo.setChecked(true);
    }

    public void seleccionarTipo(String tipo){
        if(tipo.equals("manual"))resrbtManual.setChecked(true);
        if(tipo.equals("electrica"))resrbtElectrico.setChecked(true);

    }

    private boolean readyUI(){
        boolean ready = true;

        if(resNombre.getText().toString().isEmpty())ready = false;
        if(resMarca.getText().toString().isEmpty())ready = false;
        if(resDescripcion.getText().toString().isEmpty())ready = false;

        if(!resrbtBueno.isChecked()
                && !resrbtRegular.isChecked()
                && !resrbtMalo.isChecked())ready = false;

        if(!resrbtManual.isChecked()
                && !resrbtElectrico.isChecked())ready = false;

        return ready;
    }
}
