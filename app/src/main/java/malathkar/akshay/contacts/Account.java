package malathkar.akshay.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class Account extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextInputEditText etName,etEmail;
    Button btnName,btnEmail,btnDeleteAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.bringToFront();

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.login).setVisible(false);
        menu.findItem(R.id.register).setVisible(false);

        navigationView.setNavigationItemSelectedListener(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnName = findViewById(R.id.btnName);
        btnEmail = findViewById(R.id.btnEmail);
        btnDeleteAcc = findViewById(R.id.btnDeleteAcc);

        etName.setVisibility(View.GONE);
        etEmail.setVisibility(View.GONE);

        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnName.setText("UPDATE");
                etName.setVisibility(View.VISIBLE);
                if(etName.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(Account.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String name = etName.getText().toString().trim();

                    ApplicationClass.user.setProperty("name",name);

                    showProgress(true);
                    tvLoad.setText("Updating...Please wait...");
                    Backendless.UserService.update(ApplicationClass.user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            showProgress(false);
                            etName.setText("");
                            etName.setVisibility(View.GONE);
                            btnName.setText("EDIT NAME");
                            Toast.makeText(Account.this, "Your name has been updated!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            showProgress(false);
                            Toast.makeText(Account.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEmail.setText("UPDATE");
                etEmail.setVisibility(View.VISIBLE);
                if(etEmail.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(Account.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String email = etEmail.getText().toString().trim();

                    ApplicationClass.user.setEmail(email);

                    showProgress(true);
                    tvLoad.setText("Updating...Please wait...");
                    Backendless.UserService.update(ApplicationClass.user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            showProgress(false);
                            etEmail.setText("");
                            etEmail.setVisibility(View.GONE);
                            btnEmail.setText("EDIT EMAIL");
                            Toast.makeText(Account.this, "Your email has been updated!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            showProgress(false);
                            Toast.makeText(Account.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
                builder.setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account? This cannot be undone!")
                        .setNegativeButton("Cancel",null)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String userObjectId = UserIdStorageFactory.instance().getStorage().get();
                                showProgress(true);
                                tvLoad.setText("Deleting your account...Please wait...");
                                Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(BackendlessUser response) {
                                        Backendless.Data.of(BackendlessUser.class).remove(response, new AsyncCallback<Long>() {
                                            @Override
                                            public void handleResponse(Long response) {
                                                startActivity(new Intent(Account.this,Login.class));
                                                Toast.makeText(Account.this, "Deleted account successfully!", Toast.LENGTH_SHORT).show();
                                                Account.this.finish();
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                showProgress(false);
                                                Toast.makeText(Account.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        showProgress(false);
                                        Toast.makeText(Account.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                builder.create().show();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_home:
                startActivity(new Intent(Account.this,MainActivity.class));
                Account.this.finish();
                break;
            case R.id.list_of_contacts:
                startActivity(new Intent(Account.this,ContactList.class));
                Account.this.finish();
                break;
            case R.id.createContacts:
                startActivity(new Intent(Account.this,NewContact.class));
                Account.this.finish();
                break;
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setNegativeButton("Cancel",null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showProgress(true);
                                tvLoad.setText("Logging you out...Please wait...");
                                Backendless.UserService.logout(new AsyncCallback<Void>() {
                                    @Override
                                    public void handleResponse(Void response) {
                                        startActivity(new Intent(Account.this,Login.class));
                                        Account.this.finish();
                                        Toast.makeText(Account.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        showProgress(false);
                                        Toast.makeText(Account.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                builder.create().show();
                break;
            case R.id.exit:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}