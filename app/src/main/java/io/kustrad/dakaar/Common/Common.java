package io.kustrad.dakaar.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.kustrad.dakaar.Model.User;
import io.kustrad.dakaar.Remotes.APIService;
import io.kustrad.dakaar.Remotes.RetrofitClient;

public class Common {

    public static User currentUser ;

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";

        else if (status.equals("1"))
            return "On Your Way";

        else
            return "Shiped";

    }


    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public  static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] into = connectivityManager.getAllNetworkInfo();
            if(into != null)
            {
                for (int i=0;i<into.length;i++)
                {
                    if (into[i].getState() == NetworkInfo.State.CONNECTED)
                    return true;
                }
            }
        }
        return false;
    }
}

