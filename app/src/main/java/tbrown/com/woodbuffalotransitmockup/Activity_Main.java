package tbrown.com.woodbuffalotransitmockup;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import tbrown.com.woodbuffalotransitmockup.viewpagers.ViewPageAdapter;
import tbrown.com.woodbuffalotransitmockup.widgets.SlidingTabLayout;

public class Activity_Main extends ActionBarActivity implements View.OnClickListener {
    // When using Appcompat support library you need to extend Main Activity to ActionBarActivity.

    private Toolbar toolbar;                              // Declaring the Toolbar Object
    private FloatingActionButton fab;

    private MenuItem faveSelected;                        // Declare favourite button in toolbar
    private MenuItem faveUnSelected;

    private SharedPreferences favourites;
    String sharedPrefs = "My Favourite Stops and Routes";

    ViewPager pager;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Favourites","Nearby"};
    int Numboftabs = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates the Toolbar and sets it as the Toolbar for the activity
        setupToolbar();

        // Creating an adapter that will connect to the ViewPager Container in order to
        //   supply page fragmenents on demand
        setupViewPager();

        // Implementing a tab bar below the tool bar, that can slide
        setupTabs();

        // Implement Floating Action Button
        setupFAB();

        favourites = getSharedPreferences(sharedPrefs,MODE_PRIVATE);
    }

    private void setupToolbar() {
        // Creating the Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setTitle("");
        toolbar.setTitle("Woosh Mobile"); toolbar.setTitleTextColor(getResources().getColor(R.color.ColorToolbarTitle));
        toolbar.setLogo(R.drawable.ic_bus);
    }

    private void setupViewPager() {
        // Creating an adapter that will connect to the ViewPager Container in order to
        //   supply page fragmenents on demand
        adapter = new ViewPageAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Creating a View Pager which acts as dynamic view container.
        //   Depending on the current tab, a different fragment will be supplied to this area of the screen
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
    }

    private void setupTabs() {
        // Implementing a tab bar below the tool bar, that can slide
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);
    }

    private void setupFAB() {
        fab = (FloatingActionButton) findViewById(R.id.fab_to_alternate);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // start the alternate activity when the floating action button is clicked
        finish(); // destroys current activity
        startActivity(new Intent("tbrown.com.woodbuffalotransitmockup.ALTERNATE"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        faveSelected = menu.findItem(R.id.action_favourite_selected);
        faveUnSelected = menu.findItem(R.id.action_favourite_unselected);

        faveSelected.setVisible(false);
        faveUnSelected.setVisible(true);
    return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        SharedPreferences.Editor editor = favourites.edit();
        switch (id) {
            case R.id.action_favourite_selected:
                toggleFavourites();
                editor.clear();
                editor.apply();
                break;
            case R.id.action_favourite_unselected:
                toggleFavourites();
                editor.clear();
                editor.apply();
                break;
        }
            return super.onOptionsItemSelected(item);
    }

    private void toggleFavourites() {
        // Toggle the favourites icon (star) located in the tool bar between
        // selected and unselected

        boolean isSelected = faveSelected.isVisible();
        faveSelected.setVisible(!isSelected);
        faveUnSelected.setVisible(isSelected);
    }


}