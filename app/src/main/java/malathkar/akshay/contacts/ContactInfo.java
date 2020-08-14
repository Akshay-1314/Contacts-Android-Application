package malathkar.akshay.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.material.navigation.NavigationView;


public class ContactInfo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText etName,etMail,etTel;
    TextView tvChar,tvName;
    Button btnSubmit;
    ImageView ivCall,ivMail,ivEdit,ivDelete;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    boolean edit=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contact Info");
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.bringToFront();

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.login).setVisible(false);
        menu.findItem(R.id.register).setVisible(false);
        menu.findItem(R.id.createContacts).setVisible(false);
        menu.findItem(R.id.logout).setVisible(false);
        menu.findItem(R.id.nav_home).setVisible(false);


        navigationView.setNavigationItemSelectedListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etName = findViewById(R.id.etName);
        etMail = findViewById(R.id.etMail);
        etTel = findViewById(R.id.etTel);

        tvChar = findViewById(R.id.tvChar);
        tvName = findViewById(R.id.tvName);

        ivEdit = findViewById(R.id.ivEdit);
        ivCall = findViewById(R.id.ivCall);
        ivDelete = findViewById(R.id.ivDelete);
        ivMail = findViewById(R.id.ivMail);

        btnSubmit = findViewById(R.id.btnSubmit);

        etName.setVisibility(View.GONE);
        etTel.setVisibility(View.GONE);
        etMail.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        final int index = getIntent().getIntExtra("index",0);

        etName.setText(ApplicationClass.contacts.get(index).getName());
        etMail.setText(ApplicationClass.contacts.get(index).getEmail());
        etTel.setText(ApplicationClass.contacts.get(index).getNumber());

        tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0)+"");
        tvName.setText(ApplicationClass.contacts.get(index).getName());

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "tel:" + ApplicationClass.contacts.get(index).getNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(intent);
            }
        });
        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL,ApplicationClass.contacts.get(index).getEmail());
                startActivity(intent.createChooser(intent,"Send mail to " + ApplicationClass.contacts.get(index).getName()));
            }
        });
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit= !edit;
                if(edit) {
                    etName.setVisibility(View.VISIBLE);
                    etTel.setVisibility(View.VISIBLE);
                    etMail.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    etName.setVisibility(View.GONE);
                    etTel.setVisibility(View.GONE);
                    etMail.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);
                }
            }
        });
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactInfo.this);
                builder.setMessage("Are you sure you want to delete the contact?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showProgress(true);
                                tvLoad.setText("Deleting contact...Please wait...");
                                Backendless.Data.of(Contact.class).remove(ApplicationClass.contacts.get(index), new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long response) {
                                        ApplicationClass.contacts.remove(index);
                                        Toast.makeText(ContactInfo.this, "Contact deleted successfully!", Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        ContactInfo.this.finish();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(ContactInfo.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setTitle("Delete");
                builder.create().show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etName.getText().toString().isEmpty() || etMail.getText().toString().isEmpty() || etTel.getText().toString().isEmpty())
                {
                    Toast.makeText(ContactInfo.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ApplicationClass.contacts.get(index).setName(etName.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setEmail(etMail.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setNumber(etTel.getText().toString().trim());
                    showProgress(true);
                    tvLoad.setText("Updating contact... Please wait...");
                    Backendless.Persistence.save(ApplicationClass.contacts.get(index), new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {
                            tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0)+"");
                            tvName.setText(ApplicationClass.contacts.get(index).getName());
                            Toast.makeText(ContactInfo.this, "Contact successfully updated!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(ContactInfo.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
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
            case R.id.list_of_contacts:
                ContactInfo.this.finish();
                break;
            case R.id.exit:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                break;
            case R.id.account:
                startActivity(new Intent(ContactInfo.this,Account.class));
                ContactInfo.this.finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}