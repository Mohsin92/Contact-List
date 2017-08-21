package com.mohsin.contactdemo.EditContact.Presenter;

import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;

/**
 * Created by mohsin on 12/8/17.
 */

public interface EditContactInterface {

    void updateContact(String name,String num,String email,String contactId);
    void editContact(ContactVO contactVO);
}
