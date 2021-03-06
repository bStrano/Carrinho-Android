package br.com.stralom.compras.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import androidx.databinding.ObservableArrayList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.constraintlayout.widget.Group;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.stralom.compras.adapters.ProductAdapter;
import br.com.stralom.compras.R;
import br.com.stralom.compras.dao.ProductDAO;
import br.com.stralom.compras.entities.ItemStock;
import br.com.stralom.compras.entities.Product;
import br.com.stralom.compras.entities.Stock;
import br.com.stralom.compras.helper.exceptions.InvalidElementForm;
import br.com.stralom.compras.helper.forms.ItemStockForm;
import br.com.stralom.compras.interfaces.ItemClickListener;


public class StockRegistration extends AppCompatActivity  {
    private static final String TAG = "STOCK_REGISTRATION";
    private Stock stock ;

    private Toolbar toolbar;
    private RecyclerView productsList;
    private SearchView productsSearchView;

    private Group selectedProductGroup;
    private TextView selectedProductView;
    private Group unselectedProductGroup;
    private TextView selectProductTitle;

    private ObservableArrayList<Product> products ;
    private Product selectedProduct;

    private ProductAdapter productAdapter;
    private ItemStockForm itemStockForm;

    private ProductDAO productDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_registration);

        //Temporary
        stock = new Stock((long) 1);

        productDAO = new ProductDAO(this);

        itemStockForm = new ItemStockForm(this);

        toolbar = findViewById(R.id.registration_toolbar);
        productsList = findViewById(R.id.stock_registration_products);
        productsSearchView = findViewById(R.id.stock_registration_search);
        selectedProductGroup = findViewById(R.id.stock_registration_group_selectedProduct);
        unselectedProductGroup = findViewById(R.id.stock_registration_group_productList);
        selectedProductView = findViewById(R.id.stock_registration_selectedProduct);
        selectProductTitle = findViewById(R.id.stock_regidtration_selectProductTitle);


        Intent intent = getIntent();
        ArrayList<Product> productsExtra = intent.getExtras().getParcelableArrayList("products");
        //products = (ArrayList<Product>) productDAO.getAllOrderedByNameWithoutItemStock();

        toolbar.setTitle(R.string.stock_register);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        products = new ObservableArrayList<>();
        products.addAll(productsExtra);
        for (int id: selectedProductGroup.getReferencedIds()) {
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unSelectProduct();
                }
            });
        }


        productAdapter = new ProductAdapter((ObservableArrayList<Product>) products, this, new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                selectedProduct = products.get(position);
                selectProduct();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        productsList.setAdapter(productAdapter);
        productsList.setLayoutManager(new LinearLayoutManager(this));
        productsList.setHasFixedSize(true);



        productsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void selectProduct(){
        unselectedProductGroup.setVisibility(View.GONE);
        selectedProductGroup.setVisibility(View.VISIBLE);
        selectedProductView.setText(selectedProduct.getName());
        selectProductTitle.setTextColor(Color.BLACK);
    }

    public void unSelectProduct(){
        unselectedProductGroup.setVisibility(View.VISIBLE);
        selectedProductGroup.setVisibility(View.GONE);
        selectedProductView.setText("");
        selectedProduct = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.secundary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.registration_save:
                try {
                    Product product = itemStockForm.getValidItemStock(selectedProduct);
//                    itemStock.setStock(stock);
                    productDAO.add(product,null);
//                    Long id = itemStockDAO.add(itemStockDAO.getContentValues(itemStock));
//                    itemStock.setId(id);
                    Intent intent = new Intent();
                    intent.putExtra("product", product);
                    setResult(RESULT_OK,intent);

                    finish();
                } catch (InvalidElementForm invalidElementForm) {
                    if(invalidElementForm.getErrorCode() == ItemStockForm.EMPTY_PRODUCT_ERRORCODE){
                        selectProductTitle.setTextColor(Color.RED);
                    }
                    Log.w(TAG,invalidElementForm);
                } catch ( SQLiteConstraintException e){
                    if(e.getMessage().contains("code 2067")){
                        Toast.makeText(this,R.string.error_product_alreadyRegistered, Toast.LENGTH_LONG).show();
                    } else {
                        e.printStackTrace();
                    }
                }

        }
        return true;
    }


}
