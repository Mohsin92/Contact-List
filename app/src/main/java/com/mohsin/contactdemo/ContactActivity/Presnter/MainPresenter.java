package com.mohsin.contactdemo.ContactActivity.Presnter;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mohsin.contactdemo.Base.BasePresenter;
import com.mohsin.contactdemo.ContactActivity.Adapter.AllContactsAdapter;
import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;
import com.mohsin.contactdemo.ContactActivity.View.MainView;
import com.mohsin.contactdemo.ContactActivity.View.MainViewInterface;
import com.mohsin.contactdemo.R;
import com.mohsin.contactdemo.Utility.Constants;
import com.mohsin.contactdemo.Utility.Pref_Data;
import com.mohsin.contactdemo.Utility.SharedPref;

import java.util.ArrayList;

/**
 * Created by mohsin on 12/8/17.
 */

public class MainPresenter extends BasePresenter<MainView.View> implements MainPresenterInterface {

    private Context context;


    private final MainViewInterface mainView;

    MainPresenterInterface mListener;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    EditText con_num, con_name, con_mail;
    private Paint p = new Paint();
    String contact_id;


    public MainPresenter(MainPresenterInterface listener, MainViewInterface mainView, Context context) {

        this.mListener = listener;
        this.context = context;
        this.mainView = mainView;
    }


    public void setMarshmallowPermission() {
        mListener.getMarshmallowPermission();
    }



    public void getContacts() {
        getView().showProgress();
        ArrayList<ContactVO> contactVOList = new ArrayList<ContactVO>();
        ContactVO contactVO;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {


                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new ContactVO();
                    contactVO.setContactID(id);
                    contactVO.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactVO.setContactNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        contactVO.setContactEmail(emailId);
                    }
                    emailCursor.close();

                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor companyCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);


                    if (companyCursor.getCount() > 0) {
                        while (companyCursor.moveToNext()) {
                            String company = companyCursor.getString(companyCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                            contactVO.setCompany(company);
                        }
                    }

                    companyCursor.close();
                    contactVOList.add(contactVO);
                }
            }
            getView().hideProgress();
            mListener.getallcontacts(contactVOList);

        }
    }


    @Override
    public void getallcontacts(ArrayList<ContactVO> contactVOList) {

    }


    @Override
    public void openDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.add_contact_dialog, null);
        AlertDialog.Builder contact_dialog = new AlertDialog.Builder(context);
        contact_dialog.setTitle("ADD CONTACTS");
        contact_dialog.setView(mView);


        con_num = (EditText) mView.findViewById(R.id.ed_contact_num);
        con_name = (EditText) mView.findViewById(R.id.ed_name);
        con_mail = (EditText) mView.findViewById(R.id.ed_email);
        contact_dialog
                .setCancelable(false)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        addContact();
                    }
                })

                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = contact_dialog.create();
        alertDialogAndroid.show();
    }

    @Override
    public void attachToRecyclerView(ItemTouchHelper.SimpleCallback simpleItemTouchCallback) {

    }

    public void initSwipe(int position, final Context context, final AllContactsAdapter allContactsAdapter) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                Log.e("positiondeleter", "positiondeleter" + position);
                contact_id = Constants.contact_list.get(position).getContactID();


                if (direction == ItemTouchHelper.LEFT) {
                    final ArrayList ops = new ArrayList();
                    final ContentResolver cr = context.getContentResolver();
                    ops.add(ContentProviderOperation
                            .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                            .withSelection(
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = ?",
                                    new String[]{contact_id})
                            .build());


                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to Delete?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    allContactsAdapter.notifyDataSetChanged();
                                    getContacts();

                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    try {
                                        cr.applyBatch(ContactsContract.AUTHORITY, ops);
                                        ContactVO contactVO = new ContactVO();

                                        contactVO.setContactName(Constants.contact_list.get(position).getContactName());
                                        contactVO.setContactNumber(Constants.contact_list.get(position).getContactNumber());
                                        contactVO.setContactEmail(Constants.contact_list.get(position).getContactEmail());
                                        contactVO.setContactID(contact_id);
                                        contactVO.setStatus("Deleted");


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

                                        allContactsAdapter.removeItem(position, contact_id);
                                        allContactsAdapter.notifyDataSetChanged();


                                        ops.clear();
                                        getContacts();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    } catch (OperationApplicationException e) {
                                        e.printStackTrace();
                                    }


                                }
                            })

                            .show();


                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        mListener.attachToRecyclerView(simpleItemTouchCallback);

    }


    public void addContact() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        String name = con_name.getText().toString();
        String number = con_num.getText().toString();
        String email = con_mail.getText().toString();


        //------------------------------------------------------ Names
        if (name != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            name).build());
        }

        //------------------------------------------------------ Mobile Number
        if (number != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }


        //------------------------------------------------------ Email
        if (email != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        Toast.makeText(context, "Contact Added Successfully", Toast.LENGTH_SHORT).show();
        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            ContactVO contactVO = new ContactVO();
            contactVO.setContactName(name);
            contactVO.setContactNumber(number);
            contactVO.setContactEmail(email);
            contactVO.setStatus("New Contact");

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

            getContacts();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void getMarshmallowPermission() {

    }
}
