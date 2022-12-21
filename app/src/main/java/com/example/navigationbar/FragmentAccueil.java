package com.example.navigationbar;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAccueil extends Fragment implements Response.Listener<Bitmap>{

    private ImageView viewer;
    private TextView ville;
    private TextView description;
    private TextView temp;
    private TextView temp_max;
    private TextView temp_min;
    private TextView vit_vent;
    private TextView pression_atm;
    private TextView humidite;
    private TextView direction;
    private ProgressDialog progress;
    private Weather weather;
    private static String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?id=524901&units=metric&lang=FR&APPID=c0bbff4a824cce23670fa594dfa7e8b1";
    private static String IMG_URL = "https://openweathermap.org/img/w/";
    RequestQueue queue;
    String nomVille;

    //Speech to text
    private ImageButton btnSpeak;
    protected static final int RESULT_SPEECH = 1;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accueil, container, false);
        nomVille="angers";

        // Instancie la file de message (cet objet doit être un singleton)

        queue = Volley.newRequestQueue(getActivity());
        MiseAJour();
        this.viewer = (ImageView) v.findViewById(R.id.imageV);
        this.ville = (TextView) v.findViewById(R.id.ville);
        this.description = (TextView) v.findViewById(R.id.desc);
        this.temp = (TextView) v.findViewById(R.id.temp);
        this.temp_max = (TextView) v.findViewById(R.id.temp_max);
        this.temp_min = (TextView) v.findViewById(R.id.temp_min);
        this.vit_vent = (TextView) v.findViewById(R.id.vitvent);
        this.pression_atm = (TextView) v.findViewById(R.id.pression);
        this.humidite = (TextView) v.findViewById(R.id.humidite);
        this.direction = (TextView) v.findViewById(R.id.direction);

        Button btnRefresh = (Button) v.findViewById(R.id.refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View actuelView) {
                MiseAJour();
            }
        });

        btnSpeak = (ImageButton) v.findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // lancement de l'intent de reconnaisance vocal
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                // on sp�cifie le langage
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");

                try {
                    // on demarre l'activity pour etre sur que l'api nous
                    // retourne le code correctement
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {// leve exception
                    Toast t = Toast.makeText(v.getContext(),
                            "Oups! Votre appareil ne supporte pas cette API",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        return v;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // si resultat ok
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    // on recupere dans un arrayliste le texte
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    nomVille=text.get(0);
                    MiseAJour();
                }
                break;
            }

        }
    }
    public void MiseAJour(){

        this.progress = new ProgressDialog(getActivity());
        this.progress.setTitle("Veuillez patienter");
        this.progress.setMessage("Récupération de des informations météos en cours...");
        this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progress.show();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL +"&q="+nomVille, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        weather = new Weather();
                        try {
                            Log.e("tab",response.toString());
                            weather = JSONWeatherParser.getWeather(response.toString());

                            ville.setText("Nom Ville: " + nomVille);
                            temp_max.setText("température max: " + weather.temperature.getMaxTemp() + "Degrés Celcius");
                            temp_min.setText("température min: " +  weather.temperature.getMinTemp());
                            temp.setText("température: " + weather.temperature.getTemp());
                            description.setText("Description: " + weather.currentCondition.getDescr());
                            pression_atm.setText("pression Atmospherique: "+ weather.currentCondition.getPressure()+ "HP");
                            vit_vent.setText("Vitesse du vent:" + weather.wind.getSpeed());
                            humidite.setText("Humidité:" +  weather.currentCondition.getHumidity() +"%");
                            direction.setText("Direction du vent:" + weather.wind.getDeg() + " degrés");


                            downloadImage(weather.currentCondition.getIcon(), queue);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error Volley", error.toString());
                        Toast t = Toast.makeText(getContext(),
                                "La ville "+nomVille +" n'existe pas!",
                                Toast.LENGTH_SHORT);
                        t.show();

                    }
                });

        if (progress.isShowing()) progress.dismiss();
        queue.add(jsObjRequest);
    }
    public void downloadImage(String pathImg, RequestQueue queue) {
        // Requête d'une image à l'URL demandée
        Log.i("Image down path:", pathImg);
        ImageRequest picRequest = new ImageRequest(IMG_URL + pathImg + ".png?APPID=c0bbff4a824cce23670fa594dfa7e8b1",  this, 0, 0, null, null);
        // Insère la requête dans la file
        queue.add(picRequest);
    }

    @Override
    public void onResponse(Bitmap response) { //callback en cas de succès
        //fermeture de la boite de dialogue
        if (this.progress.isShowing()) this.progress.dismiss();

        Bitmap bm = Bitmap.createScaledBitmap(response, 400, 400, true);
        this.viewer.setImageBitmap(bm);


    }
}