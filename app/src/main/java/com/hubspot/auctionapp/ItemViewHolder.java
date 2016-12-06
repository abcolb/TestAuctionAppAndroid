package com.hubspot.auctionapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.google.firebase.quickstart.database.R;
import com.hubspot.auctionapp.models.Item;
import com.squareup.picasso.Picasso;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public View mItemView;
    public TextView nameView;
    public TextView donornameView;
    public ImageView imageView;
    public TextView numAvailableView;
    public TextView descriptionView;
    public TextView detailView;

    public ItemViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        nameView = (TextView) itemView.findViewById(R.id.item_title);
        donornameView = (TextView) itemView.findViewById(R.id.item_donorname);
        imageView = (ImageView) itemView.findViewById(R.id.item_thumbnail);
        numAvailableView = (TextView) itemView.findViewById(R.id.item_num_available);
        descriptionView = (TextView) itemView.findViewById(R.id.item_description);
        detailView = (TextView) itemView.findViewById(R.id.item_detail);
    }

    public void bindToItem(Item item, View.OnClickListener onClickListener) {
        nameView.setText(item.getName());
        donornameView.setText(item.getDonorname());
        Picasso.with(mItemView.getContext())
            .load(item.getImageurl())
            .placeholder(R.drawable.ic_item_image)
            .error(R.drawable.ic_item_image)
            .into(imageView);
        numAvailableView.setText(String.valueOf(item.getQty()) + " Available");
        descriptionView.setText(item.getDescription());

        //get bids once
        //get bid status
        detailView.setText(this.getBidStatus(item));
        //View.setOnClickListener(onClickListener);
    }

    public String getBidStatus(Item item) {
        if (!item.getIsBiddingOpen()) {
            return "";
        } else if (false) {
            return "OUTBID!";
        } else if (false) {
            return "WINNING";
        } else {
            return "BID NOW";
        }
    }
}
