package com.example.fe_project_cosmeticapp.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Lớp tiện ích để xử lý bàn phím
 */
public class KeyboardUtils {

    /**
     * Hiển thị bàn phím cho một view cụ thể
     *
     * @param activity Activity hiện tại
     * @param view View cần hiển thị bàn phím
     */
    public static void showKeyboard(Activity activity, View view) {
        if (activity != null && view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    /**
     * Ẩn bàn phím
     *
     * @param activity Activity hiện tại
     * @param view View đang focus
     */
    public static void hideKeyboard(Activity activity, View view) {
        if (activity != null && view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
