package com.ushop.ushopapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    private TextView aboutTextView, privacyTextView, termsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getSupportActionBar().setTitle("About, Privacy policy, T&Cs");

        aboutTextView = findViewById(R.id.aboutTextView);
        privacyTextView = findViewById(R.id.privacyPolicyTextView);
        termsTextView = findViewById(R.id.termsTextView);

        String about_html = getString(R.string.about_html);
        String privacy_policy_html = getString(R.string.privacy_policy_html);
        String terms_html = getString(R.string.terms_and_conditions_html);

        Spanned about_spanned = Html.fromHtml(about_html);
        aboutTextView.setText(about_spanned);

        Spanned privacy_policy_spanned = Html.fromHtml(privacy_policy_html);
        privacyTextView.setText(privacy_policy_spanned);

        Spanned terms_spanned = Html.fromHtml(terms_html);
        termsTextView.setText(terms_spanned);
    }
}
