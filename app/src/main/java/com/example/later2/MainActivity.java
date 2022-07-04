package com.example.later2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    LocalData localData = new LocalData();
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    int width = 0;
    int height = 0;


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
        setScreenSizes();
        //dataBaseHelper.alterTable();
        //dataBaseHelper.getAll().toString();
        loadCheckLists();

    }

    void setScreenSizes(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    void loadCheckLists(){
        GridLayout gridLayout = findViewById(R.id.Chck_gridLayout);
        List<CheckLists> list = dataBaseHelper.getAll();
        for(int i = 0; i<list.size();i++){
            CardView card = new CardView(MainActivity.this);
            Button title = new Button(MainActivity.this);
            title.setText(list.get(i).getTitle());
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            title.setTextSize(18);
            title.setPadding(0,width/7,0,0);
            title.setTransformationMethod(null);
            TextView notesNum = new TextView(MainActivity.this);
            ConstraintSet set = new ConstraintSet();
            ConstraintLayout constraintLayout = new ConstraintLayout(MainActivity.this);

            Context context = title.getContext();
            Drawable d;
            try {
                //showText(list.get(i).getIcon());
                d = context.getResources().getDrawable(context.getResources().getIdentifier(list.get(i).getIcon(), "drawable", context.getPackageName()));
                String col = list.get(i).getColor();
                d.setTint(Color.parseColor(col));
                d.setBounds(0,0,160,160);
                title.setCompoundDrawables(null,d,null,null);
            }catch (Exception e){
                //showText(e.toString());
            }

            notesNum.setText(""+list.get(i).getNumberUnticked()+ "/"+ list.get(i).getNumberInList());
            notesNum.setTextSize(12);
            notesNum.setPadding(10,10,0,0);

            ImageButton favorite = new ImageButton(MainActivity.this);
            favorite.setForeground(getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
            favorite.setId(R.id.test2);

            if(!list.get(i).getPassword().equals("")){
                ImageButton lock = new ImageButton(this);
                lock.setForeground(getResources().getDrawable(R.drawable.ic_baseline_lock_open_24));
                setButtonRipple(lock);
                lock.setId(R.id.test3);
                lock.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
                set.connect(lock.getId(),ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 15);
                set.connect(lock.getId(),ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 15);
                constraintLayout.addView(lock,70,70);
            }


            ImageButton options = new ImageButton(this);
            options.setForeground(getResources().getDrawable(R.drawable.ic_baseline_more_vert_24));
            setButtonRipple(options);
            options.setId(R.id.test4);
            options.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View view) {
                    ImageButton btn = (ImageButton)view ;
                    addOptionsToButtons(btn);
                }
            });

            constraintLayout.setId(R.id.test);

            constraintLayout.addView(favorite,80,80);
            constraintLayout.addView(options,80,80);

            set.clone(constraintLayout);
            set.connect(favorite.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 2);
            set.connect(options.getId(),ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 15);
            set.connect(options.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 10);

            set.applyTo(constraintLayout);
            setButtonRipple(favorite);

            card.addView(constraintLayout,width/2-25,width/2-25);
            card.addView(notesNum);
            card.addView(title,width/2-25,width/2-25);

            title.setElevation(0);
            constraintLayout.setElevation(12);
            card.setRadius(35);
            card.setElevation(20);
            setButtonRipple(title);
            gridLayout.addView(card);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void addOptionsToButtons(ImageButton btn){
        PopupMenu popupMenuT = new PopupMenu(this, btn);
        popupMenuT.getMenu().add(Menu.NONE, 1, 1, "Edit");
        popupMenuT.getMenu().findItem(1).setIcon(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
        popupMenuT.getMenu().add(Menu.NONE, 2, 2, "Delete");
        popupMenuT.getMenu().findItem(2).setIcon(getResources().getDrawable(R.drawable.ic_baseline_delete_24));
        popupMenuT.setGravity(Gravity.END);

        popupMenuT.setForceShowIcon(true);
        popupMenuT.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get id of the clicked item
                int id = menuItem.getItemId();
                //handle clicks
                if(id==1){ //COPY

                }
                return false;
            }
        });
        popupMenuT.show();
    }

    void setButtonRipple(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            view.setBackgroundResource(outValue.resourceId);
        }
    }

    void showText(String text){
        Toast.makeText(findViewById(R.id.toolbar).getContext(), text, Toast.LENGTH_LONG).show();
    }

    public void floatingAddClicked(View view){
        //testEdit();
        //testDelete();
        //loadCheckLists();
    }

    void testDelete(){
        CheckLists checkLists = dataBaseHelper.getAll().get(0);
        boolean tmp = dataBaseHelper.deleteCheckList(checkLists);
        showText("DELETED: " +tmp);
    }

    void testEdit(){
        CheckLists checkLists = dataBaseHelper.getAll().get(0);
        checkLists.setIcon("ic_baseline_movie_24");
        dataBaseHelper.editCheckList(checkLists);
    }

    void test(){
        CheckLists checkLists = new CheckLists(-1,"test","BLACK",
                "","",false,"","","","normal","now","now",0,"",0);
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
            CheckLists checkLists = new CheckLists(-1,"To Do","#FFB45C",
                    "ic_baseline_content_paste_24","",false,"","","","normal",
                    Calendar.getInstance().getTime().toString(),Calendar.getInstance().getTime().toString(),0,"",0);
            dataBaseHelper.addCheckList(checkLists);

            checkLists = new CheckLists(-1,"Shopping","#5CBBFF",
                    "ic_baseline_shopping_cart_24","",false,"","","","normal",Calendar.getInstance().getTime().toString()
                    ,Calendar.getInstance().getTime().toString(),0,"",0);
            dataBaseHelper.addCheckList(checkLists);

            checkLists = new CheckLists(-1,"Watch Later","#FF5263",
                    "ic_baseline_movie_24","",false,"","","","movie",Calendar.getInstance().getTime().toString()
                    ,Calendar.getInstance().getTime().toString(),0,"",0);
            dataBaseHelper.addCheckList(checkLists);

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
        GridLayout gridLayout = findViewById(R.id.Chck_gridLayout);
        gridLayout.removeAllViews();
        if(text=="Checklist" ){
            loadCheckLists();
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
