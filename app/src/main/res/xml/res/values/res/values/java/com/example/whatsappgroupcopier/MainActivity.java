package com.example.whatsappgroupcopier;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 50, 35, 35);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("📱 ناسخ مجموعات واتساب");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(18, 140, 126));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 35);

        status = new TextView(this);
        status.setText(
                "جاهز.\n\n" +
                "فعّل خدمة إمكانية الوصول أولًا، " +
                "ثم افتح واتساب واختر الجروب."
        );
        status.setTextSize(17);
        status.setTextColor(Color.DKGRAY);
        status.setGravity(Gravity.CENTER);
        status.setPadding(10, 10, 10, 30);

        Button accessibilityButton = new Button(this);
        accessibilityButton.setText("⚙️ تفعيل خدمة الوصول");
        accessibilityButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            } catch (Exception e) {
                status.setText("تعذر فتح إعدادات إمكانية الوصول.");
            }
        });

        Button whatsappButton = new Button(this);
        whatsappButton.setText("🟢 فتح واتساب");
        whatsappButton.setOnClickListener(v -> openWhatsApp());

        Button copyButton = new Button(this);
        copyButton.setText("📋 نسخ النص المجموع");
        copyButton.setOnClickListener(v -> {
            String text = CaptureAccessibilityService.getCollectedText();

            if (text.isEmpty()) {
                status.setText("لم يتم جمع أي نص حتى الآن.");
            } else {
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager)
                                getSystemService(CLIPBOARD_SERVICE);

                android.content.ClipData data =
                        android.content.ClipData.newPlainText(
                                "WhatsApp",
                                text
                        );

                clipboard.setPrimaryClip(data);
                status.setText("تم نسخ النص إلى الحافظة ✅");
            }
        });

        Button clearButton = new Button(this);
        clearButton.setText("🗑️ مسح النص");
        clearButton.setOnClickListener(v -> {
            CaptureAccessibilityService.clearCollectedText();
            status.setText("تم مسح النص المجموع.");
        });

        layout.addView(title);
        layout.addView(status);
        layout.addView(accessibilityButton);
        layout.addView(whatsappButton);
        layout.addView(copyButton);
        layout.addView(clearButton);

        setContentView(layout);
    }

    private void openWhatsApp() {
        try {
            Intent intent = getPackageManager()
                    .getLaunchIntentForPackage("com.whatsapp");

            if (intent != null) {
                startActivity(intent);
                status.setText(
                        "تم فتح واتساب.\n\n" +
                        "اختر الجروب المطلوب."
                );
            } else {
                status.setText("واتساب غير مثبت على الهاتف.");
            }
        } catch (Exception e) {
            status.setText("تعذر فتح واتساب.");
        }
    }
}
