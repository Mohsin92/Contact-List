package com.mohsin.contactdemo.ContactActivity.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mohsin.contactdemo.Base.BaseActivity;
import com.mohsin.contactdemo.ContactActivity.Adapter.AllContactsAdapter;
import com.mohsin.contactdemo.ContactActivity.Model.ContactVO;
import com.mohsin.contactdemo.ContactActivity.Presnter.MainPresenter;
import com.mohsin.contactdemo.ContactActivity.Presnter.MainPresenterInterface;
import com.mohsin.contactdemo.History.View.History;
import com.mohsin.contactdemo.R;
import com.mohsin.contactdemo.Utility.Constants;

import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends BaseActivity implements MainPresenterInterface, MainViewInterface, MainView.View {

    RecyclerView rvContacts;
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    MainPresenter mainPresenter;
    String contact_id;
    ArrayList<ContactVO> contactVOList = new ArrayList<ContactVO>();
    Toolbar mToolbar;
    boolean doubleBackToExitPressedOnce = false;

    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPresenter = new MainPresenter(this, this, this);
        mainPresenter.attachView(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        MarshmallowPermission();

        FabSpeedDial fab = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //mainPresenter.openDialog();
            }
        });
        fab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_history:
                        Intent intent = new Intent(MainActivity.this, History.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        break;

                    case R.id.action_add:
                        mainPresenter.openDialog();
                        break;
                }
                return false;
            }
        });
    }

    //For initialized
    @Override
    public void Init() {
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        setTitle("Contact List");

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

        rvContacts.setLayoutManager(layoutManager);
        
    }

    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap Back Button To Exit.", Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void MarshmallowPermission() {


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
// Toast.makeText(Splash_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                mainPresenter.getContacts();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("we need permission for read/write phone contacts")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("setting")
                .setPermissions(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS

                )
                .check();

    }


    @Override
    public void getMarshmallowPermission() {

    }

    @Override
    public void getallcontacts(ArrayList<ContactVO> contactVOList) {
        Constants.contact_list.clear();
        Constants.contact_list = contactVOList;
        AllContactsAdapter contactAdapter = new AllContactsAdapter(this, getApplicationContext(), contactVOList, false);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(contactAdapter);


        mainPresenter.initSwipe(pos, this, contactAdapter);

    }


    @Override
    public void openDialog() {

    }

    @Override
    public void attachToRecyclerView(ItemTouchHelper.SimpleCallback simpleItemTouchCallback) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvContacts);
    }

    @Override
    public void initSwipe(int position, Context context, AllContactsAdapter allContactsAdapter) {

    }


}
