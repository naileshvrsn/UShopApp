package com.ushop.ushopapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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

            final Product currentProduct = getItem(position);


            TextView productTextView = (TextView) listItemView.findViewById(R.id.product_title);
            productTextView.setText(currentProduct.getName());

            TextView priceTextView = listItemView.findViewById(R.id.product_price);
            priceTextView.setText("$ " + String.valueOf(currentProduct.getUnitPrice()));

            ImageView imageView = listItemView.findViewById(R.id.product_image_view);
            Picasso.get().load(currentProduct.getImageLocation()).into(imageView);

        }
        return listItemView;
    }
}
