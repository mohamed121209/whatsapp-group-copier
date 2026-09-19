package com.example.whatsappgroupcopier;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.LinkedHashSet;
import java.util.Set;

public class CaptureAccessibilityService extends AccessibilityService {

    private static final Set<String> collectedText =
            new LinkedHashSet<>();

    private static final Object LOCK = new Object();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event == null) {
            return;
        }

        CharSequence packageName = event.getPackageName();

        if (packageName == null ||
                !"com.whatsapp".contentEquals(packageName)) {
            return;
        }

        AccessibilityNodeInfo root = getRootInActiveWindow();

        if (root == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        collectText(root, builder);

        String result = builder.toString().trim();

        if (!result.isEmpty()) {
            synchronized (LOCK) {
                collectedText.add(result);
            }
        }

        root.recycle();
    }

    private void collectText(
            AccessibilityNodeInfo node,
            StringBuilder builder) {

        if (node == null) {
            return;
        }

        CharSequence text = node.getText();

        if (text != null) {
            String value = text.toString().trim();

            if (!value.isEmpty()) {
                builder.append(value).append("\n");
            }
        }

        CharSequence description =
                node.getContentDescription();

        if (description != null) {
            String value =
                    description.toString().trim();

            if (!value.isEmpty()) {
                builder.append(value).append("\n");
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {

            AccessibilityNodeInfo child =
                    node.getChild(i);

            if (child != null) {
                collectText(child, builder);
                child.recycle();
            }
        }
    }

    @Override
    public void onInterrupt() {
        // لا يوجد إجراء خاص عند المقاطعة.
    }

    public static String getCollectedText() {

        synchronized (LOCK) {

            StringBuilder result =
                    new StringBuilder();

            for (String item : collectedText) {
                result.append(item)
                        .append("\n\n");
            }

            return result.toString().trim();
        }
    }

    public static void clearCollectedText() {

        synchronized (LOCK) {
            collectedText.clear();
        }
    }
}
