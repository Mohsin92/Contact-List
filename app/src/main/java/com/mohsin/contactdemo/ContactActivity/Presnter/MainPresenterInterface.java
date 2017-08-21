package com.mohsin.contactdemo.ContactActivity.Presnter;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.mohsin.contactdemo.ContactActivity.Adapter.AllContactsAdapter;
import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;

import java.util.ArrayList;

/**
 * Created by mohsin on 12/8/17.
 */

public interface MainPresenterInterface {
    void getMarshmallowPermission();
    void getallcontacts(ArrayList<ContactVO> contactVOList);
    void openDialog();
    void attachToRecyclerView(ItemTouchHelper.SimpleCallback simpleItemTouchCallback);
    void initSwipe(int position, Context context, AllContactsAdapter allContactsAdapter);

}
