package com.example.later2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import java.util.Calendar;
//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    LocalData localData = new LocalData();
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolbarBackbtnClicked(view);
            }
        });
        addMainOptions();
        setUpBottomToolbar();
        checkFirstLogin();
    }

    void loadCheckLists(){

    }

    void showText(String text){
        Toast.makeText(findViewById(R.id.toolbar).getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void floatingAddClicked(View view){
        //testEdit();
        //testDelete();
        showText(dataBaseHelper.getAll().toString());
    }

    void testDelete(){
        CheckLists checkLists = dataBaseHelper.getAll().get(0);
        boolean tmp = dataBaseHelper.deleteCheckList(checkLists);
        showText("DELETED: " +tmp);
    }

    void testEdit(){
        CheckLists checkLists = dataBaseHelper.getAll().get(0);
        checkLists.setTitle("working");
        dataBaseHelper.editCheckList(checkLists);
    }

    void test(){
        CheckLists checkLists = new CheckLists(-1,"test","BLACK",
                "","",false,"","","","normal","now","now",0,"");
        boolean tmp = dataBaseHelper.addCheckList(checkLists);
        showText("success: " + tmp);
    }

    void checkFirstLogin(){
        Boolean first = localData.getDataBool("Settings","LoggedBefore",this.getApplicationContext());
        if(!first){
            showText("Welcome!");
            localData.setDataBool("Settings","LoggedBefore",true,this.getApplicationContext());
            localData.setDataBool("Settings","DarkMode",true,this.getApplicationContext());
            localData.setDataBool("Settings","Vibration",true,this.getApplicationContext());
            localData.setDataBool("Settings","AutoSync",false,this.getApplicationContext());
            CheckLists checkLists = new CheckLists(-1,"To Do","#000000",
                    "","",false,"","","","normal",
                    Calendar.getInstance().getTime().toString(),Calendar.getInstance().getTime().toString(),0,"");
            boolean tmp = dataBaseHelper.addCheckList(checkLists);
            //showText("success: " + tmp);
            checkLists = new CheckLists(-1,"Shopping","#FFFFFF",
                    "","",false,"","","","normal",Calendar.getInstance().getTime().toString()
                    ,Calendar.getInstance().getTime().toString(),0,"");
            tmp = dataBaseHelper.addCheckList(checkLists);
            //showText("success: " + tmp);
            checkLists = new CheckLists(-1,"Watch Later","#00ffff",
                    "","",false,"","","","movie",Calendar.getInstance().getTime().toString()
                    ,Calendar.getInstance().getTime().toString(),0,"");
            tmp = dataBaseHelper.addCheckList(checkLists);
            showText("success: " + tmp);
        }
    }

    void setUpBottomToolbar(){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationClicked("Checklist");
            }
        });
        tab = tabLayout.getTabAt(1);
        tab.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationClicked("Notes");
            }
        });
        tab = tabLayout.getTabAt(2);
        tab.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationClicked("Settings");
            }
        });
    }

    public void ToolbarBackbtnClicked(View view){
        System.out.println("test");
    }

    public void btnOverflowClicked(View view){
        popupMenu.show();
    }

    public void bottomNavigationClicked(String text){
        //todo clear page
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(text);

        if(text=="Checklist" ){

        }else if(text=="Notes"){

        }else if(text=="Settings"){

        }
    }

    PopupMenu popupMenu;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    void addMainOptions(){
        popupMenu = new PopupMenu(this, findViewById(R.id.btnMainOverflow));
        //add menu items in popup menu

        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Save"); //parm 2 is menu id, param 3 is position of this menu item in menu items list, param 4 is title of the menu
        popupMenu.getMenu().findItem(0).setIcon(getResources().getDrawable(R.drawable.ic_baseline_save_24));
        popupMenu.getMenu().findItem(0).setIconTintList(ContextCompat.getColorStateList(this, android.R.color.secondary_text_dark_nodisable));
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Search");
        popupMenu.getMenu().findItem(1).setIcon(getResources().getDrawable(R.drawable.ic_baseline_search_24));
        popupMenu.getMenu().findItem(1).setIconTintList(ContextCompat.getColorStateList(this, android.R.color.secondary_text_dark_nodisable));
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Undo");
        popupMenu.getMenu().findItem(2).setIcon(getResources().getDrawable(R.drawable.ic_baseline_undo_24));
        popupMenu.getMenu().findItem(2).setIconTintList(ContextCompat.getColorStateList(this, android.R.color.secondary_text_dark_nodisable));
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Delete All");
        popupMenu.getMenu().findItem(3).setIcon(getResources().getDrawable(R.drawable.ic_baseline_delete_24));
        popupMenu.getMenu().findItem(3).setIconTintList(ContextCompat.getColorStateList(this, android.R.color.secondary_text_dark_nodisable));
        popupMenu.getMenu().add(Menu.NONE, 5, 5, "Sort by");
        popupMenu.getMenu().findItem(5).setIcon(getResources().getDrawable(R.drawable.ic_baseline_sort_24));
        popupMenu.getMenu().findItem(5).setIconTintList(ContextCompat.getColorStateList(this, android.R.color.secondary_text_dark_nodisable));
        //popupMenu.getMenu().add(Menu.NONE, 6, 6, "Move Items");
        //popupMenu.getMenu().findItem(6).setIcon(setPopUpIcon(R.drawable.mover_24));
        popupMenu.getMenu().add(Menu.NONE, 7, 7, "Filter");
        popupMenu.getMenu().findItem(7).setIcon(getResources().getDrawable(R.drawable.ic_baseline_filter_list_24));
        popupMenu.getMenu().findItem(7).setIconTintList(ContextCompat.getColorStateList(this, android.R.color.secondary_text_dark_nodisable));
        popupMenu.getMenu().add(Menu.NONE, 8, 8, "Refresh");
        popupMenu.getMenu().findItem(8).setIcon(getResources().getDrawable(R.drawable.ic_baseline_refresh_24));
        popupMenu.getMenu().findItem(8).setIconTintList(ContextCompat.getColorStateList(this, android.R.color.secondary_text_dark_nodisable));
        popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get id of the clicked item
                int id = menuItem.getItemId();
                //handle clicks
                if (id==0){ //SAVED

                }
                return false;
            }
        });
    }

}