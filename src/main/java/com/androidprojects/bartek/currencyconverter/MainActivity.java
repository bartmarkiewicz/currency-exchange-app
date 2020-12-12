package com.androidprojects.bartek.currencyconverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    long lastAPICall;

    private static class NetworkAsync extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                final StringBuffer jsonStr = new StringBuffer();
                HttpURLConnection urlConnection;
                urlConnection =(HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null){
                    jsonStr.append(line);
                }
                bufferedReader.close();
                return jsonStr.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;

            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }
        }
    }

    String[] arrayOfCurrencies;
    String[] conversionValues;
    Spinner toCurrency;
    Spinner fromCurrency;

    static public String apiCall(){
        //this probably works
        //it returns the string from the URL

        String result = "";
        try {
            result = new NetworkAsync().execute("http://data.fixer.io/api/latest?access_key=8eae2ef6f65a82f76f4c1535f18f8243&format=1").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public void parseJson(String jsonStr){
        try {
            System.out.println("Printingbegins here!!!!");
            System.out.println("\n" + jsonStr);
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject ratesObj =(JSONObject) jsonObj.get("rates");
            arrayOfCurrencies = new String[ratesObj.length()];
            conversionValues = new String[ratesObj.length()];
            JSONArray ratesArray = ratesObj.toJSONArray(ratesObj.names());

            SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            for(int i = 0; i < ratesObj.names().length(); i++){
                Currency currentCurrency = Currency.getInstance(ratesObj.names().get(i).toString());
                arrayOfCurrencies[i] = "(" + ratesObj.names().get(i).toString() + ") " + currentCurrency.getDisplayName();
                conversionValues[i] = Double.toString(ratesArray.getDouble(i));
                editor.putString("arrayOfCurrencies" + i, arrayOfCurrencies[i]);
                editor.putString("conversionValues" + i, conversionValues[i]);
            }
            editor.putInt("currenciesArrayLength", arrayOfCurrencies.length);
            editor.apply();
            DropDownMenu adapter = new DropDownMenu(this, android.R.layout.simple_list_item_1, arrayOfCurrencies);
            fromCurrency.setAdapter(adapter);
            toCurrency.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fromCurrency = findViewById(R.id.currencyFrom);
        toCurrency = findViewById(R.id.currencyTo);














        SharedPreferences saveData = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        lastAPICall = saveData.getLong("lastAPIcallByUser", 0 );
        DataCacher dataCacher = new DataCacher();
        if (lastAPICall <= System.currentTimeMillis() - 43200000){ //if API wasn't called within past 12 hours /*12h in miliseconds*/
            //call API

            String jsonToParse = apiCall();
            SharedPreferences.Editor editor = saveData.edit();
            lastAPICall = System.currentTimeMillis();
            editor.putLong("lastAPIcallByUser", lastAPICall);
            editor.apply();


            dataCacher.saveFile(this, "conversion.json", jsonToParse);
            parseJson(jsonToParse);

        } else {
            //use saved/cached data
            if (dataCacher.fileFound(this, "conversion.json")){ // if save data found
                String json = dataCacher.readFile(this, "conversion.json");
                //parse json
                if (!json.isEmpty()) {
                    parseJson(json);
                } else {
                    String jsonStr = apiCall();
                    if (jsonStr!=null && !jsonStr.isEmpty()) dataCacher.saveFile(this, "conversion.json", jsonStr);
                    parseJson(jsonStr);
                }

            } else {
                //file not found
                String jsonStr = apiCall();
                if(dataCacher.saveFile(this, "conversion.json", jsonStr)){
                    //file saved successfully
                } else {
                    System.out.println("File could not be saved");
                }
            }
        }

        fromCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateTheCurrency();

                /*String fromCurrencyStr = parent.getSelectedItem().toString();
                String toCurrencyStr = toCurrency.getSelectedItem().toString();
                TextView fromTV = findViewById(R.id.inputNum1);
                TextView toTV = findViewById(R.id.inputNum2);
                if (fromCurrencyStr != "" && toCurrencyStr != "" && fromTV.getText().length() > 0){
                    int locOfFrom = 0;
                    int locOfTo = 0;
                    for (int i = 0; i < arrayOfCurrencies.length; i++){
                        if (arrayOfCurrencies[i].equals(toCurrencyStr)){
                            locOfTo = i;
                        }
                        if (arrayOfCurrencies[i].equals(fromCurrencyStr)){
                            locOfFrom = i;
                        }
                    }
                    double euroEquivalentFrom = Double.parseDouble(conversionValues[locOfFrom]);
                    double euroEquivalentTo = Double.parseDouble(conversionValues[locOfTo]);

                        //Double toValue = Double.parseDouble(toTV.getText().toString());

                    Double fromValue = Double.parseDouble(fromTV.getText().toString());
                    Double fromInEuro = fromValue/euroEquivalentFrom;
                    Double result = (fromInEuro * euroEquivalentTo);

                    toTV.setText(String.format(Locale.getDefault(),"%.2f", result));

                    //converting 25 AFN to ALL
                    // 1 euro = 85.388559 AFN
                    //  0.2927 euro   = 25/85.388559 AFN = 25 AFN
                    //
                    // 1 euro = 125.36762 ALL
                    // 36.81 ALL  = 0.2927euro * 125.36762



                    // result * 1 = 25 / 0.853346
                    // 25*0.853346
                */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateTheCurrency();

                /*String fromCurrencyStr = fromCurrency.getSelectedItem().toString();
                String toCurrencyStr = parent.getSelectedItem().toString();
                TextView fromTV = findViewById(R.id.inputNum1);
                TextView toTV = findViewById(R.id.inputNum2);
                if (fromCurrencyStr != "" && toCurrencyStr != "" && fromTV.getText().length() > 0) {
                    int locOfFrom = 0;
                    int locOfTo = 0;
                    for (int i = 0; i < arrayOfCurrencies.length; i++){
                        if (arrayOfCurrencies[i].equals(toCurrencyStr)){
                            locOfTo = i;
                        }
                        if (arrayOfCurrencies[i].equals(fromCurrencyStr)){
                            locOfFrom = i;
                        }
                    }
                    double euroEquivalentFrom = Double.parseDouble(conversionValues[locOfFrom]);
                    double euroEquivalentTo = Double.parseDouble(conversionValues[locOfTo]);

                    //Double toValue = Double.parseDouble(toTV.getText().toString());

                    Double fromValue = Double.parseDouble(fromTV.getText().toString());
                    Double fromInEuro = fromValue / euroEquivalentFrom;
                    Double result = fromInEuro * euroEquivalentTo;

                    toTV.setText(String.format(Locale.getDefault(),"%.2f", result));

                } */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button calcBt = findViewById(R.id.calcBt);
        calcBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTheCurrency();
            }
        });





    }


    public void calculateTheCurrency() {
        String fromCurrencyStr = fromCurrency.getSelectedItem().toString();
        String toCurrencyStr = toCurrency.getSelectedItem().toString();
        TextView fromTV = findViewById(R.id.inputNum1);
        TextView toTV = findViewById(R.id.inputNum2);
        if (fromCurrencyStr != "" && toCurrencyStr != "" && fromTV.getText().length() > 0) {
            int locOfFrom = 0;
            int locOfTo = 0;
            for (int i = 0; i < arrayOfCurrencies.length; i++) {
                if (arrayOfCurrencies[i].equals(toCurrencyStr)) {
                    locOfTo = i;
                }
                if (arrayOfCurrencies[i].equals(fromCurrencyStr)) {
                    locOfFrom = i;
                }
            }
            double euroEquivalentFrom = Double.parseDouble(conversionValues[locOfFrom]);
            double euroEquivalentTo = Double.parseDouble(conversionValues[locOfTo]);

            //Double toValue = Double.parseDouble(toTV.getText().toString());

            Double fromValue = Double.parseDouble(fromTV.getText().toString());
            Double fromInEuro = fromValue / euroEquivalentFrom;
            Double result = (fromInEuro * euroEquivalentTo);

            toTV.setText(String.format(Locale.getDefault(), "%.2f", result));

            //converting 25 AFN to ALL
            // 1 euro = 85.388559 AFN
            //  0.2927 euro   = 25/85.388559 AFN = 25 AFN
            //
            // 1 euro = 125.36762 ALL
            // 36.81 ALL  = 0.2927euro * 125.36762


            // result * 1 = 25 / 0.853346
            // 25*0.853346
        }
    }
}
