package com.pantrybuddy.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pantrybuddy.R;
import com.pantrybuddy.stubs.GlobalClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<UserProduct> mExampleList;
        private Context context;
        private static AnimatorSet leftIn, leftOut, rightIn, rightOut;



    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;

        public TextView mTextView8;
        public TextView mTextView9;
        public TextView mTextView10;
        public TextView mTextView11;
        public TextView mTextView12;
        public TextView mTextView13;
        public String Manufacturer;
        public String Ingredients;
        public String servingSize;
        public String title;
        View itemView;


        public ImageView imageView;
        public View view ;
        public com.google.android.material.card.MaterialCardView card;
        public com.google.android.material.card.MaterialCardView card_back;
        public TextView expiredLabel;
        public ConstraintLayout layout;
        public ConstraintLayout layout_back;
        private PopupWindow popupWindow;




        public ProductViewHolder(Context context, View itemView) {

                super(itemView);
                popupWindow = new PopupWindow(context);

                this.itemView = itemView;
                mTextView1 = itemView.findViewById(R.id.textView);
                mTextView2 = itemView.findViewById(R.id.textView2);
                mTextView3 = itemView.findViewById(R.id.textView6);
                expiredLabel = itemView.findViewById(R.id.textView7);
                imageView = itemView.findViewById(R.id.imageView5);
                layout = itemView.findViewById(R.id.constraintLayout);

                mTextView8 = itemView.findViewById(R.id.textView8);
                mTextView9 = itemView.findViewById(R.id.textView9);
                mTextView10 = itemView.findViewById(R.id.textView10);
                mTextView11 = itemView.findViewById(R.id.textView11);
                mTextView12= itemView.findViewById(R.id.textView12);
                mTextView13= itemView.findViewById(R.id.textView13);

                card = itemView.findViewById(R.id.cardview);
                view = itemView.findViewById(R.id.view5);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, PopUpActivity.class);
                        intent.putExtra("Manufacturer", Manufacturer);
                        intent.putExtra("Ingredients", Ingredients);
                        intent.putExtra("servingSize", servingSize);
                        intent.putExtra("title", title);

                        context.startActivity(intent);
                    }
                });
            }
        }
        public ProductAdapter(Context context, ArrayList<UserProduct> exampleList) {
            mExampleList = exampleList;
            this.context = context;
        }
        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_product, parent, false);
            ProductViewHolder evh = new ProductViewHolder(context, v);
            return evh;
        }


        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {

            UserProduct currentItem = mExampleList.get(position);
            holder.Manufacturer = currentItem.getManufacturer();
            holder.Ingredients = currentItem.getIngredients();
            holder.servingSize = currentItem.getServingSize();
            holder.title = currentItem.getProductName();
            holder.mTextView1.setText(String.valueOf(currentItem.getProductName()));
            holder.mTextView2.setText(String.valueOf(currentItem.getExpiryDate()));
            holder.mTextView3.setText("Count : " + String.valueOf(currentItem.getCount()));

            GlobalClass c  = new GlobalClass();
            String name = String.valueOf(currentItem.getImage());

            holder.view.setBackground(c.getDrawable(name));
            Date date = getDate(String.valueOf(currentItem.getExpiryDate()));
            String currentDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Log.d("INFO", "onBindViewHolder: currentdate" + currentDateString);
            Date currentDate = getDate(currentDateString);


               if(currentDate.getYear() < date.getYear()){
                   return;
               }
            Log.d(TAG, "onBindViewHolder: curr : " + currentDate.getMonth() + ",exp: " +  date.getMonth());
               int difference = date.getDate() - currentDate.getDate();
            Log.d(TAG, "onBindViewHolder: diff is " + difference);
            holder.expiredLabel.setTextColor(Color.parseColor("green"));
            holder.expiredLabel.setText("Safe");
               if(currentDate.getYear() > date.getYear() ){
                   holder.expiredLabel.setTextColor(Color.parseColor("red"));
                   holder.expiredLabel.setText("Expired");
               }else if(currentDate.getYear() == date.getYear()){
                   if(currentDate.getMonth() < date.getMonth()){
                       return;
                   }
                   if(currentDate.getMonth() > date.getMonth() || (currentDate.getMonth() ==  date.getMonth() && difference<=0)){
                       holder.expiredLabel.setTextColor(Color.parseColor("red"));
                       holder.expiredLabel.setText("Expired");
                   }else if(difference <= 3){
                       holder.expiredLabel.setTextColor(Color.parseColor("orange"));
                       holder.expiredLabel.setText("About to Expire");
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

