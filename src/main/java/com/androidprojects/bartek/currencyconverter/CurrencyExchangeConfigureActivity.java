package com.androidprojects.bartek.currencyconverter;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;

/**
 * The configuration screen for the {@link CurrencyExchange CurrencyExchange} AppWidget.
 */
public class CurrencyExchangeConfigureActivity extends Activity {

    private Spinner fromSpinner;
    private Spinner toSpinner;
    private String[] arrayOfCurrencies;
    private String[] conversionValues;
    private static final String PREFS_NAME = "com.androidprojects.bartek.currencyconverter.CurrencyExchange";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = CurrencyExchangeConfigureActivity.this;

            // When the button is clicked, store the string locally
            //saveTitlePref(context, mAppWidgetId, widgetText);
            setTheCurrencies();
            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            CurrencyExchange.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public CurrencyExchangeConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    public void parseJson(String json){
        try {
            System.out.println("Printingbegins here!!!!");
            System.out.println("\n" + json);
            JSONObject jsonObj = new JSONObject(json);
            JSONObject ratesObj =(JSONObject) jsonObj.get("rates");
            arrayOfCurrencies = new String[ratesObj.length()];
            conversionValues = new String[ratesObj.length()];
            JSONArray ratesArray = ratesObj.toJSONArray(ratesObj.names());


            for(int i = 0; i < ratesObj.names().length(); i++){
                Currency currentCurrency = Currency.getInstance(ratesObj.names().get(i).toString());
                arrayOfCurrencies[i] = "(" + ratesObj.names().get(i).toString() + ") " + currentCurrency.getDisplayName();
                conversionValues[i] = Double.toString(ratesArray.getDouble(i));
            }
            SharedPreferences.Editor editor = getSharedPreferences("prefs",MODE_PRIVATE).edit();
            editor.putInt("currenciesArrayLength", arrayOfCurrencies.length);


            fromSpinner = findViewById(R.id.spinnerFrom);
            toSpinner = findViewById(R.id.spinnerTo);

            DropDownMenu adapter = new DropDownMenu(this, android.R.layout.simple_list_item_1, arrayOfCurrencies);
            fromSpinner.setAdapter(adapter);
            toSpinner.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.currency_exchange_configure);


        SharedPreferences saveData = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saveData.edit();

        long lastAPICall = saveData.getLong("lastAPIcallByUser", 0 );
        DataCacher dataCacher = new DataCacher();
        String jsonStr = "";

        if (lastAPICall <= System.currentTimeMillis() - 43200000){ //if API wasn't called within past 12 hours /*12h in miliseconds*/
            //call API

            jsonStr = MainActivity.apiCall();
            lastAPICall = System.currentTimeMillis();
            editor.putLong("lastAPIcallByUser", lastAPICall);
            editor.apply();


            dataCacher.saveFile(this, "conversion.json", jsonStr);
            parseJson(jsonStr);

        } else {
            //use saved/cached data
            if (dataCacher.fileFound(this, "conversion.json")){ // if save data found
                jsonStr = dataCacher.readFile(this, "conversion.json");
                //parse json
                if (!jsonStr.isEmpty()) {
                    parseJson(jsonStr);
                } else {
                    jsonStr = MainActivity.apiCall();
                    if (jsonStr!=null && !jsonStr.isEmpty()) dataCacher.saveFile(this, "conversion.json", jsonStr);
                    parseJson(jsonStr);
                }

            } else {
                //file not found
                jsonStr = MainActivity.apiCall();
                if(dataCacher.saveFile(this, "conversion.json", jsonStr)){
                    //file saved successfully
                } else {
                    System.out.println("File could not be saved");
                }
            }
        }

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setTheCurrencies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setTheCurrencies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        setTheCurrencies();

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent intentB = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, CurrencyExchange.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {mAppWidgetId});
        sendBroadcast(intentB);




        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }


    }


    public void setTheCurrencies(){
        String fromCurrencyStr = fromSpinner.getSelectedItem().toString();
        String toCurrencyStr = toSpinner.getSelectedItem().toString();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fromCurrency", fromCurrencyStr);
        editor.putString("toCurrency", toCurrencyStr);
    }
}

