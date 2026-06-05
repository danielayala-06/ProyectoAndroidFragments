package com.example.myapplicationworld.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class PrestamoFragment extends Fragment
{
    Button btnTestWS;
    RequestQueue requestQueue; // Cola de solicitudes

    private final String URL = "http://192.168.101.62:3000/api/herramientas/"; //EndPoint

    // Constructor
    public PrestamoFragment(){}

    // Asociar el fragment con el XML
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prestamo, container, false);
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

                                Toast.makeText(getContext(), "Existen datos", Toast.LENGTH_SHORT).show();
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

        btnTestWS = view.findViewById(R.id.btnTestWS);

        btnTestWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWS();
                Toast.makeText(getContext(), "hellou", Toast.LENGTH_LONG).show();
            }
        });
    }
}
