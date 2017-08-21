package com.mohsin.contactdemo.History.Presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mohsin.contactdemo.Base.BasePresenter;
import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;
import com.mohsin.contactdemo.History.View.HistoryView;
import com.mohsin.contactdemo.Utility.Pref_Data;
import com.mohsin.contactdemo.Utility.SharedPref;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mohsin on 14/8/17.
 */

public class HistoryPresenter extends BasePresenter<HistoryView.View> implements HistoryPresenterInterface {

    private Context context;
    HistoryPresenterInterface mListener;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Gson gson;

    private final HistoryPresenterInterface historyView;


    public HistoryPresenter(Context context, HistoryPresenterInterface mListener, HistoryPresenterInterface historyView) {
        this.context = context;
        this.mListener = mListener;
        this.historyView = historyView;
        pref = context.getSharedPreferences(Pref_Data.HISTORY_DATA, MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();

    }

    public void getHistroyData() {
        ArrayList<ContactVO> contactVOs = new ArrayList<>();
        ContactVO con = new ContactVO();
        SharedPref tinyDB = new SharedPref(context);
        contactVOs.addAll(tinyDB.getListObject(Pref_Data.HISTORY_DATA, con));


//        if (Constants.contact_list.size() > 0) {
        mListener.getAllStoredContacts(contactVOs);
//        }

    }


    @Override
    public void getAllStoredContacts(ArrayList<ContactVO> contactVOList) {

    }

    @Override
    public void openDialog() {

    }
}
