package com.ushop.ushopapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {


    public ProductAdapter(Activity context, ArrayList<Product> products) {
        super(context, 0, products);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_product_list_item, parent, false);

            Product currentProduct = getItem(position);



            TextView productTextView = (TextView) listItemView.findViewById(R.id.product_title);
            productTextView.setText(currentProduct.getName());

            TextView priceTextView = listItemView.findViewById(R.id.product_price);
            priceTextView.setText(String.valueOf(currentProduct.getUnitPrice()));


        }
        return listItemView;
    }
}
