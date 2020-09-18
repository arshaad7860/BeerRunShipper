package com.example.beerrunshipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beerrunshipper.Common.Common;
import com.example.beerrunshipper.Model.RestaurantModel;
import com.example.beerrunshipper.Model.ShipperUserModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static int APP_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private DatabaseReference serverRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener!=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //delete data offline
       /* Paper.init(this);
        Paper.book().delete(Common.TRIP_START);
        Paper.book().delete(Common.SHIPPING_ORDER_DATA);*/

    }

    private void init() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        //serverRef = FirebaseDatabase.getInstance().getReference(Common.SHIPPER_REF);
        firebaseAuth =FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        listener=firebaseAuthLocal ->{
            FirebaseUser user =firebaseAuthLocal.getCurrentUser();
            if (user!=null)
            {
                Paper.init(this);
                String jsonEncode =Paper.book().read(Common.RESTAURANT_SAVE);
                RestaurantModel restaurantModel = new Gson().fromJson(jsonEncode,
                        new TypeToken<RestaurantModel>(){}.getType());
                if (restaurantModel!=null)
                {
                    checkServerUserFromFirebase(user,restaurantModel);
                }
                else
                {
                    startActivity(new Intent(MainActivity.this,RestaurantListActivity.class));
                    finish();
                }
            }
            else
            {
                phoneLogin();
            }
        };
    }
    private void checkServerUserFromFirebase(FirebaseUser user, RestaurantModel restaurantModel) {
        dialog.show();

        serverRef= FirebaseDatabase.getInstance()
                .getReference(Common.RESTAURANT_REF)
                .child(restaurantModel.getUid())
                .child(Common.SHIPPER_REF);

        serverRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            ShipperUserModel userModel = dataSnapshot.getValue(ShipperUserModel.class);
                            if (userModel.isActive())
                            {
                                goToHomeActivity(userModel,restaurantModel);
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "You must be allowed to use app by management please wait while we allow you access. login after 24hrs if not accepted immediately ", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void goToHomeActivity(ShipperUserModel userModel, RestaurantModel restaurantModel) {
        dialog.dismiss();
        Common.currentRestaurant = restaurantModel;
        Common.currentShipperUser =userModel;
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder().setAvailableProviders(providers)
                .setLogo(R.drawable.brunlogo)
                .setTheme(R.style.LoginTheme)
                .build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==APP_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode== RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}