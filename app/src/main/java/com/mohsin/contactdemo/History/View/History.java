package com.mohsin.contactdemo.History.View;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;


import com.mohsin.contactdemo.Base.BaseActivity;
import com.mohsin.contactdemo.ContactActivity.Adapter.AllContactsAdapter;
import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;
import com.mohsin.contactdemo.History.Presenter.HistoryPresenter;
import com.mohsin.contactdemo.History.Presenter.HistoryPresenterInterface;
import com.mohsin.contactdemo.R;


import java.util.ArrayList;

public class History extends BaseActivity implements HistoryPresenterInterface, HistoryView.View {

    public static RecyclerView rv_history;
    HistoryPresenter historyPresenter;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyPresenter = new HistoryPresenter(this, this, this);
        historyPresenter.attachView(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        historyPresenter.getHistroyData();

    }

    @Override
    public void Init() {
        setContentView(R.layout.activity_history);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        setTitle("History");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("??");
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
        rv_history = (RecyclerView) findViewById(R.id.rv_history);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(History.this);
        rv_history.setLayoutManager(layoutManager);
    }


    @Override
    public void getAllStoredContacts(ArrayList<ContactVO> contactVOList) {
        if(contactVOList.size()>0)
        {
            AllContactsAdapter contactAdapter = new AllContactsAdapter(this,getApplicationContext(), contactVOList, true);
            rv_history.setLayoutManager(new LinearLayoutManager(this));
            rv_history.setAdapter(contactAdapter);
        }else
        {
            Toast.makeText(History.this,"No History Found!",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void openDialog() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
