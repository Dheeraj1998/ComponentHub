package com.example.componenthub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.componenthub.R;
import com.example.componenthub.fragment.HomeFragment;
import com.example.componenthub.fragment.InventoryFragment;
import com.example.componenthub.fragment.IssueFragment;
import com.example.componenthub.fragment.ReportFragment;
import com.example.componenthub.other.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.support.v7.widget.AppCompatDrawableManager.get;

public class DashboardActivity extends AppCompatActivity {

    private int QR_length = 8;
    private int max_IssueDays = 14;
    private DatabaseReference component_database;
    private DatabaseReference user_database;
    private String issueDate;
    private String returnDate;
    FirebaseAuth mAuth;
    private String user_email;

    // Initialising global variables
    private Toolbar toolbar;

    private String[] activityTitles;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        mAuth = FirebaseAuth.getInstance();
        user_email = mAuth.getCurrentUser().getEmail();

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();

                if(fragment == null){
                    beginLogout();
                }

                else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                InventoryFragment photosFragment = new InventoryFragment();
                return photosFragment;
            case 2:
                // movies fragment
                IssueFragment moviesFragment = new IssueFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                ReportFragment notificationsFragment = new ReportFragment();
                return notificationsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                // Check if log-out is selected
                int log_out = 0;

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_inventory:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_issue:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_report:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_signout:
                        log_out = 1;
                        break;
                    default:
                        navItemIndex = 0;
                }

                if(log_out == 1){
                    beginLogout();
                    return false;
                }

                else {
                    //Checking if the item is in checked state or not, if not make it in checked state
                    if (menuItem.isChecked()) {
                        menuItem.setChecked(false);
                    } else {
                        menuItem.setChecked(true);
                    }
                    menuItem.setChecked(true);

                    loadHomeFragment();
                    return true;
                }
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    //region Code for handling the QR code scanner
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        final Button scan_button = (Button) findViewById(R.id.btn_start_scan);
        final ProgressBar scan_progress = (ProgressBar) findViewById(R.id.prb_scan);

        // Setting up the Progress bar
        scan_button.setVisibility(View.INVISIBLE);
        scan_progress.setVisibility(View.VISIBLE);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            final String item_id = result.getContents();

            // Function to handle invalid QR scans
            if (result.getContents() == null || result.getContents().length() != QR_length) {
                Toast.makeText(this, "No valid QR code was found!", Toast.LENGTH_LONG).show();
            }

            // Function to handle correct QR codes
            else {
                // Get the current date from the system
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                issueDate = df.format(c.getTime());

                // Find the return date
                c.add(Calendar.DATE, max_IssueDays);
                returnDate = df.format(c.getTime());

                // Getting the reference for the item in database
                component_database = FirebaseDatabase.getInstance().getReference().child("inventory_details").child(item_id);

                // Getting the reference for the item in database
//                user_database = FirebaseDatabase.getInstance().getReference().child("user_profiles");

                // Connect to the database and create an event
                 component_database.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if(dataSnapshot.child("IssueDate").getValue() == null){
                             Toast.makeText(getApplicationContext(), "Not a valid QR code.", Toast.LENGTH_SHORT).show();
                         }

                         else if(dataSnapshot.child("IssueDate").getValue().toString().equals("NA")){
                             // Sets the issue/return date
                             component_database.child("CurrentIssue").setValue(user_email);
                             component_database.child("IssueDate").setValue(issueDate);
                             component_database.child("Renewal").setValue(returnDate);

                             // Stores the values in the issuer's database
//                             user_database.orderByChild("email_address").equalTo("email_address").on("")

                             // Closure message
                             Toast.makeText(getApplicationContext(), "The item has been issued!", Toast.LENGTH_SHORT).show();
                         }

                         else{
                             // Item already issued
                             Toast.makeText(getApplicationContext(), "Sorry, the item is already issued!", Toast.LENGTH_SHORT).show();
                         }

                         // Closing the Progress bar
                         scan_button.setVisibility(View.VISIBLE);
                         scan_progress.setVisibility(View.INVISIBLE);
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {
                         // Closing the Progress bar
                         scan_button.setVisibility(View.VISIBLE);
                         scan_progress.setVisibility(View.INVISIBLE);

                         Toast.makeText(getApplicationContext(), R.string.generic_error_message, Toast.LENGTH_SHORT).show();
                     }
                 });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    //endregion

    //region Code for handling the logout of users
    public void beginLogout() {
        try {
            mAuth.signOut();

            Intent temp = new Intent(DashboardActivity.this, SplashScreen.class);
            startActivity(temp);
            finishAffinity();
            FirebaseDatabase.getInstance().goOffline();
            Toast.makeText(getApplicationContext(), "Logout successful!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Logout failed. Try again later!", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion
}