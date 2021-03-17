package com.yoyo.recordapp.utils;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;

import com.yoyo.recordapp.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * renamed from OSHelper
 */
public class SystemOperatorGlobalUtils {

    private final static int MIUI = 0;
    private final static int FLYME = 1;
    private final static int COMMON = 2;
    private static final boolean isTranslucentStatusMiUi;

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";


    static {
        isTranslucentStatusMiUi = isTranslucentStatusMiUiVersion();
    }

    //判断是否是小米的 MiUi（所有版本）
    private static boolean isMiUiOS() {
        try {
            final PropertyGlobalUtils prop = PropertyGlobalUtils.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    //判断是否是支持沉浸式状态栏（以及状态栏图标变黑色）的 MiUi 版本
    private static boolean isTranslucentStatusMiUiVersion() {
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getStringMethod = sysClass.getDeclaredMethod("get", String.class);
            boolean isMiUiV6 = "V6".equals(getStringMethod.invoke(sysClass, "ro.miui.ui.version.name"));
            boolean isMiUiV7 = "V7".equals(getStringMethod.invoke(sysClass, "ro.miui.ui.version.name"));
            boolean isMiUiV8 = "V8".equals(getStringMethod.invoke(sysClass, "ro.miui.ui.version.name"));
            boolean isMiUiV9 = "V9".equals(getStringMethod.invoke(sysClass, "ro.miui.ui.version.name"));
            return isMiUiV6 | isMiUiV7 | isMiUiV8 | isMiUiV9;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 设置文字颜色
     */
    private static void setStatusBarFontIconDark(boolean dark, int type, Activity activity) {
        switch (type) {
            case MIUI:
                setMiuiUIStatusBarDarkMode(dark,activity);
                break;
            case COMMON:
                setCommonUI(activity);
                break;
            case FLYME:
                setFlymeUI(dark,activity);
                break;
        }
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param fontIconDark 状态栏字体和图标颜色是否为深色
     */
    public static void setStatusBarDarkMode(boolean fontIconDark, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarFontIconDark(fontIconDark,COMMON,activity);
        } else if (isMiUiOS()) {
            setStatusBarFontIconDark(fontIconDark,MIUI,activity);
        } else if (isFlyMeOS()) {
            setStatusBarFontIconDark(fontIconDark,FLYME,activity);
        }else {
            if (fontIconDark){
                setStatusBarColor(activity, R.color.gray_color_plus);
            }
        }
    }

    private static void setCommonUI(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

    private static void setFlymeUI(boolean dark, Activity activity) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * set status bar darkMode
     * <p>
     * MiUi 将修改MiUiWindowManager的部分LayoutParams
     * FlyMe 将调用其对应的API
     * Android 6.0 起将调用高亮状态栏模式
     *
     * @param darkMode 是否是黑色模式
     * @param activity 所要设置的activity
     */
    private static void setMiuiUIStatusBarDarkMode(boolean darkMode, Activity activity) {
        if (isTranslucentStatusMiUi) {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);

                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), darkMode ? darkModeFlag : 0, darkModeFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //MiUi 和 FlyMe 均再进行高亮模式设置，为了避免系统版本在Android M起切换黑色模式无效的问题  
        setLightStatusBar(darkMode, activity);
    }

    /**
     * Android 6.0起设置原生状态栏高亮模式（高亮模式下状态栏文字及图标将变成灰色）
     *
     * @param lightMode 是否是高亮模式
     * @param activity  所要设置的activity
     */
    private static void setLightStatusBar(boolean lightMode, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int vis = activity.getWindow().getDecorView().getSystemUiVisibility();
            if (lightMode) vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            else vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            activity.getWindow().getDecorView().setSystemUiVisibility(vis);
        }
    }

    //判断是否是魅族的 FlyMe OS  
    private static boolean isFlyMeOS() {
        /* 获取魅族系统操作版本标识*/
        String meiZuFlyMeOSFlag = getSystemProperty("ro.build.display.id", "");
        if (TextUtils.isEmpty(meiZuFlyMeOSFlag)) {
            return false;
        } else return meiZuFlyMeOSFlag.contains("flyme") || meiZuFlyMeOSFlag.toLowerCase().contains("flyme");
    }

    /**
     * 修改状态栏颜色，支持5.0以上版本
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, @ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        }
    }

    /**
     * 获取系统属性
     *
     * @param key          ro.build.display.id
     * @param defaultValue 默认值
     * @return 系统操作版本标识
     */
    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
            return null;
        }
    }


}