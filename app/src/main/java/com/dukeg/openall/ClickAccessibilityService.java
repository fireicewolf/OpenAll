package com.dukeg.openall;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class ClickAccessibilityService extends AccessibilityService {
    private static final String TAG = "OpenAllAccessibility";

    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getSource() != null)
        {
            findAndPerformAction("同意");
            findAndPerformAction("继续");
            findAndPerformAction("跳过");
        }
        else {
            Log.i(TAG, "event.getSource() == null");
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void findAndPerformAction(String text)
    {
        // 查找当前窗口中包含“text”文字的按钮
        if(getRootInActiveWindow()==null) {
            Log.i(TAG, "getRootInActiveWindow()==null");
            return;
        }
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++)
        {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行按钮点击行为
            if (node.isClickable() && node.isEnabled())
            {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
