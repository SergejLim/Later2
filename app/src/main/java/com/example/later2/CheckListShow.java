package com.example.later2;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Scanner;

public class CheckListShow {
    String FId;
    Context outContext;
    SwipeRefreshLayout swipeRefreshLayout;
    ScrollView scrollView;

    public CheckListShow(Context context, String fid, SwipeRefreshLayout innerSwipeRefreshLayout, ScrollView innerScrollView) {
        FId =fid;
        outContext = context;
        swipeRefreshLayout = innerSwipeRefreshLayout;
        scrollView = innerScrollView;
        createList();
    }

    private void createList(){
        CheckListDataHelper helper = new CheckListDataHelper(outContext);
        List<CheckListData> data = helper.getWithFId(FId);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (data.size()==0){
            ImageButton imageButton = new ImageButton(outContext);
            imageButton.setBackgroundResource(R.drawable.empty_list);
            scrollView.addView(imageButton);
        }
        else{

        }
    }

}
