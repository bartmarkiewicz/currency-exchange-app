package com.androidprojects.bartek.currencyconverter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CurrencyExchangeConfigureActivity CurrencyExchangeConfigureActivity}
 */
public class CurrencyExchange extends AppWidgetProvider {
    private static final String num0Pressed = "num0";
    private static final String num1Pressed = "num1";
    private static final String num2Pressed = "num2";
    private static final String num3Pressed = "num3";
    private static final String num4Pressed = "num4";
    private static final String num5Pressed = "num5";
    private static final String num6Pressed = "num6";
    private static final String num7Pressed = "num7";
    private static final String num8Pressed = "num8";
    private static final String num9Pressed = "num9";
    private static final String delBtPressed = "delBt";
    private static final String clrBtPressed = "clrBt";
    private static String[] arrayOfCurrencies;
    private static String[] conversionValues;
    RemoteViews views;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_exchange);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    protected PendingIntent createPendingIntent(Context context, String action, int appWidgetId){
        Intent intent = new Intent();
        intent.putExtra("widgetid", appWidgetId);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, appWidgetId, intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void setUpListeners(Context context, int appWidgetId, RemoteViews views){

        PendingIntent delBtpres = null, clrBtPres = null;
        PendingIntent num0pres, num1pres, num2pres,num3pres,num4pres,num5pres,num6pres,num7pres,num8pres,num9pres;

        num0pres = createPendingIntent(context, num0Pressed, appWidgetId);
        num1pres = createPendingIntent(context, num1Pressed, appWidgetId);
        num2pres = createPendingIntent(context, num2Pressed, appWidgetId);
        num3pres = createPendingIntent(context, num3Pressed, appWidgetId);
        num4pres = createPendingIntent(context, num4Pressed, appWidgetId);
        num5pres = createPendingIntent(context, num5Pressed, appWidgetId);
        num6pres = createPendingIntent(context, num6Pressed, appWidgetId);
        num7pres = createPendingIntent(context, num7Pressed, appWidgetId);
        num8pres = createPendingIntent(context, num8Pressed, appWidgetId);
        num9pres = createPendingIntent(context, num9Pressed, appWidgetId);

        delBtpres = createPendingIntent(context, delBtPressed, appWidgetId);
        clrBtPres = createPendingIntent(context, clrBtPressed, appWidgetId);

        views.setOnClickPendingIntent(R.id.num0, num0pres);
        views.setOnClickPendingIntent(R.id.num1, num1pres);
        views.setOnClickPendingIntent(R.id.num2, num2pres);
        views.setOnClickPendingIntent(R.id.num3, num3pres);
        views.setOnClickPendingIntent(R.id.num4, num4pres);
        views.setOnClickPendingIntent(R.id.num5, num5pres);
        views.setOnClickPendingIntent(R.id.num6, num6pres);
        views.setOnClickPendingIntent(R.id.num7, num7pres);
        views.setOnClickPendingIntent(R.id.num8, num8pres);
        views.setOnClickPendingIntent(R.id.num9, num9pres);

        views.setOnClickPendingIntent(R.id.delBt, delBtpres);
        views.setOnClickPendingIntent(R.id.clearBT, clrBtPres);

    }

    public void getArrays(Context context){
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int arrayLength = prefs.getInt("currenciesArrayLength", 0);
        conversionValues = new String[arrayLength];
        arrayOfCurrencies = new String[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            conversionValues[i] = prefs.getString("conversionValues" + i, "");
            arrayOfCurrencies[i] = prefs.getString("arrayOfCurrencies" + i, "");
        }
        SharedPreferences.Editor editor = prefs.edit();
        String fromCurrencyStr = prefs.getString("fromCurrency", "");
        String toCurrencyStr = prefs.getString("toCurrency", "");
        int fromCur = 0;
        int toCur = 0;
        for (int i=0; i < arrayOfCurrencies.length; i++) { // fix this, this presumes MainActivity is alive while the app is clearly not running since this is widget code
            //it should get the array from shared prefs probably, or construct it here
            if (arrayOfCurrencies[i].equals(fromCurrencyStr)){
                fromCur = i;
            }
            if (arrayOfCurrencies[i].equals(toCurrencyStr)){
                toCur = i;
            }
        }
        editor.putInt("fromCurrencyIndex", fromCur);
        editor.putInt("toCurrencyIndex", toCur);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_exchange);
        if (fromCurrencyStr.length() > 3 && toCurrencyStr.length() > 3) {
            String currencyCodeFr = Character.toString(fromCurrencyStr.charAt(1)) + Character.toString(fromCurrencyStr.charAt(2)) + Character.toString(fromCurrencyStr.charAt(3));
            String currencyCodeTo = Character.toString(toCurrencyStr.charAt(1)) + Character.toString(toCurrencyStr.charAt(2)) + Character.toString(toCurrencyStr.charAt(3));
            views.setTextViewText(R.id.fromLabel, currencyCodeFr);
            views.setTextViewText(R.id.currencyTo, currencyCodeTo);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        /*views.setOnClickPendingIntent(R.id.num0, createPendingIntent(context, num0Pressed));
        views.setOnClickPendingIntent(R.id.delBt, createPendingIntent(context, delBtPressed));
        views.setOnClickPendingIntent(R.id.clearBT, createPendingIntent(context, clrBtPressed));*/

        //ComponentName thisWidget = new ComponentName(context, CurrencyExchange.class);
        //appWidgetManager.updateAppWidget(thisWidget, views);




        //views.setTextViewText(R.id.currencyFrom, "Test");
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
            views = new RemoteViews(context.getPackageName(), R.layout.currency_exchange);

            setUpListeners(context, appWidgetId, views);
            getArrays(context);

            ComponentName thisWidget = new ComponentName(context, CurrencyExchange.class);
            appWidgetManager.updateAppWidget(thisWidget, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            CurrencyExchangeConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_exchange);
        if (views == null){
            views = new RemoteViews(context.getPackageName(), R.layout.currency_exchange);
        }


        ComponentName currentWidget = new ComponentName(context, CurrencyExchange.class);
        /*String sharedPrefsStr = currentWidget.toString().replace(" ", ""); // this is used to uniquely identify the widget
        sharedPrefsStr = sharedPrefsStr.replace("/", "");
        sharedPrefsStr = sharedPrefsStr.replace("\\", ""); */
        //String sharedPrefsStr = intent.get;


        int widgetid = intent.getIntExtra("widgetid", 0);

        SharedPreferences saveData = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saveData.edit();

        String currentDisplay = saveData.getString("fromDisplay", "0");
        String newString = currentDisplay;

        if (num0Pressed.equals(intent.getAction())){
            newString = currentDisplay + "0";
        } else if (num1Pressed.equals(intent.getAction())){
            newString = currentDisplay + "1";
        } else if (num2Pressed.equals(intent.getAction())){
            newString = currentDisplay + "2";
        } else if (num3Pressed.equals(intent.getAction())){
            newString = currentDisplay + "3";
        } else if (num4Pressed.equals(intent.getAction())){
            newString = currentDisplay + "4";
        } else if (num5Pressed.equals(intent.getAction())){
            newString = currentDisplay + "5";
        } else if (num6Pressed.equals(intent.getAction())){
            newString = currentDisplay + "6";
        } else if (num7Pressed.equals(intent.getAction())){
            newString = currentDisplay + "7";
        } else if (num8Pressed.equals(intent.getAction())){
            newString = currentDisplay + "8";
        } else if (num9Pressed.equals(intent.getAction())){
            newString = currentDisplay + "9";
        } else if (clrBtPressed.equals(intent.getAction())){
            newString = "";
            views.setTextViewText(R.id.toDisplay, newString);
            editor.putString("fromDisplay", newString);
            editor.putString("toDisplay", newString);
        } else if(delBtPressed.equals(intent.getAction())){
            if (currentDisplay.length()>0) {
                newString = currentDisplay.substring(0, currentDisplay.length() - 1);
            }
        }
        views.setTextViewText(R.id.fromDisplay, newString);
        calculateTheExchange(context, widgetid, views);
        editor.putString("fromDisplay", newString);
        editor.putString("toDisplay", newString);

        editor.commit();
        AppWidgetManager.getInstance(context).updateAppWidget(currentWidget, views);// this updates the widget
        //createPendingIntent(context, AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    }

    public void calculateTheExchange(Context context, int widgetId, RemoteViews views){
        if (conversionValues == null){
            getArrays(context);
        }
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int fromIndex = prefs.getInt("fromCurrencyIndex", 0);
        int toIndex = prefs.getInt("toCurrencyIndex", 0);
        double euroEquivalentFrom = Double.parseDouble(conversionValues[fromIndex]);
        double euroEquivalentTo = Double.parseDouble(conversionValues[toIndex]);
        String fromValueStr = prefs.getString("fromDisplay", "0");
        double fromValue;
        if (!fromValueStr.equals("")) {
            fromValue = Double.parseDouble(fromValueStr);
        } else {
            fromValue = 0;
        }
        Double fromInEuro = fromValue / euroEquivalentFrom;
        Double result = (fromInEuro * euroEquivalentTo);

        views.setTextViewText(R.id.toDisplay, String.format(Locale.getDefault(), "%.2f", result));
    }


    @Override
    public void onEnabled(Context context) {
        //oncreate
        // Enter relevant functionality for when the first widget is created
            SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            int arrayLength = prefs.getInt("currenciesArrayLength", 0);
            conversionValues = new String[arrayLength];
            arrayOfCurrencies = new String[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                conversionValues[i] = prefs.getString("conversionValues" + i, "");
                arrayOfCurrencies[i] = prefs.getString("arrayOfCurrencies" + i, "");
            }

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

