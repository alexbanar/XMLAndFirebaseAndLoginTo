package alex.ness.edu.xmlandfirebaseandloginto;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

/**
 * Created by Alex on 22-Jun-17.
 */

//Registered in the manifest tag in the name attribute.
//Init struff once for an app
public class AppManager extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        //Font Awsome fa_android
        //http://fontawesome.io/cheatsheet/
        TypefaceProvider.registerDefaultIconSets();
    }

    //private String does = "";
}
