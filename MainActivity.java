package ro.example.paginaurmatoare;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {
    private static final String COURSE_URL =
        "https://cursuri.agenti-asigurari.ro/mod/lesson/view.php?id=3560&pageid=19849&startlastseen=yes";
    private static final long INTERVAL_MS = 9 * 60 * 1000L;

    private WebView webView;
    private TextView statusView;
    private TextView timerView;
    private Button startButton;
    private Button stopButton;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;
    private long nextClickAt = 0L;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (!running) return;
            long remaining = Math.max(0, nextClickAt - System.currentTimeMillis());
            timerView.setText("Următoarea apăsare în: " + formatTime(remaining));
            if (remaining == 0) {
                clickNextPage();
                nextClickAt = System.currentTimeMillis() + INTERVAL_MS;
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        TextView heading = new TextView(this);
        heading.setText("Pagina următoare – automat");
        heading.setTextSize(20);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setTextColor(Color.BLACK);
        heading.setPadding(16, 14, 16, 8);
        root.addView(heading);

        statusView = new TextView(this);
        statusView.setText("Deschide cursul și autentifică-te. Automatizarea este oprită.");
        statusView.setTextSize(14);
        statusView.setTextColor(Color.DKGRAY);
        statusView.setPadding(16, 4, 16, 8);
        root.addView(statusView);

        timerView = new TextView(this);
        timerView.setText("Interval: 9 minute");
        timerView.setTextSize(16);
        timerView.setTypeface(null, Typeface.BOLD);
        timerView.setTextColor(Color.BLACK);
        timerView.setPadding(16, 6, 16, 10);
        root.addView(timerView);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setPadding(12, 4, 12, 8);
        buttons.setGravity(Gravity.CENTER);

        startButton = new Button(this);
        startButton.setText("START");
        buttons.addView(startButton, new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        stopButton = new Button(this);
        stopButton.setText("STOP");
        stopButton.setEnabled(false);
        buttons.addView(stopButton, new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        root.addView(buttons);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        root.addView(webView, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(root);
        webView.loadUrl(COURSE_URL);

        startButton.setOnClickListener(v -> startAutomation());
        stopButton.setOnClickListener(v -> stopAutomation());
    }

    private void startAutomation() {
        if (running) return;
        running = true;
        nextClickAt = System.currentTimeMillis() + INTERVAL_MS;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        statusView.setText("Automatizare pornită. Caută «Pagina următoare» la fiecare 9 minute.");
        // Keep the display awake while this screen is in the foreground.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler.removeCallbacks(ticker);
        handler.post(ticker);
    }

    private void stopAutomation() {
        running = false;
        handler.removeCallbacks(ticker);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        timerView.setText("Automatizare oprită.");
        statusView.setText("Automatizarea este oprită.");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void clickNextPage() {
        // Match visible links/buttons by their rendered text, including nested spans.
        String js = "(function(){"
            + "const wanted='pagina următoare';"
            + "const els=Array.from(document.querySelectorAll('a,button,input[type=button],input[type=submit],[role=button]'));"
            + "const norm=s=>(s||'').replace(/\\\\s+/g,' ').trim().toLocaleLowerCase('ro');"
            + "const el=els.find(e=>{"
            + " const t=norm(e.innerText||e.textContent||e.value||e.getAttribute('aria-label'));"
            + " const r=e.getBoundingClientRect();"
            + " const st=getComputedStyle(e);"
            + " return t===wanted && r.width>0 && r.height>0 && st.visibility!=='hidden' && st.display!=='none';"
            + "});"
            + "if(el){el.click(); return 'clicked';}"
            + "return 'not_found';"
            + "})();";

        webView.evaluateJavascript(js, value -> {
            if ("\"clicked\"".equals(value)) {
                statusView.setText("Butonul «Pagina următoare» a fost apăsat. Următoarea încercare peste 9 minute.");
            } else {
                statusView.setText("Nu am găsit butonul «Pagina următoare». Voi reîncerca peste 9 minute.");
                Toast.makeText(this, "Butonul nu a fost găsit pe pagina curentă.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        stopAutomation();
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
