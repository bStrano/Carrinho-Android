package br.com.stralom.compras.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Color;


import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import br.com.stralom.compras.adapters.TabFragmentPagerAdapter;
import br.com.stralom.compras.R;
import br.com.stralom.compras.dao.ProductDAO;
import br.com.stralom.compras.dao.RecipeDAO;
import br.com.stralom.compras.dao.UserDAO;
import br.com.stralom.compras.entities.Category;
import br.com.stralom.compras.entities.Product;
import br.com.stralom.compras.entities.Profile;
import br.com.stralom.compras.entities.Recipe;
import br.com.stralom.compras.entities.User;
import br.com.stralom.compras.helper.DataViewModel;
import br.com.stralom.compras.listerners.FirebaseGetDataListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    public ArrayList<Product> productList;
    public ArrayList<Recipe> recipeList;
    private ArrayList<Profile> profiles;
    private User userInfo;
    private static final int NUMBER_ASYNCTASK = 2;
    private static int counter = 0;
    private HashMap<String, ArrayList> data;
    private final int MAX_CART = 3;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = this.mAuth.getCurrentUser();

         sharedPreferences = getSharedPreferences("profiles", Context.MODE_PRIVATE);

        productList = new ArrayList<>();
        recipeList = new ArrayList<>();
        profiles = new ArrayList<>();

        Log.d(TAG,user.getUid());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();

        data = new HashMap<>();
        mTabLayout = findViewById(R.id.mTabLayout);
        mViewPager = findViewById(R.id.mViewPager);

        mViewPager.setOffscreenPageLimit(2);


        final ProductDAO productDAO = new ProductDAO(this);
        final RecipeDAO recipeDAO = new RecipeDAO(this);
        UserDAO userDAO = new UserDAO();


        String selectedProfile =  sharedPreferences.getString(getString(R.string.sharedPreferences_selectedProfile), null);

//        userDAO.getAll(user, new FirebaseGetDataListener() {
//            @Override
//            public void handleListData(List objects) {
//                final ArrayList<User> userInfoObjects = (ArrayList<User>) objects;
//
//                userInfo = userInfoObjects.get(0);
//                Log.d(TAG,userInfo.toString());
//                asyncTaskCompleted("userInfo", userInfoObjects);
//            }
//
//            @Override
//            public void onHandleListDataFailed() {
//
//            }
//
//            @Override
//            public void getObject() {
//
//            }
//        });
        for(int i = 0 ; i < MAX_CART ; i++){
            final String profileIdentifier = (i+1)+"_" +sharedPreferences.getString(getString(R.string.sharedPreferences_userUid), null);
            final boolean activeProfile = profileIdentifier.equals(selectedProfile);
            Log.d("TESTE",profileIdentifier + " / " + selectedProfile + " / " + activeProfile);
            final int finalI = i;

            productDAO.getAllOrderedByName(profileIdentifier,new FirebaseGetDataListener() {
                @Override
                public void handleListData(List objects) {
                    final ArrayList<Product> products = (ArrayList<Product>) objects;

                    recipeDAO.getAll(profileIdentifier,new FirebaseGetDataListener() {
                        @Override
                        public void handleListData(List objects) {
                            recipeList = (ArrayList<Recipe>) objects;

                            profiles.add(new Profile(profileIdentifier,"Perfil "  + (finalI +1), activeProfile,false, products,recipeList));
                            asyncTaskCompleted("recipes", (ArrayList) objects);
                        }

                        @Override
                        public void onHandleListDataFailed() {

                        }

                        @Override
                        public void getObject() {

                        }
                    }, productList);

                    Log.d(TAG, String.valueOf(productList));
                    asyncTaskCompleted("products", (ArrayList) objects);
                }

                @Override
                public void onHandleListDataFailed() {
                }

                @Override
                public void getObject() {
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Teste", Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public synchronized void asyncTaskCompleted(String key, ArrayList value) {
        counter++;
        data.put(key, value);
        Log.d(TAG,counter + " / " + NUMBER_ASYNCTASK);
        if (counter == NUMBER_ASYNCTASK) {
            Collections.sort(profiles);
            for(Profile profile : profiles){
                Log.d("TESTE", profile.getId() + " / " + sharedPreferences.getString(getString(R.string.sharedPreferences_selectedProfile),null));
                Log.d("TESTE", String.valueOf(profile.getId().equals(sharedPreferences.getString(getString(R.string.sharedPreferences_selectedProfile),null))));
                if( profile.getId().equals(sharedPreferences.getString(getString(R.string.sharedPreferences_selectedProfile),null))){
                    this.productList = profile.getProducts();
                    this.recipeList = profile.getRecipes();
                }
            }
            mViewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.tab_titles), data));
            mTabLayout.setupWithViewPager(mViewPager);
            counter = 0;

//            profiles.add(new Profile(productList,recipeList));
//            profiles.add(new Profile(productList,recipeList));

        }


        //start new activity
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                // Handle the camera action
                break;
            case R.id.nav_gallery:

                break;
            case R.id.nav_slideshow:

                break;
            case R.id.nav_manage:

                break;
            case R.id.nav_share:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putParcelableArrayListExtra("profiles",profiles);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_logout:

                mAuth.signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("selectedProfile");
                editor.apply();
                for (UserInfo user : user.getProviderData()) {
                    if (user.getProviderId().equals("facebook.com")) {
                        LoginManager.getInstance().logOut();
                    } else if (user.getProviderId().equals("google.com")) {
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .build();
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                        mGoogleSignInClient.signOut();
                    }
                }


                Intent logoutIntent = new Intent(this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

