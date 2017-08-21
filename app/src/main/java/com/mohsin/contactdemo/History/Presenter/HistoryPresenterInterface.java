package com.mohsin.contactdemo.History.Presenter;



import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;

import java.util.ArrayList;

/**
 * Created by mohsin on 14/8/17.
 */

public interface HistoryPresenterInterface {

    void getAllStoredContacts(ArrayList<ContactVO> contactVOList);

    void openDialog();

}
