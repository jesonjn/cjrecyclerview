/*
 * Copyright (C) 2015 Paul Burke
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

package com.nqsky.nest.home.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.jeson.cj.view.adapter.RecyclerListAdapter;
import com.jeson.cj.view.helper.OnStartDragListener;
import com.jeson.cj.view.helper.CJTouchHelperCallback;
import com.jeson.cj.view.model.ItemBean;
import com.nqsky.rmad.R;

import java.util.ArrayList;
import java.util.List;


/**
 *  用法实例待修改
 * @author jeson
 */
public class RecyclerGridFragment extends Fragment implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;
    private List<ItemBean>  mList=new ArrayList<>();
    public RecyclerGridFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          View view=inflater.inflate(R.layout.home_refalout,container,false);
        return  view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerListAdapter adapter = new RecyclerListAdapter(getActivity(), this){
            View view;
            @Override
            public ItemViewHolder onCreatViewFristVHoleder(ViewGroup parent, int viewType) {
                view = new WebView(getActivity());
                view.setId(R.id.webview);
                GridLayoutManager.LayoutParams  params=new GridLayoutManager.LayoutParams(GridLayoutManager.LayoutParams.MATCH_PARENT,800);
                view.setLayoutParams(params);
                return new AppItemViewHolder(view,viewType);
            }

            @Override
            public ItemViewHolder onCreatViewItemVHoleder(ViewGroup parent, int viewType) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, null, false);
                return new AppItemViewHolder(view,viewType);
            }

            @Override
            public ItemViewHolder onCreatViewHeaderVHoleder(ViewGroup parent, int viewType) {


                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
                return new AppItemViewHolder(view,viewType);
            }

            @Override
            public void onBindViewFristHolder(ItemViewHolder holder, int position) {
                if(holder  instanceof  AppItemViewHolder){
                    ((AppItemViewHolder) holder).webview.loadUrl("http://www.qq.com");//.setText(super.mItems.get(position).getText());
                }


            }

            @Override
            public void onBindViewItemHolder(ItemViewHolder holder, int position) {
                if(holder  instanceof  AppItemViewHolder){
                    ((AppItemViewHolder) holder).textView.setText(super.mItems.get(position).getText());
                    ((AppItemViewHolder) holder).handleView.setImageBitmap( BitmapFactory.decodeResource(getResources(),R.drawable.ic_app));

                }

            }

            @Override
            public void onBindViewHeaderHolder(ItemViewHolder holder, int position) {
                if(holder  instanceof  AppItemViewHolder){
                    ((AppItemViewHolder) holder).textView.setText(super.mItems.get(position).getText());
                }

            }
        };

        RecyclerView recyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        final int spanCount = getResources().getInteger(R.integer.grid_columns);
        final GridLayoutManager layoutManager =new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)){
                    case RecyclerListAdapter.ITEM_VIEW_TYPE_CUSTOMER:
                        return 4;
                    case  RecyclerListAdapter.ITEM_VIEW_TYPE_HEADER:
                        return 4;
                    case  RecyclerListAdapter.ITEM_VIEW_TYPE_ITEM:
                        return 1;
                    default:
                        return 4;
                }
            }
        });

        ItemTouchHelper.Callback callback= new CJTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RecyclerGridFragment.this.getActivity(), "当前点击了第"+position+"项了", Toast.LENGTH_SHORT).show();
                if(adapter.getItemViewType(position)==RecyclerListAdapter.ITEM_VIEW_TYPE_ITEM) {
                    try {
                        ItemBean bean = new ItemBean();
                        bean.setText("当前点击了第" + position + "项了");
                        bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_ITEM);
                        bean.setId(position / 10 + "");
                        bean.setpID(adapter.getItemBean(position).getpID());
                        adapter.addItem(adapter.getItemBean(position).getpID(), bean);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }else{
                    adapter.delItem(position+1);

                }
            }
        });
        ItemBean bean;
        List<ItemBean>  list=new ArrayList<ItemBean>();

        for(int i=0;i<80;i++){
            bean=new ItemBean();
            if(i%10==0) {
                bean.setText("标题" + i/10);
                bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_HEADER);
                bean.setId(String.valueOf((i/10)));
            }else{
                bean.setText("Item" +i/10+ i%10);
                bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_ITEM);
                bean.setpID(String.valueOf((i/10)));
                bean.setId(String.valueOf((i)));
            }

            list.add(bean);
        }
        bean=new ItemBean();
        bean.setText("标题" );
        bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_CUSTOMER);
        bean.setId(String.valueOf((10)));
        list.add(0,bean);
        adapter.addItems(list);
        final com.cjj.MaterialRefreshLayout swipeRefreshLayout;
        final Handler  handler=new Handler();

        swipeRefreshLayout= (com.cjj.MaterialRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setWaveColor(0xf90fffff);
        swipeRefreshLayout.setIsOverLay(true);
        swipeRefreshLayout.setWaveShow(true);
        swipeRefreshLayout.setLoadMore(true);
        swipeRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener(){

            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        ItemBean bean;
                        List<ItemBean>  list=new ArrayList<ItemBean>();

                        for(int i=0;i<80;i++){
                            bean=new ItemBean();
                            if(i%10==0) {
                                bean.setText("刷新" + i/10);
                                bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_HEADER);
                                bean.setId(String.valueOf((i/10)));
                            }else{
                                bean.setText("刷新" +i/10+ i%10);
                                bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_ITEM);
                                bean.setpID(String.valueOf((i/10)));
                                bean.setId(String.valueOf((i)));
                            }

                            list.add(bean);
                        }
                        bean=new ItemBean();
                        bean.setText("刷新" );
                        bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_CUSTOMER);
                        bean.setId(String.valueOf((10)));
                        list.add(0,bean);
                        adapter.addItems(list);
                        materialRefreshLayout.finishRefresh();
                    }
                },1000);


            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList  list=new ArrayList<ItemBean>();

                        for(int i=60;i<90;i++){
                            ItemBean   bean=new ItemBean();
                            if(i%10==0) {
                                bean.setText("加载更多" + i/10);
                                bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_HEADER);
                                bean.setId(String.valueOf((i/10)));
                            }else{
                                bean.setText("加载更多" +i/10+ i%10);
                                bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_ITEM);
                                bean.setpID(String.valueOf((i/10)));
                                bean.setId(String.valueOf((i)));
                            }

                            list.add(bean);
                        }
//                        bean=new ItemBean();
//                        bean.setText("加载更多" );
//                        bean.setType(RecyclerListAdapter.ITEM_VIEW_TYPE_CUSTOMER);
//                        bean.setId(String.valueOf((10)));
//                        list.add(0,bean);
                        adapter.addItems(list);
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                }, 1000);
            }
        });

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public static class AppItemViewHolder  extends RecyclerListAdapter.ItemViewHolder
    {
        public TextView textView;
        public WebView  webview;
        public AppItemViewHolder(final View itemView, int type){
            super(itemView,type);
            if(type==RecyclerListAdapter.ITEM_VIEW_TYPE_HEADER) {
                textView = (TextView) itemView.findViewById(R.id.text);
            }else if(type==RecyclerListAdapter.ITEM_VIEW_TYPE_ITEM){
//                handleView = (ImageView) itemView.findViewById(R.id.rec_image);
//                Log.e("tag","----handl----"+handleView);
                textView = (TextView) itemView.findViewById(R.id.text);

            }else  if(type==RecyclerListAdapter.ITEM_VIEW_TYPE_CUSTOMER){
                webview=(WebView)itemView.findViewById(R.id.webview);
            }
        }
    }
}
