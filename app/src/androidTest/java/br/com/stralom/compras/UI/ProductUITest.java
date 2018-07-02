package br.com.stralom.compras.UI;

import android.app.Activity;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import br.com.stralom.compras.MainActivity;
import br.com.stralom.compras.R;
import br.com.stralom.dao.CategoryDAO;
import br.com.stralom.dao.ProductDAO;
import br.com.stralom.entities.Category;
import br.com.stralom.entities.Product;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.stralom.compras.UI.matchers.CustomMatcher.withError;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;



@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductUITest {
    private static String DEFAULTPRODUCTNAME = "Register Product Test";
    private Activity activity;
    private static boolean initialized = false;
    private static ProductDAO productDAO;
    private static CategoryDAO categoryDAO;
    private static Product product;
    private static Category category;
    @Before
    public void init(){
        goToProductTab();
        activity = activityActivityTestRule.getActivity();
        if(!initialized){
            initialized = true;
            productDAO = new ProductDAO(activity);
            categoryDAO = new CategoryDAO(activity);

            category = categoryDAO.add("Product", "Product", R.drawable.cherries);
            product = productDAO.add("Product", 10,category );
        }
    }


    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);




    @Test
    public void TestOpenRegisterProductDialog(){
        onView(ViewMatchers.withId(R.id.product_btn_addNew)).perform(click());
        onView(withId(R.id.product_dialog_mainLayout))
                .check(matches(isDisplayed()));
    }

    @Test
    public void TestAddingProducts() {
        registerProduct(DEFAULTPRODUCTNAME,"10");
        onView(withText(R.string.toast_produc_register)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void TestAddingProductsAlreadyRegistered(){
        registerProduct(DEFAULTPRODUCTNAME,"10");
        registerProduct(DEFAULTPRODUCTNAME,"10");

        onView(withText(R.string.toast_product_alreadyRegistered)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void TestProductValidationEmptyPrice(){
        registerProduct("Test Validation","");
        String errorMsg = activity.getResources().getString(R.string.product_validation_price);
        //onView(withId(R.id.product_dialog_mainLayout)).check((matches(hasErrorText(errorMsg))));
        onView(withId(R.id.form_productPrice)).check(matches(withError(errorMsg)));
    }

    @Test
    public void TestProductValidationEmptyName(){
        registerProduct("","12");
        String errorMsg = activity.getResources().getString(R.string.product_validation_name);
        onView(withId(R.id.form_productName)).check(matches(withError(errorMsg)));
    }

    @Test
    public void TestProductValidationNameToLong(){
        registerProduct("This product name is greater than the max chars permited","12");
        String errorMsg = activity.getResources().getString(R.string.validation_maxLenght);
        //onView(withId(R.id.product_dialog_mainLayout)).check((matches(hasErrorText(errorMsg))));
        onView(withId(R.id.form_productName)).check(matches(withError(errorMsg)));
    }

    public static void registerProduct(String productName, String productPrice) {
        goToProductTab();
        onView(withText("Produtos")).perform(click());
        onView(withId(R.id.product_btn_addNew)).perform(click());
        onView(withId(R.id.form_productName)).perform(replaceText(productName));
        onView(withId(R.id.form_productPrice)).perform(replaceText(productPrice));
        onView(withId(R.id.form_productCategory)).perform(click());
        onData(anything()).atPosition(1).inRoot(isPlatformPopup()).perform(click());
        onView(withText(R.string.save)).perform(click());

    }

    public static void goToProductTab(){
        onView(withText("Produtos")).perform(click());
    }

}