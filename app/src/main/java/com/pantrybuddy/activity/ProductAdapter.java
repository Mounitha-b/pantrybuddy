package com.pantrybuddy.activity;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.icu.lang.UProperty;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pantrybuddy.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private ArrayList<UserProduct> mExampleList;
        public static class ProductViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView mTextView1;
            public TextView mTextView2;
            public com.google.android.material.card.MaterialCardView card;


            public ProductViewHolder(View itemView) {
                super(itemView);
                mTextView1 = itemView.findViewById(R.id.textView);
                mTextView2 = itemView.findViewById(R.id.textView2);
                card = itemView.findViewById(R.id.cardview);
            }
        }
        public ProductAdapter(ArrayList<UserProduct> exampleList) {
            mExampleList = exampleList;
        }
        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_product, parent, false);
            ProductViewHolder evh = new ProductViewHolder(v);
            return evh;
        }



        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            UserProduct currentItem = mExampleList.get(position);
            holder.mTextView1.setText(String.valueOf(currentItem.getProductName()));
            holder.mTextView2.setText(String.valueOf(currentItem.getExpiryDate()));
            Date date = getDate(String.valueOf(currentItem.getExpiryDate()));
            String currentDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Log.d("INFO", "onBindViewHolder: currentdate" + currentDateString);
            Date currentDate = getDate(currentDateString);
           if(currentDate.after(date)){
               Log.d("INFO", "onBindViewHolder: AFter");
               holder.card.setCardBackgroundColor(Color.parseColor("#ED736F"));
           }else{
               if(currentDate.getYear() < date.getYear() || currentDate.getMonth() < date.getMonth()){
                   return;
               }
               Log.d("info", "onBindViewHolder: else");
                if(currentDate.getDay() - date.getDay() <=3 ){
                    holder.card.setCardBackgroundColor(Color.parseColor("#EF865B"));
                }
           }
            Log.d("INFO", "onBindViewHolder: " + String.valueOf(currentItem.getExpiryDate()));

        }

        private Date getDate(String dateString) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                 date = format.parse(dateString);
                System.out.println(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        @Override
        public int getItemCount() {
            return mExampleList.size();
        }
    }

