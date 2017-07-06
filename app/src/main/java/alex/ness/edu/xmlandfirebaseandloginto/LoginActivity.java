package alex.ness.edu.xmlandfirebaseandloginto;


import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity
                        implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    //Properties:
    private FirebaseAuth mAuth;
    private GoogleApiClient mApiClient; //Take away

    @BindView(R.id.btnGoogle)
    SignInButton signInButton;
    @BindView(R.id.btnRegister)
    BootstrapButton btnRegister;
    @BindView(R.id.btnLogin)
    BootstrapButton btnLogin;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.tilEmail)
    TextInputLayout tilEmail;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnVerify)
    BootstrapButton btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        GoogleApiClient.Builder builder = new  GoogleApiClient.Builder(this);

        builder.enableAutoManage(this /*Activity for onPause/onResume*/,
                                 this /*FailureListener*/);

        // Configure Google Sign In
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestProfile()
                        .requestEmail()
                        .build();

        builder.addApi(Auth.GOOGLE_SIGN_IN_API, gso);

        /*GoogleApiClient*/mApiClient = builder.build();

    }

    @NonNull
    private String getEmail() {
        return etEmail.getText().toString();
    }

    @NonNull
    private String getPassword() {
        return etPassword.getText().toString();
    }

    private boolean isEmailValid() {
        String email = getEmail();
        boolean isEmailValid = email.contains("@") && email.length() > 5;
        //Pattern emailAddress =  Patterns.EMAIL_ADDRESS;
        //return emailAddress.matcher(email).matches();
        if (!isEmailValid) {
            etEmail.setError("Invalid Email Address");
        } else {
            //removes old error if happened
            etEmail.setError(null);
        }

        return isEmailValid;
    }

    private boolean isPasswordValid() {
        String password = getPassword();
        boolean isPasswordValid = password.length() > 5;

        if (!isPasswordValid) {
            etPassword.setError("Password Must contain At least 6 Characters");
        } else {
            //removes old error if happened
            etPassword.setError(null);
        }

        return isPasswordValid;
    }

    private ProgressDialog dialog;

    //A Progress dialog contains:
    //Title, Message, Icon AND A ProgressBar
    private void showProgress(boolean show) {
        //Lazy Loading... Not initialized in onCreate/start
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Logging You In");
            dialog.setTitle("Connecting...");
        }

        if (show) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

    private void showError(Exception e) {
        showProgress(false);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        };

        Snackbar.make(etEmail, e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener).show();
    }

    private void gotoMain() {
        showProgress(false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            showError(e);
        }
    };

    OnSuccessListener<AuthResult> onSuccessListener = new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
            showProgress(false);
            btnRegister.animate()
                    .alpha(0)
                    .rotation(360)
                    .setDuration(3000)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btnLogin.setVisibility(View.GONE);
                            btnRegister.setVisibility(View.GONE);
                            etPassword.setVisibility(View.GONE);
                            etEmail.setVisibility(View.GONE);
                            tilEmail.setVisibility(View.GONE);
                            tilPassword.setVisibility(View.GONE);
                            btnVerify.setVisibility(View.VISIBLE);
                            btnVerify.animate().scaleX(2).scaleY(2);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    };

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        hideSoftKeyboard(this);
        // /isEmail valid
        //isPassword valid
        //need to check two parts in order to delete
        // previous input errors messages if were before
        if (!validateForm()) {
            return;
        }

        showProgress(true);

        //adds new user to current firebase
        mAuth.createUserWithEmailAndPassword(getEmail(), getPassword())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    @OnClick(R.id.btnLogin)
    public void login() {
        hideSoftKeyboard(this);

        if (!validateForm()) {
            return;
        }

        showProgress(true);
        //logins existent user to current firebase
        mAuth.signInWithEmailAndPassword(getEmail(), getPassword())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener((onFailureListener));
    }

    private boolean validateForm() {
        return isEmailValid() & isPasswordValid();
    }


    boolean sent = false;

    @OnClick(R.id.btnVerify)
    public void onBtnVerifyClicked() {
        //mAuth.getCurrentUser() has email and password
        // of trying to login or sign in by current user
        final FirebaseUser user = mAuth.getCurrentUser();

        if (!sent) {
            Log.d("Ness", "LoginActivity: !Sent Email Verification yet");
            if (user == null) {
                return;
            }

            //sends email for verification by tapping link in new message
            user.sendEmailVerification();
            Log.d("Ness", "LoginActivity: Sent Email Verification to Email:" + user.getEmail());
            Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show();
            sent = true;

            btnVerify.setText("Refresh");
            Log.d("Ness", "LoginActivity: Refresh");
        }
        //Sent Email Verification
        else {
            Log.d("Ness", "LoginActivity: after sent for Email Verification to Email:" + user.getEmail());
            //if user.reload() method successed  to reload into application memory
            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Ness", "LoginActivity: User was been reloaded " +
                            "successfuly into application memory");
                    if (user.isEmailVerified()) {
                        Log.d("Ness", "LoginActivity: User was been verified " +
                                "at his Email Address " + user.getEmail() + " by tapping on link there");
                        showProgress(true);
                        gotoMain();
                    } else {
                        Log.d("Ness", "LoginActivity: User's verification" +
                                " didn't arrived yet to the current 'user' variable");
                    }
                }
            });
        }
    }

    @OnClick(R.id.btnGoogle)
    public void onViewClicked() {
        //Intent... GoogleApiClient
        Intent gsIntent = Auth.GoogleSignInApi
                .getSignInIntent(/*GoogleApiClient*/mApiClient);

       //startActivityForResult
        startActivityForResult(gsIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                //From Google Account
                GoogleSignInAccount account = result.getSignInAccount();
                //To Firebase account(Credentials)
                AuthCredential authCredential = GoogleAuthProvider
                        .getCredential(account.getIdToken(), null);

                //send result to firebase
                mAuth.signInWithCredential(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                gotoMain();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e);
                    }
                });
            }

            else
            {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }
}


