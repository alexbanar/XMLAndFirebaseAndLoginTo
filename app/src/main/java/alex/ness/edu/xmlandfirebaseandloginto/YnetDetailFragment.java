package alex.ness.edu.xmlandfirebaseandloginto;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

//import alex.ness.edu.xmlandfirebaseandlogin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class YnetDetailFragment extends Fragment {
    //Constants
    private static final String ARG_URL = "url";

    //Properties:
    private WebView webView;
    private ProgressBar progressBar;


    public static YnetDetailFragment newInstance(String url) {

        Bundle args = new Bundle();

        args.putString(ARG_URL, url);

        YnetDetailFragment fragment = new YnetDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ynet_detail, container, false);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        webView = (WebView) v.findViewById(R.id.webview);
        String url = getArguments().getString(ARG_URL);
        //JS
        webView.getSettings().setJavaScriptEnabled(true);
        //redirection
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                webView.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.loadUrl(url);

        return v;
    }
}


