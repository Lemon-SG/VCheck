package cc.siyo.iMenu.VCheck.util;

import android.util.Log;

import java.util.Stack;

import cc.siyo.iMenu.VCheck.activity.BaseActivity;

/**
 * Created by Sylar on 14-7-11.
 */
public class ActivityStackManager {
    private Stack<BaseActivity> activityStack;
    private static ActivityStackManager manager = null;

    private ActivityStackManager() {
        activityStack = new Stack<BaseActivity>();
    }

    public static ActivityStackManager getActivityManager() {
        if (manager == null) {
            synchronized (ActivityStackManager.class) {
                if (manager == null) {
                    manager = new ActivityStackManager();
                }
            }
        }
        return manager;
    }

    public void push(BaseActivity activity) {
        if (activityStack != null) {
            activityStack.push(activity);
        }
    }

    public void remove(BaseActivity activity) {
        if (activityStack != null) {
            Log.e("TAG", activity.getClass().getSimpleName());
            activityStack.removeElement(activity);
        }
    }

    public BaseActivity pop() {
        if (activityStack != null && !activityStack.isEmpty()) {
            return activityStack.pop();
        }
        return null;
    }

    public void finishAllExceptOne(Class clazz) {
        BaseActivity activity;
        while ((activity = pop()) != null) {
            if (activity.getClass() != clazz) {
                activity.finish();
            }
        }
    }

    public void finishAll() {
        BaseActivity activity;
        Log.e("TAG", activityStack.size() + " -------- ");
        while ((activity = pop()) != null) {
            activity.finish();
        }
    }
}
