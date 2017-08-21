package com.mohsin.contactdemo.EditContact.Presenter;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

import com.google.gson.Gson;
import com.mohsin.contactdemo.Base.BasePresenter;
import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;
import com.mohsin.contactdemo.EditContact.View.EditView;
import com.mohsin.contactdemo.Utility.Constants;
import com.mohsin.contactdemo.Utility.Pref_Data;
import com.mohsin.contactdemo.Utility.SharedPref;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.mohsin.contactdemo.Utility.Constants.contact_list;

/**
 * Created by mohsin on 12/8/17.
 */

public class EditContactPresenter extends BasePresenter<EditView.View> implements EditContactInterface {

    private android.content.Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Gson gson;
    EditContactInterface mListner;
    Activity activity = (Activity) context;

    public EditContactPresenter(Activity activity, EditContactInterface mListner, Context context) {
        this.activity = activity;
        this.context = context;
        this.mListner = mListner;
        pref = context.getSharedPreferences(Pref_Data.HISTORY_DATA, MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void editContact(String id) {
        getView().showProgress();
        for (int i = 0; i < contact_list.size(); i++) {
            if (contact_list.get(i).getContactID().equalsIgnoreCase(id)) {
                getView().hideProgress();
                mListner.editContact(contact_list.get(i));
            }
            getView().hideProgress();
        }
    }

    public void update(String name, String num, String email, String ContactId, String home_number, String company) {
        boolean success = true;
//        String phnumexp = "^[0-9]*$";

        try {
            name = name.trim();
            email = email.trim();
            num = num.trim();
            home_number = home_number.trim();
            company = company.trim();

            if (name.equals("") && num.equals("") && email.equals("")) {
                success = false;
            } else {
                ContentResolver contentResolver = context.getContentResolver();

                String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

                String[] emailParams = new String[]{ContactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                String[] nameParams = new String[]{ContactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                String[] numberParams = new String[]{ContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
                String[] companyParams = new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

                ArrayList<ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();

                if (!email.equals("")) {
                    ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, emailParams)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                            .build());
                }

                if (!name.equals("")) {
                    ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, nameParams)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                            .build());
                }

                if (!num.equals("")) {

                    ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, numberParams)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, num)
                            .build());
                }
                if (!home_number.equals("")) {

                }
                if (Constants.contact_list.get(0).getCompany() == null) {

                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                            .build());
                    context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

                } else {
                    ops.add(ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, companyParams)
                            .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                            .build());
                }
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);

                ContactVO contactVO = new ContactVO();
                contactVO.setContactName(name);
                contactVO.setContactNumber(num);
                contactVO.setContactEmail(email);
                contactVO.setContactID(ContactId);
                contactVO.setStatus("Updated");


                Constants.StroreContactList.add(contactVO);

                ArrayList<ContactVO> contactVOs = new ArrayList<>();
                ContactVO con = new ContactVO();
                SharedPref tinyDB = new SharedPref(context);
                contactVOs.addAll(tinyDB.getListObject(Pref_Data.HISTORY_DATA, con));


                if (contactVOs.size() > 0) {
                    contactVOs.addAll(Constants.StroreContactList);
                    tinyDB.putListObject(Pref_Data.HISTORY_DATA, contactVOs);
                } else {
                    tinyDB.putListObject(Pref_Data.HISTORY_DATA, Constants.StroreContactList);
                }
                mListner.updateContact(name, num, email, ContactId);


            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

    }


    @Override
    public void editContact(ContactVO contactVO) {

    }

    @Override
    public void updateContact(String name, String num, String email, String contactId) {

    }


}
