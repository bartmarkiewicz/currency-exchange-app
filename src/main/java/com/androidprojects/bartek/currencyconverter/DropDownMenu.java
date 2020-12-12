package com.androidprojects.bartek.currencyconverter;

import android.content.Context;
import android.widget.ArrayAdapter;

public class DropDownMenu extends ArrayAdapter<String> {

    public DropDownMenu(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }
}
