package com.example.later2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.color.MaterialColors;
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

    String colorMode = "";
    String backColor ="";
    String foreColor = "";

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
                back(view);
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.list_swipe_refresh_layout);
        // Refresh  the layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetNotes();
                swipeRefreshLayout.setRefreshing(false); }
        }
        );
        darkModeCheck();
        addMainOptions();
        setUpBottomToolbar();
        checkFirstLogin();
        setScreenSizes();
        loadCheckLists();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void addOptionsToListButtons(ImageButton btn, String text, String idRecieved, int pos,CardView cardView){
        PopupMenu popupMenuT = new PopupMenu(this, btn);
        popupMenuT.getMenu().add(Menu.NONE, 0, 0, "Copy"); //parm 2 is menu id, param 3 is position of this menu item in menu items list, param 4 is title of the menu
        popupMenuT.getMenu().findItem(0).setIcon(getResources().getDrawable(R.drawable.ic_baseline_content_copy_24));
        popupMenuT.getMenu().add(Menu.NONE, 1, 1, "Edit");
        popupMenuT.getMenu().findItem(1).setIcon(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
        popupMenuT.getMenu().add(Menu.NONE, 2, 2, "Remove");
        popupMenuT.getMenu().findItem(2).setIcon(getResources().getDrawable(R.drawable.ic_baseline_delete_24));
        popupMenuT.setGravity(Gravity.END);
        if(URLUtil.isValidUrl(text)){
            popupMenuT.getMenu().add(Menu.NONE, 3, 3, "Visit Page");
            popupMenuT.getMenu().findItem(3).setIcon(getResources().getDrawable(R.drawable.ic_baseline_open_in_browser_24));
        }
        if(text.length()>50){
            popupMenuT.getMenu().add(Menu.NONE, 4, 4, "View Full Note");
            popupMenuT.getMenu().findItem(4).setIcon(getResources().getDrawable(R.drawable.ic_baseline_remove_red_eye_24));
        }
        popupMenuT.setForceShowIcon(true);
        popupMenuT.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get id of the clicked item
                int id = menuItem.getItemId();
                //handle clicks
                if(id==0){ //COPY
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", text);
                    clipboard.setPrimaryClip(clip);
                }
                else if (id==1){ //EDIT
                    FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    cardView.setVisibility(View.GONE);
                    //todo get data with id and pass onto sub, then update confirm btn and edit the thing.
                    CheckListData data = checkListDataHelper.getWithId(String.valueOf(idRecieved)).get(0);
                    currentAction = "editListItem";
                    cardForNewListItem(data.getTitle(),data.getDescription(),data.getColor(),-1,pos,data.getId());
                }
                else if (id==2){ // REMOVE
                    vibrate(120);
                    CheckListData data = checkListDataHelper.getWithId(String.valueOf(idRecieved)).get(0);
                    checkListDataHelper.deleteCheckListData(data);
                    CheckLists checkLists = dataBaseHelper.getWithId(openNote).get(0);
                    checkLists.setNumberInList(checkLists.getNumberInList()-1);
                    if(!data.isTicked()){
                        checkLists.setNumberUnticked(checkLists.getNumberUnticked()-1);
                    }
                    dataBaseHelper.editCheckList(checkLists);
                    cardView.setVisibility(View.GONE);
                }
                else if(id==3){
                    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( text ) );
                    startActivity( browse );
                }
                else if(id==4){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(text);
                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });
        popupMenuT.show();
    }



    void loadCheckListData(String FId,String title){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        TextView txt = findViewById(R.id.toolbarText);
        txt.setText(title);

        CheckListDataHelper helper = new CheckListDataHelper(MainActivity.this);
        List<CheckListData> data = helper.getWithFId(FId);
        SwipeRefreshLayout swipeRefreshLayout =findViewById(R.id.list_swipe_refresh_layout);
        LinearLayout linearLayout = findViewById(R.id.LinearListLayout);
        //linearLayout.setBackgroundColor(MaterialColors.getColor(this, R.attr.colorOnSecondary, Color.BLACK)); //todo color can be here
        //CheckLists parent = dataBaseHelper.getWithId(FId).get(0);
        //linearLayout.setBackgroundColor(Color.parseColor(parent.getColor()));

        swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (data.size()==0){ //empty list
            ImageButton imageButton = new ImageButton(MainActivity.this);
            imageButton.setBackgroundResource(R.drawable.empty_list);
            linearLayout.addView(imageButton, width,width);
        }
        else{
            for(int i =0; i< data.size();i++){
                LinearLayout linearLayoutHor = findViewById(R.id.LinearListLayout);

                LinearLayout innerLinear = new LinearLayout(this);
                innerLinear.setGravity(Gravity.CENTER_VERTICAL);
                innerLinear.setTag(i);
                LinearLayout expandableLayout = new LinearLayout(this);


                CardView cardView = new CardView(MainActivity.this);
                cardView.setCardBackgroundColor(MaterialColors.getColor(this, R.attr.colorOnSecondary, Color.WHITE));
                cardView.setTag(data.get(i).getId());
                ImageButton color = new ImageButton(MainActivity.this);
                //color.setForeground(getResources().getDrawable(R.drawable.ic_baseline_color_lens_24));
                //color.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(accentColor)));
                color.setBackgroundColor(Color.parseColor(data.get(i).getColor()));

                TextView txttitle = new TextView(this);
                txttitle.setTextSize(16);
                txttitle.setTag(String.valueOf(data.get(i).isTicked()));
                if(data.get(i).isTicked()){
                    txttitle.setPaintFlags(txttitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txttitle.setTextColor(Color.rgb(100, 100, 100));
                    cardView.setCardBackgroundColor(MaterialColors.getColor(this, R.attr.actionModeCopyDrawable, Color.WHITE));
                }
                else{
                    txttitle.setTextColor(MaterialColors.getColor(this, R.attr.colorOnPrimary, Color.BLACK));
                }

                setButtonEffect(txttitle);
                txttitle.setGravity(Gravity.CENTER_VERTICAL);
                txttitle.setPadding(20,0,0,0);

                txttitle.setText(data.get(i).getTitle());
                txttitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vibrate(50);
                        CheckLists checkLists = dataBaseHelper.getWithId(openNote).get(0);
                        CheckListData checkListData = checkListDataHelper.getWithId(String.valueOf(cardView.getTag())).get(0);
                        if(txttitle.getTag().equals("true")){
                            cardView.setCardBackgroundColor(MaterialColors.getColor(MainActivity.this, R.attr.colorOnSecondary, Color.WHITE));
                            txttitle.setPaintFlags(txttitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            txttitle.setTextColor(getResources().getColor(android.R.color.system_neutral2_10));
                            txttitle.setTag("false");
                            checkListData.setTicked(false);
                            checkLists.setNumberUnticked(checkLists.getNumberUnticked()+1);
                        }else{
                            cardView.setCardBackgroundColor(MaterialColors.getColor(MainActivity.this, R.attr.actionModeCopyDrawable, Color.WHITE));
                            txttitle.setPaintFlags(txttitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            txttitle.setTextColor(Color.rgb(100, 100, 100));
                            txttitle.setTag("true");
                            checkListData.setTicked(true);
                            checkLists.setNumberUnticked(checkLists.getNumberUnticked()-1);
                        }
                        dataBaseHelper.editCheckList(checkLists);
                        checkListDataHelper.editCheckListData(checkListData);
                    }
                });

                LinearLayout extra = new LinearLayout(this);
                TextView description = new TextView(this);
                description.setText(data.get(i).getDescription());
                extra.addView(description,width,150);
                extra.setVisibility(View.GONE);

                expandableLayout.addView(innerLinear);
                expandableLayout.addView(extra);
                expandableLayout.setOrientation(LinearLayout.VERTICAL);

                ImageButton options = new ImageButton(this);
                ImageButton expand = new ImageButton(this);
                setButtonRipple(expand);
                setButtonRipple(options);


                options.setForeground(getResources().getDrawable(R.drawable.ic_baseline_more_vert_24));
                options.setForegroundGravity(Gravity.CENTER);
                options.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onClick(View view) {
                        ImageButton tmpbtn =   (ImageButton) view;
                        addOptionsToListButtons(tmpbtn, txttitle.getText().toString(),cardView.getTag().toString(),Integer.parseInt(innerLinear.getTag().toString()),cardView);
                    }
                });
                expand.setForeground(getResources().getDrawable(R.drawable.ic_baseline_expand_more_24));
                expand.setForegroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                expand.setForegroundGravity(Gravity.CENTER);
                expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TransitionManager.beginDelayedTransition(cardView,
                                new AutoTransition());
                        if(extra.getVisibility()==View.VISIBLE){
                            extra.setVisibility(View.GONE);
                            expand.setForeground(getResources().getDrawable(R.drawable.ic_baseline_expand_more_24));
                        }else{
                            extra.setVisibility(View.VISIBLE);
                            expand.setForeground(getResources().getDrawable(R.drawable.ic_baseline_expand_less_24));
                        }
                    }
                });

                cardView.addView(expandableLayout);

                innerLinear.addView(color,15,180);
                innerLinear.addView(txttitle,width-(110*2),180);
                innerLinear.addView(expand,100,100);
                innerLinear.addView(options,100,100);

                linearLayout.addView(cardView);
            }
        }
    }

    void loadCheckLists(){
        GridLayout gridLayout = findViewById(R.id.Chck_gridLayout);
        gridLayout.removeAllViews();
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(View.VISIBLE);

        ScrollView scrollView = findViewById(R.id.MainScrollView);
        scrollView.setBackgroundColor(MaterialColors.getColor(this, R.attr.actionBarItemBackground, Color.BLACK));

        List<CheckLists> list = dataBaseHelper.getAll();
        for(int i = 0; i<list.size();i++){
            CardView card = new CardView(MainActivity.this);
            card.setCardBackgroundColor(MaterialColors.getColor(this, R.attr.colorOnSecondary, Color.WHITE));
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
                    openNote = title.getTag().toString();
                    loadCheckListData(title.getTag().toString(),title.getText().toString());
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
        else if (currentAction.equals("newListItem")){
            LinearLayout linearLayout = findViewById(R.id.LinearListLayout);
            linearLayout.removeViewAt(0);
            currentAction = "";
            FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        else if (currentAction.equals("editListItem")){
            //LinearLayout linearLayout = findViewById(R.id.LinearListLayout);
            //linearLayout.removeViewAt(0);
            currentAction = "";
            resetNotes();
            FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        else if(!openNote.equals("")){
            LinearLayout linearLayout = findViewById(R.id.LinearListLayout);
            linearLayout.removeAllViews();
            SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.list_swipe_refresh_layout);
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
            openNote = "";
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(null);
            TextView txt = findViewById(R.id.toolbarText);
            txt.setText("CheckList");
        }
    }

    @Override public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (which == BUTTON_POSITIVE ){
            int color = extras.getInt(SimpleColorDialog.COLOR);
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            outColor.setTag(hexColor);
            outColor.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));
            if(!currentAction.equals("newListItem") && !currentAction.equals("editListItem")){
                outIcon.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));
            }
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
        card.setCardBackgroundColor(MaterialColors.getColor(this, R.attr.colorOnSecondary, Color.WHITE));
        card.setId(R.id.card);
        card.setClickable(true);
        String action =currentAction;

        EditText title = new EditText(MainActivity.this);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setTextSize(18);
        title.setHint("Checklist Title");
        title.setId(R.id.text1);
        title.setText(givenText);
        title.requestFocus();
        showKeyboard();

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
        confirm.setTextColor(MaterialColors.getColor(this, R.attr.colorPrimary, Color.BLACK));
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
        TextView toolbar = (TextView) findViewById(R.id.toolbarText);
        toolbar.setText(text);
        GridLayout gridLayout = findViewById(R.id.Chck_gridLayout);
        gridLayout.removeAllViews();
        if(text.equals("Checklist")){
            loadCheckLists();
        }else if(text.equals("Notes")){

        }else if(text.equals("Settings")){
            loadSettings();
        }
    }

    boolean getSetting(String setting){
        SharedPreferences settings2 = getApplicationContext().getSharedPreferences("Settings", 0);
        boolean settingToRet = settings2.getBoolean(setting, true);
        return  settingToRet;
    }

    Button CreateSettingButton(String text, int iconimg){
        TypedValue outValue = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        Button btn = new Button(this);
        btn.setText(text);
        btn.setTransformationMethod(null);
        btn.setPadding(20,0,0,0);
        btn.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        Drawable icon= getResources().getDrawable(iconimg);
        icon.setBounds(0,0,50,50);
        icon.setColorFilter(MaterialColors.getColor(this, R.attr.colorOnPrimary, Color.BLACK), PorterDuff.Mode.SRC_IN);
        btn.setCompoundDrawables( icon, null, null, null );
        btn.setCompoundDrawablePadding(20);
        btn.setBackgroundResource(outValue.resourceId);
        return btn;
    }

    Switch CreateSettingSwitch(String text, int iconimg){
        Drawable icon= getResources().getDrawable(iconimg);
        icon.setBounds(0,0,50,50);
        Switch icons = new Switch(this);

        icon.setColorFilter(MaterialColors.getColor(this, R.attr.colorOnPrimary, Color.BLACK), PorterDuff.Mode.SRC_IN);
        icons.setCompoundDrawables( icon, null, null, null );
        icons.setCompoundDrawablePadding(20);
        icons.setText(text);
        icons.setPadding(20,0,0,0);
        //icons.setTextSize(16);
        return icons;
    }

    Button CreatebtnSplitter(){
        Button splitter = new Button(this);
        splitter.setBackgroundColor(Color.GRAY);
        return splitter;
    }

    void loadSettings(){
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(View.INVISIBLE);

        GridLayout list = findViewById(R.id.Chck_gridLayout);

        LinearLayout linearLayout = new LinearLayout(this);

        ImageView profilePic = new ImageView(this);

        //Glide.with(this).load(loggedInImageUri).circleCrop().into(profilePic);

        profilePic.setPadding(20,35,0,0);
        TextView txtEmail = new TextView(this);
        //txtEmail.setText(Username);
        txtEmail.setTextSize(20);
        txtEmail.setPadding(20,0,0,50);
        TextView txtName = new TextView(this);
        //txtName.setText(loggedInName);
        txtName.setTextSize(20);
        txtName.setPadding(20,23,0,0);

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.addView(txtName);
        tableLayout.addView(txtEmail);

        linearLayout.addView(profilePic,180,180);
        linearLayout.addView(tableLayout);


        //for selecting (visual)
        TypedValue outValue = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        //login btn
//        if(Username.equals("")){
//            TextView noAccountInfo = new TextView(this);
//            noAccountInfo.setText("No account information available. Please login below.");
//            noAccountInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            Button login = new Button(this);
//            login.setBackgroundResource(R.drawable.common_google_signin_btn_text_dark);
//            login.setText("Login with Google");
//            login.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                    startActivityForResult(signInIntent, RC_SIGN_IN);
//                }
//            });
//            list.addView(noAccountInfo,width,150);
//            list.addView(login,width,180);
//        }else{
//            list.addView(linearLayout);
//        }

        //logout btn
//        Button logout = CreateSettingButton("Logout",R.drawable.sign_out);//new Button(this);
//        logout.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                signOut();
//            }
//        });

        //settings layout
        TableLayout tl = new TableLayout(this);
        //tl.setBackgroundResource(R.drawable.full_background2);
        //tl.getBackground().setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
        list.addView(tl);//,width,height);

        //Sync btn
        Button sync = CreateSettingButton("Sync",R.drawable.ic_baseline_sync_24);
        sync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                if(Username!=""){
//                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
//                    ProcessDeleteOnline(true);
//                    //SyncData();
//                }else{
//                    Toast.makeText(findViewById(R.id.btnSettings).getContext(), "Please Login to Sync Data", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        Button shared = CreateSettingButton("Shared with me",R.drawable.ic_baseline_share_24);

        //Theme btn
        Button theme = CreateSettingButton("Theme",R.drawable.ic_baseline_color_lens_24);
        theme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //themebtnClicked();
            }
        });

        //trash btn
        Button trash = CreateSettingButton("Trash",R.drawable.ic_baseline_delete_24);



        //about btn
        Button about = CreateSettingButton("About",R.drawable.ic_baseline_info_24);
        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("ABOUT:...");
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        //Vibrate switch
        boolean vibrate = getSetting("vibrate");
        Switch vibrateSw = CreateSettingSwitch("Vibration",R.drawable.ic_baseline_vibration_24);
        if(vibrate){
            vibrateSw.setChecked(true);
        }
        vibrateSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Switch vibrateSw = (Switch) buttonView;
                SharedPreferences settings = getApplicationContext().getSharedPreferences("Settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                if(vibrateSw.isChecked()){
                    editor.putBoolean("vibrate", true);
                }else{
                    editor.putBoolean("vibrate", false);
                }
                editor.apply();
            }
        });

        //sync switch
        boolean syncIsOn = getSetting("auto_sync");
        Switch AutoSync = CreateSettingSwitch("Automatically sync data",R.drawable.ic_baseline_sync_24);
        if(syncIsOn){
            AutoSync.setChecked(true);
        }
        AutoSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(Username!=""){
//                    Switch autoSync = (Switch) buttonView;
//                    SharedPreferences settings = getApplicationContext().getSharedPreferences("Settings", 0);
//                    SharedPreferences.Editor editor = settings.edit();
//                    if(autoSync.isChecked()){
//                        editor.putBoolean("auto_sync", true);
//                    }else{
//                        editor.putBoolean("auto_sync", false);
//                    }
//                    editor.apply();
//                }else{
//                    AutoSync.setChecked(false);
//                    Toast.makeText(findViewById(R.id.btnSettings).getContext(), "Please Login to Sync Data", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        //dark mode switch
        boolean isOn = getSetting("dark_mode");
        Switch darkMode = CreateSettingSwitch("Dark Mode",R.drawable.ic_baseline_dark_mode_24);
        if(isOn){
            darkMode.setChecked(true);
        }
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                darkModeSwitch();
            }
        });

        //settings header
        TextView settings = new TextView(this);
        settings.setText("Settings");
        settings.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        settings.setTextColor(Color.GRAY);
        settings.setBackgroundColor(Color.BLACK);
        settings.setTextSize(18);
        //info header
        TextView information = new TextView(this);
        information.setText("Information");
        information.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        information.setTextColor(Color.GRAY);
        information.setBackgroundColor(Color.BLACK);
        information.setTextSize(18);
        //version header
        TextView version = new TextView(this);
        version.setText("Version: 0.1.0.");
        version.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        version.setTextColor(Color.GRAY);

        //FAQ btn
        Button FAQ = CreateSettingButton("FAQ",R.drawable.ic_baseline_question_mark_24);
        FAQ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("FAQ:\nHow do I change color of main screen icons?\n-Long hold on the icon you want to change the color of.");
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        //contact btn
        Button contact = CreateSettingButton("Contact",R.drawable.ic_baseline_email_24);
        contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Contact us: \nTest@gmail.com");
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        //TODO: font size, lock, about-description, version, updates, rate us, FAQ, Contact us
        tl.addView(settings,width,80);
        tl.addView(CreatebtnSplitter(),width,2);
        tl.addView(sync);
        tl.addView(theme);
        tl.addView(trash);
        tl.addView(vibrateSw,width-20,150);
        tl.addView(AutoSync,width-20,150);
        tl.addView(darkMode,width-20,150);
        tl.addView(shared);
//        if(Username!=""){
//            tl.addView(logout);
//        }

        tl.addView(information,width,80);
        tl.addView(CreatebtnSplitter(),width,2);

        tl.addView(about);
        tl.addView(FAQ);
        tl.addView(contact);
        tl.addView(version);
    }

    @SuppressLint("ResourceAsColor")
    public void floatingAddClicked(View view){
        view.setVisibility(View.INVISIBLE);
        //create addition
        if(openNote.equals("")){
            currentAction = "addNew";
            createCardEditAdd(0,"",null,null);
        }
        else{
            currentAction = "newListItem";
            cardForNewListItem(null,null,null,-1,0,-1);
        }
    }

    void cardForNewListItem(String titletxt, String descriptiontxt, String colortxt, int ratingtxt, int position, int givenID){
        LinearLayout linearLayout = findViewById(R.id.LinearListLayout);

        LinearLayout innerLinear = new LinearLayout(this);
        innerLinear.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout expandableLayout = new LinearLayout(this);

        CardView cardView = new CardView(MainActivity.this);
        cardView.setCardBackgroundColor(MaterialColors.getColor(this, R.attr.colorOnSecondary, Color.WHITE));
        ImageButton color = new ImageButton(MainActivity.this);
        //-----
        color.setForeground(getResources().getDrawable(R.drawable.ic_baseline_color_lens_24));
        if(colortxt ==null){
            color.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(accentColor)));
            color.setTag(accentColor);
        }else{
            color.setForegroundTintList(ColorStateList.valueOf(Color.parseColor(colortxt)));
            color.setTag(colortxt);
        }
        setButtonRipple(color);
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

        EditText title = new EditText(this);
        title.setHint("Title");
        title.setText(titletxt);
        title.requestFocus();
        showKeyboard();

        ImageButton confirm = new ImageButton(this);
        ImageButton expand = new ImageButton(this);
        setButtonRipple(expand);
        setButtonRipple(confirm);
        confirm.setForegroundGravity(Gravity.CENTER);
        expand.setForegroundGravity(Gravity.CENTER);
        confirm.setForeground(getResources().getDrawable(R.drawable.ic_baseline_check_24));
        expand.setForeground(getResources().getDrawable(R.drawable.ic_baseline_expand_more_24));
        expand.setForegroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        //-----

        LinearLayout extra = new LinearLayout(this);
        EditText description = new EditText(this);
        description.setHint("Description");
        description.setGravity(Gravity.TOP);
        description.setText(descriptiontxt);

        //---
        EditText rating = new EditText(this);
        rating.setHint("Rating");
        rating.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if(ratingtxt>=0){
            rating.setText(ratingtxt);
        }

        ImageButton urgent = new ImageButton(this);
        urgent.setForeground(getResources().getDrawable(R.drawable.ic_baseline_error_outline_24));
        urgent.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
        setButtonRipple(urgent);
        urgent.setForegroundGravity(Gravity.CENTER);

        LinearLayout rightLayout = new LinearLayout(this);
        rightLayout.addView(rating,240,120);
        rightLayout.addView(urgent,240,120);
        rightLayout.setOrientation(LinearLayout.VERTICAL);
        //---

        extra.addView(description,width-240,300);
        extra.addView(rightLayout,240,240);
        //extra.setVisibility(View.GONE);

        ImageButton divider = new ImageButton(this);
        divider.setBackgroundColor((MaterialColors.getColor(MainActivity.this, R.attr.colorOnPrimary, Color.WHITE)));
        ImageButton dividerTwo = new ImageButton(this);
        dividerTwo.setBackgroundColor((MaterialColors.getColor(MainActivity.this, R.attr.colorOnPrimary, Color.WHITE)));

        LinearLayout extraTwo = new LinearLayout(this);

        ImageButton image = new ImageButton(this);

        LinearLayout rightLayoutTwo = new LinearLayout(this);

        EditText dueDate =new EditText(this);
        dueDate.setHint("Due Date");

        EditText location = new EditText(this);
        location.setHint("Location");
        rightLayoutTwo.addView(dueDate,width-240,120);
        rightLayoutTwo.addView(location,width-240,120);
        rightLayoutTwo.setOrientation(LinearLayout.VERTICAL);

        extraTwo.addView(image,240,240);
        extraTwo.addView(rightLayoutTwo);

        LinearLayout verticalFull = new LinearLayout(this);
        verticalFull.setOrientation(LinearLayout.VERTICAL);
        verticalFull.addView(divider,width,2);
        verticalFull.addView(extra);
        verticalFull.addView(extraTwo);
        verticalFull.addView(dividerTwo,width,2);
        verticalFull.setVisibility(View.GONE);

        expandableLayout.addView(innerLinear);
        expandableLayout.addView(verticalFull);
        expandableLayout.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(expandableLayout);

        title.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if(currentAction.equals("newListItem")){
                        addNewCheckListData(title.getText().toString(),color.getTag().toString(),null,description.getText().toString(),false,null);
                        back(null);
                        resetNotes();
                    }else if(currentAction.equals("editListItem")){
                        CheckListData checkListData = checkListDataHelper.getWithId(String.valueOf(givenID)).get(0);
                        checkListData.setTitle(title.getText().toString());
                        checkListData.setDescription(description.getText().toString());
                        checkListData.setColor(color.getTag().toString());
                        checkListDataHelper.editCheckListData(checkListData);
                        back(null);
                        resetNotes();
                    }
                    return true;
                }
                return false;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentAction.equals("newListItem")){
                    addNewCheckListData(title.getText().toString(),color.getTag().toString(),null,description.getText().toString(),false,null);
                    back(null);
                    resetNotes();
                }else if(currentAction.equals("editListItem")){
                    CheckListData checkListData = checkListDataHelper.getWithId(String.valueOf(givenID)).get(0);
                    checkListData.setTitle(title.getText().toString());
                    checkListData.setDescription(description.getText().toString());
                    checkListData.setColor(color.getTag().toString());
                    checkListDataHelper.editCheckListData(checkListData);
                    back(null);
                    resetNotes();
                }
            }
        });

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(cardView,
                        new AutoTransition());
                if(verticalFull.getVisibility()==View.VISIBLE){
                    verticalFull.setVisibility(View.GONE);
                    expand.setForeground(getResources().getDrawable(R.drawable.ic_baseline_expand_more_24));
                    cardView.setCardBackgroundColor(MaterialColors.getColor(MainActivity.this, R.attr.colorOnSecondary, Color.WHITE));
                }else{
                    verticalFull.setVisibility(View.VISIBLE);
                    expand.setForeground(getResources().getDrawable(R.drawable.ic_baseline_expand_less_24));
                    cardView.setCardBackgroundColor(MaterialColors.getColor(MainActivity.this, R.attr.actionBarItemBackground, Color.WHITE));
                }
            }
        });

        innerLinear.addView(color,120,120);
        innerLinear.addView(title,width-(120*3),180);
        innerLinear.addView(expand,120,120);
        innerLinear.addView(confirm,120,120);

        linearLayout.addView(cardView,position);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void testEdit(){
        CheckLists checkLists = dataBaseHelper.getAll().get(0);
        checkLists.setIcon("ic_baseline_movie_24");
        dataBaseHelper.editCheckList(checkLists);
    }

    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    void darkModeCheck(){
        SharedPreferences settings2 = getApplicationContext().getSharedPreferences("Settings", 0);
        boolean isOn = settings2.getBoolean("dark_mode", false);
        if(isOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //change between dark mode and non dark mode. Some changes in either manifest of gradle were needed.
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    void resetNotes(){
        if(!currentAction.equals("")){
            back(null);
        }
        DataBaseHelper helper = new DataBaseHelper(this);
        String title = helper.getWithId(openNote).get(0).getTitle();
        LinearLayout linearLayout = findViewById(R.id.LinearListLayout);
        linearLayout.removeAllViews();
        loadCheckListData(openNote,title);
    }

    public void darkModeSwitch(){
        SharedPreferences settings2 = getApplicationContext().getSharedPreferences("Settings", 0);
        boolean isOn = settings2.getBoolean("dark_mode", false);
        Boolean toUpdate;// = false;
        if(!isOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //change between dark mode and non dark mode. Some changes in either manifest of gradle were needed.
            toUpdate = true;
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            toUpdate = false;
        }
        SharedPreferences settings = getApplicationContext().getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("dark_mode", toUpdate);
        editor.apply();
    }

    void setButtonRipple(View view){
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's// selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            view.setBackgroundResource(outValue.resourceId);
        //}
    }

    void vibrate(int time){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(time);
        }
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

    CheckListDataHelper checkListDataHelper = new CheckListDataHelper(this);
    void addNewCheckListData(String name, String color, String rating, String description, boolean urgent, String dateDue){
        CheckListData checkListData = new CheckListData(-1, Integer.parseInt(openNote),name, color, description,null,urgent,false,rating,
                10,dateDue,Calendar.getInstance().getTime().toString(),Calendar.getInstance().getTime().toString(),
                null,null,null,null,null,null);
        checkListDataHelper.addCheckListData(checkListData);
        //increase number of notes by 1
        CheckLists checkLists = dataBaseHelper.getWithId(String.valueOf(openNote)).get(0);
        checkLists.setNumberInList(checkLists.getNumberInList()+1);
        checkLists.setNumberUnticked(checkLists.getNumberUnticked()+1);
        dataBaseHelper.editCheckList(checkLists);
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
