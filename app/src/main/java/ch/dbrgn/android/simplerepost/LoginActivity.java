package ch.dbrgn.android.simplerepost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity {

    // Log tag
    private static final String LOG_TAG = LoginActivity.class.getName();

    // URL constants
    private static final String AUTH_URL = "https://instagram.com/oauth/authorize/"
            + "?client_id=" + Config.IG_CLIENT_ID
            + "&redirect_uri=" + Config.IG_REDIRECT_URI_ENCODED
            + "&response_type=token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get webview
        final WebView webView = (WebView) findViewById(R.id.login_webview);

        // Set custom WebViewClient
        webView.setWebViewClient(getWebViewClient());

        // Don't offer to save password
        WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);

        // Load login URL
        webView.loadUrl(AUTH_URL);
    }

    /**
     * By default, redirects cause jump from WebView to default
     * system browser. Overriding url loading allows the WebView
     * to load the redirect into this screen. Also, capture access token.
     */
    private WebViewClient getWebViewClient() {
        final LoginActivity instance = this;

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(LOG_TAG, "Loading " + url);

                if (url.startsWith(Config.IG_REDIRECT_URI) && url.contains("access_token")) {
                    try {
                        // Parse fragment, get access token
                        URI uri = new URI(url);
                        final String fragment = uri.getFragment();
                        final String accessToken = instance.parseFragment(fragment);

                        // Store access token and proceed to main activity
                        storeAccessToken(accessToken);
                        launchMainActivity();
                    } catch (URISyntaxException e) {
                        // This should never happen
                        e.printStackTrace();
                    }
                } else {
                    view.loadUrl(url);
                }
                return false;
            }
        };
    }

    /**
     * Parses the redirect URI fragment and returns the access token.
     */
    private String parseFragment(String fragment) {
        final String[] fragmentParts = fragment.split("=");
        assert fragmentParts.length == 2;
        final String accessToken = fragmentParts[1];
        Log.d(LOG_TAG, "Access token is " + accessToken);
        return accessToken;
    }

    /**
     * Store access token in shared preferences file.
     */
    private void storeAccessToken(String accessToken) {
        SharedPreferences settings = getSharedPreferences(Config.SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("AccessToken", accessToken);
        editor.commit();
    }

    /**
     * Launch the main activity.
     */
    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}