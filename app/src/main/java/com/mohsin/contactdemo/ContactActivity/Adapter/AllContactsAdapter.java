package com.mohsin.contactdemo.ContactActivity.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;
import com.mohsin.contactdemo.EditContact.EditContact;
import com.mohsin.contactdemo.R;
import com.mohsin.contactdemo.Utility.Constants;

import java.util.ArrayList;

/**
 * Created by mohsin on 12/8/17.
 */
public class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<ContactVO> contactVOList = new ArrayList<ContactVO>();
    Context mContext;
    Boolean flag;
    Activity activity = (Activity) mContext;


    public AllContactsAdapter(Activity activity,Context mContext, ArrayList<ContactVO> contactVOList, Boolean flag) {
        this.activity=activity;
        this.contactVOList = contactVOList;
        this.mContext = mContext;
        this.flag = flag;
    }

    @Override
    public AllContactsAdapter.ContactViewHolder onCreateViewHolder(ViewGroup ViewGroup, int viewType) {
        View view = LayoutInflater.from(ViewGroup.getContext()).inflate(R.layout.single_contact_view, ViewGroup, false);
        return new ContactViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AllContactsAdapter.ContactViewHolder holder, int position) {
        ContactVO contactVO = contactVOList.get(position);
        holder.tvContactName.setText(contactVO.getContactName());
        holder.tvPhoneNumber.setText(contactVO.getContactNumber());
        if (flag) {
            holder.tvStatus.setText(contactVO.getStatus());
        }


        holder.setClickListener(new Recycle_item_interface() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!flag) {
                    Intent intent = new Intent(mContext, EditContact.class);
                    intent.putExtra("json", contactVOList.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }


            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public void removeItem(int position,String contact_id) {
        flag=true;
        Constants.contact_list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,  Constants.contact_list.size());
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber, tvStatus;
        private Recycle_item_interface clickListener;

        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);

        }

        public void setClickListener(Recycle_item_interface itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }


}
