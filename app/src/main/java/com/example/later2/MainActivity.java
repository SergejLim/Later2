package com.example.later2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;
import eltos.simpledialogfragment.color.SimpleColorWheelDialog;
//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener{

    LocalData localData = new LocalData();
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    int width = 0;
    int height = 0;
    String openNote = "";
    String accentColor = "#FFB45C";
    String currentAction = "";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        loadCheckLists();
    }

    void loadCheckListData(String FId){
        CheckListDataHelper helper = new CheckListDataHelper(MainActivity.this);
        List<CheckListData> data = helper.getWithFId(FId);
        SwipeRefreshLayout swipeRefreshLayout =findViewById(R.id.list_swipe_refresh_layout);
        LinearLayout linearLayout = findViewById(R.id.LinearListLayout);

        swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (data.size()==0){
            ImageButton imageButton = new ImageButton(MainActivity.this);
            imageButton.setBackgroundResource(R.drawable.empty_list);
            linearLayout.addView(imageButton, width,width);
        }
        else{

        }
    }

    void loadCheckLists(){
        GridLayout gridLayout = findViewById(R.id.Chck_gridLayout);
        gridLayout.removeAllViews();

        List<CheckLists> list = dataBaseHelper.getAll();
        for(int i = 0; i<list.size();i++){
            CardView card = new CardView(MainActivity.this);
            card.setTag(list.get(i).getId());

            Button title = new Button(MainActivity.this);
            title.setText(list.get(i).getTitle());
            title.setTag(list.get(i).getId());
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            title.setTextSize(18);
            title.setPadding(0,width/7,0,0);
            title.setTransformationMethod(null);
            TextView notesNum = new TextView(MainActivity.this);
            ConstraintSet set = new ConstraintSet();
            ConstraintLayout constraintLayout = new ConstraintLayout(MainActivity.this);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo BUTTON CLICK
                    openNote = title.getTag().toString();
                    loadCheckListData(title.getTag().toString());
                }
            });

            Context context = title.getContext();
            Drawable d;
            try {
                d = context.getResources().getDrawable(context.getResources().getIdentifier(list.get(i).getIcon(), "drawable", context.getPackageName()));
                String col = list.get(i).getColor();
                d.setTint(Color.parseColor(col));
                d.setBounds(0,0,160,160);
                title.setCompoundDrawables(null,d,null,null);
            }catch (Exception e){}

            notesNum.setText(""+list.get(i).getNumberUnticked()+ "/"+ list.get(i).getNumberInList());
            notesNum.setTextSize(12);
            notesNum.setPadding(10,10,0,0);

            ImageButton favorite = new ImageButton(MainActivity.this);
            if(list.get(i).isFavourite()){
                favorite.setForeground(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
                favorite.setTag(true);
            }else{
                favorite.setForeground(getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                favorite.setTag(false);
            }
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckLists checkLists = dataBaseHelper.getWithId(String.valueOf(card.getTag())).get(0);
                    if(favorite.getTag().equals(true)){
                        favorite.setForeground(getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                        favorite.setTag(false);
                        checkLists.setFavourite(false);
                    }else{
                        favorite.setForeground(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
                        favorite.setTag(true);
                        checkLists.setFavourite(true);
                    }
                    dataBaseHelper.editCheckList(checkLists);
                }
            });
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
                    addOptionsToButtons(btn,card.getTag().toString(),title.getText().toString());
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
    void addOptionsToButtons(ImageButton btn, String ChecklistId, String name){
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
                if(id==1){ //EDIT
                    currentAction = "edit";
                    CheckLists ch = dataBaseHelper.getWithId(ChecklistId).get(0);
                    createCardEditAdd(ch.getId(), ch.getTitle(),ch.getColor(),ch.getIcon());
                }else if(id==2){//DELETE
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Are you sure you want to delete all data for " + name + "?");
                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataBaseHelper.deleteCheckList(dataBaseHelper.getWithId(ChecklistId).get(0));
                            loadCheckLists();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }});
                    builder.show();
                }
                return false;
            }
        });
        popupMenuT.show();
    }

    @Override
    public void onBackPressed() {
        ConstraintLayout cn = findViewById(R.id.extraLayout);
        if (openNote.equals("") && currentAction.equals("")){
            super.onBackPressed();
        } else{
            back(null);
        }
    }

    public void back(View view){
        if(currentAction.equals("addNew") || currentAction.equals("edit")){
            currentAction = "";
            ConstraintLayout cn = findViewById(R.id.extraLayout);
            cn.removeAllViews();
            FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
            floatingActionButton.setVisibility(View.VISIBLE);
            cn.setVisibility(View.INVISIBLE);
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            tabLayout.setVisibility(View.VISIBLE);
        }else if (currentAction.contains("iconSelect")){
            currentAction = currentAction.replace("iconSelect","");
            ConstraintLayout cn = findViewById(R.id.extraLayout);
            cn.removeViewAt(1);
        }
        else if(!openNote.equals("")){
            LinearLayout linearLayout = findViewById(R.id.LinearListLayout);
            linearLayout.removeAllViews();
            SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.list_swipe_refresh_layout);
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
            openNote = "";
        }
    }

    @Override public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (which == BUTTON_POSITIVE ){
            int color = extras.getInt(SimpleColorDialog.COLOR);
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            outColor.setTag(hexColor);
            outColor.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));
            outIcon.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));
            return true;
        }
        return false;
    }

    ImageButton outIcon;
    ImageButton outColor;
    void createCardEditAdd(int id, String givenText, String givenColor, String givenIcon){
        FloatingActionButton fl = findViewById(R.id.floatingActionButton);
        fl.setVisibility(View.INVISIBLE);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.INVISIBLE);

        CardView card = new CardView(MainActivity.this);
        card.setId(R.id.card);
        card.setClickable(true);
        String action =currentAction;

        EditText title = new EditText(MainActivity.this);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setTextSize(18);
        title.requestFocus();
        title.setHint("Checklist Title");
        //title.setActivated(true);
        title.setId(R.id.text1);
        title.setText(givenText);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //showkeyboard
        imm.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);

        String colorToUse =accentColor;
        if (givenColor!=null){
            colorToUse=givenColor;
        }

        ImageButton icon = new ImageButton(this);
        icon.setId(R.id.test4);
        setButtonRipple(icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                hideKeyboard(MainActivity.this);
                currentAction =currentAction+ "iconSelect";
                Field[] drawablesFields = R.drawable.class.getFields();
                ScrollView scrollView = new ScrollView(MainActivity.this);
                GridLayout layout = new GridLayout(MainActivity.this);
                layout.setColumnCount(5);
                ConstraintLayout extra = findViewById(R.id.extraLayout);
                scrollView.addView(layout);
                extra.addView(scrollView,1);
                layout.setBackgroundColor(Color.parseColor("#000000"));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        extra.removeView(scrollView);
                        currentAction=action;
                    }});
                //todo SEARCH
                scrollView.setElevation(10);
                for (Field field : drawablesFields) {
                    try {
                        if(field.getName().contains("ic_") && field.getName().contains("_24")){
                            Button btn = new Button(MainActivity.this);

                            btn.setForeground(getResources().getDrawable(field.getInt(null)));

                            btn.setText(field.getName());
                            btn.setTextSize(0);
                            btn.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                            btn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    icon.setForeground(btn.getForeground());
                                    icon.setTag(btn.getText().toString());
                                    extra.removeView(scrollView);
                                    currentAction=action;
                                }
                            });
                            layout.addView(btn,width/5,width/5);
                        }
                    } catch (Exception e) {}
                }
            }
        });
        icon.setForeground(getResources().getDrawable(R.drawable.ic_baseline_star_24));
        icon.setTag("ic_baseline_star_24");
        outIcon = icon;

        if(givenIcon!=null){
            Context context = title.getContext();
            Drawable d;
            try {
                d = context.getResources().getDrawable(context.getResources().getIdentifier(givenIcon, "drawable", context.getPackageName()));
                icon.setForeground(d);
                icon.setTag(givenIcon);
            }catch (Exception e){ }
        }
        icon.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(colorToUse)));

        ConstraintSet set = new ConstraintSet();
        ConstraintLayout constraintLayout = new ConstraintLayout(MainActivity.this);

        ImageButton color = new ImageButton(this);
        color.setForeground(getResources().getDrawable(R.drawable.ic_baseline_color_lens_24));
        color.setId(R.id.test2);
        color.setTag(colorToUse);
        setButtonRipple(color);
        constraintLayout.addView(color,100,100);
        color.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(colorToUse)));
        outColor = color;
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] clr = new int[]{ Color.parseColor("#c66900"),Color.parseColor("#FF5263"),Color.parseColor("#FFB45C"),Color.parseColor("#FFD952"),
                        Color.parseColor("#82FF6E"), Color.parseColor("#6EFFE5"),Color.parseColor("#5CBBFF")
                        ,Color.parseColor("#9E52FF"),Color.parseColor("#FF6EC5"),Color.parseColor("#FFFFFF"),Color.parseColor("#000000") };
                SimpleColorDialog.build()
                        .colors(clr)
                        .allowCustom(true)
                        .show(MainActivity.this);
            }
        });

        Button cancel = new Button(this);
        Button confirm = new Button(this);
        cancel.setText("Cancel");
        confirm.setText("Confirm");
        setButtonEffect(confirm);
        setButtonEffect(cancel);
        cancel.setId(R.id.cancel);
        confirm.setId(R.id.confirm);
        confirm.setTextColor(Color.parseColor("#c66900"));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(null);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().length()==0){
                    showText("Cannot add empty");
                }else{
                    if(currentAction.equals("addNew")){
                        addNewCheckList(title.getText().toString(),color.getTag().toString(),icon.getTag().toString(),"","normal");
                    }else if(currentAction.equals("edit")){
                        CheckLists checkLists = dataBaseHelper.getWithId(String.valueOf(id)).get(0);
                        checkLists.setTitle(title.getText().toString());
                        checkLists.setIcon(icon.getTag().toString());
                        checkLists.setColor(color.getTag().toString());
                        dataBaseHelper.editCheckList(checkLists);
                    }
                    back(null);
                    loadCheckLists();
                }
            }
        });

        ImageButton lock = new ImageButton(this);
        lock.setForeground(getResources().getDrawable(R.drawable.ic_baseline_lock_open_24));
        constraintLayout.setId(R.id.test);
        setButtonRipple(lock);
        lock.setId(R.id.test3);
        constraintLayout.addView(lock,85,85);
        constraintLayout.addView(icon,200,200);
        constraintLayout.addView(title,(int)(width*0.8),150);

        constraintLayout.addView(cancel);
        constraintLayout.addView(confirm);

        lock.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
        set.clone(constraintLayout);
        set.connect(lock.getId(),ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 15);
        set.connect(lock.getId(),ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 15);

        set.connect(color.getId(),ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 15);
        set.connect(color.getId(),ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 15);

        set.connect(icon.getId(),ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
        set.connect(icon.getId(),ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 150);
        set.connect(icon.getId(),ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 15);
        set.connect(icon.getId(),ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 15);

        set.connect(title.getId(),ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 160);

        set.connect(cancel.getId(),ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 10);
        set.connect(cancel.getId(),ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 10);
        set.connect(cancel.getId(),ConstraintSet.RIGHT, confirm.getId(), ConstraintSet.LEFT, 10);

        set.connect(confirm.getId(),ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 10);
        set.connect(confirm.getId(),ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 10);
        set.connect(confirm.getId(),ConstraintSet.LEFT, cancel.getId(), ConstraintSet.RIGHT, 10);

        set.applyTo(constraintLayout);
        card.addView(constraintLayout,(int)(width*0.8),(int)(width*0.8));

        title.setElevation(0);
        constraintLayout.setElevation(12);
        card.setRadius(35);
        card.setElevation(1);

        ConstraintLayout extra = findViewById(R.id.extraLayout);

        extra.setVisibility(View.VISIBLE);
        ConstraintSet extraSet = new ConstraintSet();
        extra.addView(card);
        extraSet.clone(extra);

        extraSet.connect(card.getId(),ConstraintSet.BOTTOM, extra.getId(), ConstraintSet.BOTTOM, 200);
        extraSet.connect(card.getId(),ConstraintSet.LEFT, extra.getId(), ConstraintSet.LEFT, 15);
        extraSet.connect(card.getId(),ConstraintSet.RIGHT, extra.getId(), ConstraintSet.RIGHT, 15);
        extraSet.connect(card.getId(),ConstraintSet.TOP, extra.getId(), ConstraintSet.TOP, 10);
        extraSet.applyTo(extra);
    }

    public void bottomNavigationClicked(String text){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(text);
        GridLayout gridLayout = findViewById(R.id.Chck_gridLayout);
        gridLayout.removeAllViews();
        if(text.equals("Checklist")){
            loadCheckLists();
        }else if(text.equals("Notes")){

        }else if(text.equals("Settings")){

        }
    }

    @SuppressLint("ResourceAsColor")
    public void floatingAddClicked(View view){
        view.setVisibility(View.INVISIBLE);
        //create addition
        if(openNote.equals("")){
            currentAction = "addNew";
            createCardEditAdd(0,"",null,null);
        }
    }

    void testEdit(){
        CheckLists checkLists = dataBaseHelper.getAll().get(0);
        checkLists.setIcon("ic_baseline_movie_24");
        dataBaseHelper.editCheckList(checkLists);
    }

    void setButtonRipple(View view){
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's// selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            view.setBackgroundResource(outValue.resourceId);
        //}
    }

    void setButtonEffect(View view){
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's// selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            view.setBackgroundResource(outValue.resourceId);
        //}
    }

    void showText(String text){
        Toast.makeText(findViewById(R.id.toolbar).getContext(), text, Toast.LENGTH_LONG).show();
    }

    void addNewCheckList(String name, String color, String icon, String password,String type){
        CheckLists checkLists = new CheckLists(-1,name,color,
                icon,password,false,"","","",type,
                Calendar.getInstance().getTime().toString(),Calendar.getInstance().getTime().toString(),0,"",0);
        dataBaseHelper.addCheckList(checkLists);
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

    void setScreenSizes(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
