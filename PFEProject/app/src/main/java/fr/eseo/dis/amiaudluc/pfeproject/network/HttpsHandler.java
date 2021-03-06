package fr.eseo.dis.amiaudluc.pfeproject.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import fr.eseo.dis.amiaudluc.pfeproject.R;
import fr.eseo.dis.amiaudluc.pfeproject.common.Tools;

/**
 * Created by lucasamiaud on 20/12/2017.
 */

public class HttpsHandler {

    private static final String TAG = HttpsHandler.class.getSimpleName();

    public HttpsHandler() {
    }

    /**
     * This function will verify if the user is connected to internet.
     *
     * @param ctx : The current context.
     * @return Boolean which describes whether the user is well connected.
     */
    public boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public InputStream makeServiceCallStream(String req,String args,Context context) {
        InputStream in = null;
        String reqUrl = "https://192.168.4.10/www/pfe/webservice.php?q="+req+args;
        try {

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream chainInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.chain));
            InputStream interInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.inter));
            InputStream rootInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.root));
            Certificate chain;
            Certificate inter;
            Certificate root;

            try {
                chain = cf.generateCertificate(chainInput);
                inter = cf.generateCertificate(interInput);
                root = cf.generateCertificate(rootInput);
            } finally {
                chainInput.close();
                interInput.close();
                rootInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);

            keyStore.setCertificateEntry("chain", chain);
            keyStore.setCertificateEntry("inter", inter);
            keyStore.setCertificateEntry("root", root);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext contextSSL = SSLContext.getInstance("TLS");
            contextSSL.init(null, tmf.getTrustManagers(), null);

            URL url = new URL(reqUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setSSLSocketFactory(contextSSL.getSocketFactory());
            conn.setRequestMethod("GET");
            // read the response
            in = new BufferedInputStream(conn.getInputStream());
            //response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return in;
    }

    public String makeServiceCall(String req,String args,Context context){
        InputStream result = makeServiceCallStream(req,args,context);
        if(result == null){
            return "";
        }else {
            return Tools.convertStreamToString(result);
        }
    }
}

