package malathkar.akshay.contacts;

import android.app.Application;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.push.DeviceRegistrationResult;

import java.util.ArrayList;
import java.util.List;

public class ApplicationClass extends Application {
    public static final String APPLICATION_ID = "E7D524A2-A909-60AE-FF2E-E9C5DA28FC00";
    public static final String API_KEY = "6968FD1B-3774-486D-AC67-19F327A11ABB";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Contact> contacts;
    @Override
    public void onCreate() {
        super.onCreate();
        Backendless.setUrl(SERVER_URL);
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );
    }

}
