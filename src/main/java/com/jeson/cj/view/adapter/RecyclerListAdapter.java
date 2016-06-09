/*
 * Copyright (C) 2016 jeson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jeson.cj.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.jeson.cj.view.helper.ItemTouchHelperAdapter;
import com.jeson.cj.view.helper.ItemTouchHelperViewHolder;
import com.jeson.cj.view.helper.OnStartDragListener;
import com.jeson.cj.view.model.ItemBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author jeson
 */
public abstract   class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    public static final int ITEM_VIEW_TYPE_HEADER = 0;
    public static final int ITEM_VIEW_TYPE_ITEM = 1;
    public static  final  int ITEM_VIEW_TYPE_CUSTOMER=2;
    private static final  int ITEM_VIEW_TYPE_FOOTS=3;
    protected     LinkedList<ItemBean> mItems = new LinkedList<ItemBean>();
    private  Map<String,List<ItemBean>>  mMap=new HashMap<>();
    private  boolean  itemScoll=true;
    private  boolean  pSwap=false;

    private final OnStartDragListener mDragStartListener;
    public static AdapterView.OnItemClickListener mOnClickListener;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

    }
    public void  addItems(List<ItemBean>  items){
        int i=mItems.size();
        mItems.addAll(items);
        for(ItemBean bean:items){
            bean.setSec(i++);
            if(bean.getType()==ITEM_VIEW_TYPE_ITEM) {
                List<ItemBean>  list;
                list=mMap.get(bean.getpID());

                if(list==null){
                    list=new ArrayList<>();
                }
                list.add(bean);
                mMap.put(bean.getpID(),list);
            }
        }
        notifyDataSetChanged();
    }
    public  void addItem(String pid,ItemBean  bean)throws NullPointerException{
        if(!TextUtils.isEmpty(pid)) {
            Integer i = mMap.get(pid).get(mMap.get(pid).size() - 1).getSec();
            if (i < mItems.size()) {
                mMap.get(pid).add(bean);
                bean.setSec(i + 1);
                mItems.add(i + 1, bean);
                for (int j = i + 2; j < mItems.size(); j++) {

                    mItems.get(j).setSec((mItems.get(j).getSec() + 1));

                }
                notifyDataSetChanged();
            }else{
                throw new NullPointerException();
            }
        }else {
            throw new NullPointerException();
        }

    }
    public  void  delItem(int position){
     ItemBean  bean=   mItems.get(position);
        mMap.get(bean.getpID()).remove(bean);

        if (position < mItems.size()) {
            bean.setSec(position - 1);
            for (int j = position+1; j < mItems.size(); j++) {

                mItems.get(j).setSec((mItems.get(j).getSec() -1));

            }
        }
        mItems.remove(position);
        notifyItemRemoved(position);
    }
    public  void  clear(){
        mMap.clear();
        mItems.clear();
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener  onClickListener){
        mOnClickListener=onClickListener;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ItemViewHolder  itemViewHolder;
        if(viewType==ITEM_VIEW_TYPE_HEADER){
            return onCreatViewHeaderVHoleder(parent,viewType);
        }else if(viewType==ITEM_VIEW_TYPE_ITEM) {
            return onCreatViewItemVHoleder(parent,viewType);
        }else if(viewType== ITEM_VIEW_TYPE_CUSTOMER){
            return onCreatViewFristVHoleder(parent,viewType);
        }else{
             view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.list_content, parent, false);
             itemViewHolder = new ItemViewHolder(view,viewType);
            return itemViewHolder;
        }
    }
    public abstract  ItemViewHolder   onCreatViewFristVHoleder(ViewGroup parent, int viewType);
    public abstract  ItemViewHolder   onCreatViewItemVHoleder(ViewGroup parent, int viewType);
    public abstract  ItemViewHolder   onCreatViewHeaderVHoleder(ViewGroup parent, int viewType);
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
//        holder.textView.setText(mItems.get(position).getText());
        holder.setPostion(position);
        if(getItemViewType(position)==ITEM_VIEW_TYPE_HEADER){
            onBindViewHeaderHolder(holder,position);
        }else if(getItemViewType(position)==ITEM_VIEW_TYPE_ITEM) {
            holder.handleView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    mDragStartListener.onStartDrag(holder);
                    return true;
                }
            });
            onBindViewItemHolder(holder,position);
        }else if(getItemViewType(position)== ITEM_VIEW_TYPE_CUSTOMER){
            onBindViewFristHolder( holder,  position);
        }
    }
    public abstract void onBindViewFristHolder(ItemViewHolder holder, int position);
    public abstract void onBindViewItemHolder(ItemViewHolder holder, int position);
    public abstract void onBindViewHeaderHolder(ItemViewHolder holder, int position);

   @Override
   public int getItemViewType(int position) {
       return mItems.get(position).getType();
   }
    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition<0||fromPosition>mItems.size())
            return false;
        if(toPosition<0||toPosition>mItems.size())
            return false;
        if(mItems.get(fromPosition).getType()==ITEM_VIEW_TYPE_HEADER){
            if(pSwap) {
                ItemBean bean = mItems.get(fromPosition);
                List<ItemBean> list = mMap.get(bean.getId());
                mItems.remove(fromPosition);
                if (fromPosition > toPosition) {
                    if (list != null) {
                        mItems.removeAll(list);
                        mItems.add(toPosition, bean);
                        mItems.addAll(toPosition + 1, list);
                    }
                } else {
                    if (list != null) {
                        mItems.removeAll(list);
                        mItems.add(toPosition, bean);
                        mItems.addAll(toPosition + 1, list);
                    }
                }
                notifyDataSetChanged();
                return true;
            }else{
                return false;
            }
        }
        if(mItems.get(fromPosition).getpID().equals(mItems.get(toPosition).getpID())) {
            Collections.swap(mItems, fromPosition, toPosition);
            int i= mItems.get(toPosition).getSec();
            mItems.get(fromPosition).setSec(i);
            mItems.get(toPosition).setSec(mItems.get(fromPosition).getSec());
            Collections.sort(mMap.get(mItems.get(toPosition).getpID()));
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }else{
            if(itemScoll){
                return  false;
            }
            if(fromPosition==toPosition)
                return false;
            ItemBean bean=  mItems.get(fromPosition);
             bean.setpID(mItems.get(toPosition).getpID());
             mItems.add(toPosition,bean);
            int i= mItems.get(toPosition).getSec();
            mItems.get(fromPosition).setSec(i);
            mItems.get(toPosition).setSec(mItems.get(fromPosition).getSec());
            Collections.sort(mMap.get(mItems.get(toPosition).getpID()));
            Collections.sort(mMap.get(mItems.get(fromPosition).getpID()));
            mMap.get(bean.getpID()).remove(bean);
            mMap.get(mItems.get(toPosition).getpID()).add(bean);
            notifyItemInserted(toPosition);
            if(fromPosition>toPosition) {
                mItems.remove(fromPosition + 1);
                notifyItemRemoved(fromPosition+1);
            }else{
                mItems.remove(fromPosition );
                notifyItemRemoved(fromPosition);
            }


            return true;
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ItemBean  getItemBean(int position){
        return  mItems.get(position);
    }



    /**
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

//        public  TextView textView;
        public  ImageView handleView;
        public  int  postion;

        public ItemViewHolder(final View itemView, int type) {
            super(itemView);
            if(type==ITEM_VIEW_TYPE_ITEM) {
//                textView = (TextView) itemView.findViewById(R.id.text);
//            }else{
                handleView = (ImageView) itemView.findViewById(android.R.id.icon2);
//                textView = (TextView) itemView.findViewById(R.id.text);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnClickListener!=null){
                        mOnClickListener.onItemClick(null,itemView,postion,0l);
                    }
                }
            });

        }


       public void  setPostion(int pos){
           postion=pos;
       }
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
