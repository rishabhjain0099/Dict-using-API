package com.example.hrishabh.mydictionary;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView word;
    TextView mka;
    Button button;
    TextView meaning;
    String ans;
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView() {
        word = (AutoCompleteTextView) findViewById(R.id.word);
        mka = (TextView) findViewById(R.id.answer);
        button = (Button) findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ans = String.valueOf(word.getText());
                String languagePair = "en-hi";
                Translate(ans, languagePair);

                //new NetworkThread().execute(address);
                //Log.d("Hello", "On network thread");


            }
        });

    }

    void Translate(String textToBeTranslated,String languagePair){
        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
        AsyncTask<String, Void, String> translationResult = translatorBackgroundTask.execute(textToBeTranslated,languagePair); // Returns the translated text as a String
        Log.d("Translation Result", String.valueOf(translationResult)); // Logs the result in Android Monitor
    }


    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String>  {

        //Declare Context
        Context ctx;
        //Set Context
        TranslatorBackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            //String variables
            String textToBeTranslated = params[0];
            String languagePair = params[1];

            String jsonString;

            try {
                //Set up the translation call URL
                String yandexKey = "trnsl.1.1.20171029T065010Z.17f77f5b5eeb3478.1f3ebbadc4b9e7b581cf93109b40d8a9e4aeaeda";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                URL yandexTranslateURL = new URL(yandexUrl);

                //Set Http Conncection, Input Stream, and Buffered Reader
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                InputStream inputStream = httpJsonConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Set string builder and insert retrieved JSON result into it
                StringBuilder jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }

                //Close and disconnect
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();

                //Making result human readable
                String resultString = jsonStringBuilder.toString().trim();
                //Getting the characters between [ and ]
                resultString = resultString.substring(resultString.indexOf('[')+1);
                resultString = resultString.substring(0,resultString.indexOf("]"));
                //Getting the characters between " and "
                resultString = resultString.substring(resultString.indexOf("\"")+1);
                resultString = resultString.substring(0,resultString.indexOf("\""));

                Log.d("Translation Result:", resultString);
                return jsonStringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject reader = null;
            try {
                reader = new JSONObject(result);

            JSONArray sys  = reader.getJSONArray("text");
            for(int i =0 ;i<sys.length();i++){
                mka.setText(sys.getString(0));
            }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }




}

