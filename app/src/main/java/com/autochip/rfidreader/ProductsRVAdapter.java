package com.autochip.rfidreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*
 * Created by Vj on 15-OCT-18.
 */

class ProductsRVAdapter extends RecyclerView.Adapter<ProductsRVAdapter.ProductsHolder> {

    //private OnFragmentInteractionListener mListener;
    private Context context;
    //private FragmentManager supportFragmentManager;
    private ArrayList<String> alProducts;
    private ArrayList<Integer> alQuantity;


    ProductsRVAdapter(Context context, ArrayList<String> alProducts, ArrayList<Integer> alQuantity) {
        this.context = context;
        this.alProducts = alProducts;
        this.alQuantity = alQuantity;
    }

    @NonNull
    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_products, parent, false);

        return new ProductsRVAdapter.ProductsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsRVAdapter.ProductsHolder holder, int position) {
        holder.tvProducts.setText(alProducts.get(position));

        holder.tvQuantity.setText(String.valueOf(alQuantity.get(position)));

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment;
                FragmentTransaction transaction;
                String sBackStackParent;
                newFragment = new ViewOrderFragment();
                //newFragment.setArguments(bundle);
                sBackStackParent = newFragment.getClass().getName();
                transaction = supportFragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.t2b, R.anim.b2t);
                transaction.replace(R.id.fl_container, newFragment, sBackStackParent);
                transaction.addToBackStack(null);
                transaction.commit();
                HomeScreenActivity.onFragmentInteractionListener.onFragmentMessage("MY_ORDER_ITEM_CLICK", 6);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return alProducts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ProductsHolder extends RecyclerView.ViewHolder{
        /*ImageView mImageView;
        TextView tvProductsReview;*/
        TextView tvProducts, tvQuantity;

        ProductsHolder(View itemView) {
            super(itemView);
            tvProducts = itemView.findViewById(R.id.tv_product);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            /*tvProductsReview = (TextView) itemView.findViewById(R.id.tv_products_review);
            mImageView = (ImageView) itemView.findViewById(R.id.products_rv_image_view);*/
        }
    }
}
