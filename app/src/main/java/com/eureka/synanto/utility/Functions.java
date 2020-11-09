package com.eureka.synanto.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class Functions {

    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        LinearLayout layout = (LinearLayout) toast.getView();

        if (layout.getChildCount() > 0) {
            TextView view = (TextView) layout.getChildAt(0);
            view.setGravity(Gravity.CENTER);
        }

        toast.show();
    }

    public static void test(Object o) {
        System.out.print(o);
    }

    public static SharedPreferences getSession(Context context) {
        return context.getSharedPreferences("com.eureka.synanto", context.MODE_PRIVATE);
    }
}
