package com.mohsin.contactdemo.EditContact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mohsin.contactdemo.Base.BaseActivity;
import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;
import com.mohsin.contactdemo.ContactActivity.View.MainActivity;
import com.mohsin.contactdemo.EditContact.Presenter.EditContactInterface;
import com.mohsin.contactdemo.EditContact.Presenter.EditContactPresenter;
import com.mohsin.contactdemo.EditContact.View.EditView;

import com.mohsin.contactdemo.R;

/**
 * Created by mohsin on 12/8/17.
 */

public class EditContact extends BaseActivity implements EditContactInterface,EditView.View, View.OnClickListener {

    EditText ed_name,ed_num,ed_email,ed_home,ed_company;
    Button btn_update;
    String name,email,num,contact_id,home_number,company;
    EditContactPresenter editContactPresenter;
    Toolbar mToolbar;

    ContactVO contact_list=new ContactVO();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);





    }

    @Override
    public void Init() {
        setContentView(R.layout.edit_contact);
        contact_list = (ContactVO) getIntent().getSerializableExtra("json");
        editContactPresenter=new EditContactPresenter(this,this,this);
        editContactPresenter.attachView(this);

        ed_name=(EditText) findViewById(R.id.ed_firstname);
        ed_num=(EditText) findViewById(R.id.ed_number);
        ed_email=(EditText)findViewById(R.id.ed_email);
        ed_home=(EditText)findViewById(R.id.ed_homenumber);
        ed_company=(EditText)findViewById(R.id.ed_company);
        btn_update=(Button)findViewById(R.id.btn_edit);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        setTitle("EDIT CONTACT");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("??");
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
        btn_update.setOnClickListener(this);


        editContactPresenter.editContact(contact_list.getContactID());

    }

    @Override
    public void editContact(ContactVO contactVO) {
        ed_name.setText(contactVO.getContactName());
        ed_num.setText(contactVO.getContactNumber());
        ed_email.setText(contactVO.getContactEmail());
        ed_home.setText(getResources().getString(R.string.in_development));
        ed_company.setText(getResources().getString(R.string.in_development));



    }

    @Override
    public void updateContact(String name, String num, String email, String contactId) {
        Intent main=new Intent(this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_edit:
                contact_id=contact_list.getContactID();
                name=ed_name.getText().toString();
                num=ed_num.getText().toString();
                email=ed_email.getText().toString();
                home_number=ed_home.getText().toString();
                company=ed_company.getText().toString();
                editContactPresenter.update(name,num,email,contact_id,home_number,company);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
