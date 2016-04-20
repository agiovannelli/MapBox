package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.mapbox.mapboxsdk.android.testapp.ui.CustomInfoWindow;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;

public class NavigationClass extends Fragment implements TabLayout.OnTabSelectedListener
{
    private MapView mapView = null;
    private LatLng navigationLatLng = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LatLng tempLatLng = new LatLng(39.1321095, -84.5177543);
        final View view = inflater.inflate(R.layout.fragment_navigation_explorer, container, false);
        Button search = (Button)view.findViewById(R.id.search);
        final EditText input = (EditText)view.findViewById(R.id.input);

        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.setCenter(tempLatLng);

        search.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0)
            {
                mapView.clear();
                try
                {
                    if (getActivity().getCurrentFocus() != null)
                    {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    navigationLatLng = getLocationFromAddress(view.getContext(), input.getText().toString());

                    Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(navigationLatLng.getLatitude(), navigationLatLng.getLongitude(), 1);
                    Marker marker = new Marker(mapView, addresses.get(0).getAddressLine(0), addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea(), navigationLatLng);

                    marker.setToolTip(new CustomInfoWindow(mapView, navigationLatLng));

                    mapView.addMarker(marker);
                    mapView.setCenter(navigationLatLng);
                    mapView.setZoom(18);
                    marker.closeToolTip();
                }
                catch (Exception e)
                {
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void onTabSelected(final TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(final TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(final TabLayout.Tab tab) {

    }

    public LatLng getLocationFromAddress(Context context,String strAddress)
    {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null)
            {
                return null;
            }

            Address location = address.get(0);

            byte[] signedData = signingData(location.getFeatureName(), context);

            if (verifySignature(location.getFeatureName(), signedData, context))
            {
                location.getLatitude();
                location.getLongitude();

                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return p1;
    }
    public PublicKey readPublicKey(String keyFile, Context context) throws Exception
    {
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(getKey(keyFile, context));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicSpec);
    }

    public PrivateKey readPrivateKey(String keyFile, Context context) throws Exception
    {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(getKey(keyFile, context));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public byte[] getKey(String keyFile, Context context) throws Exception
    {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(keyFile);
        byte[] keyBytes = null;

        if (inputStream != null)
        {
            keyBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
        }

        return keyBytes;
    }

    // encrypt PrivateKey Overload. Used for digital signing process.
    public byte[] encrypt(PrivateKey key, byte[] plaintext) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    // decrypt PublicKey Overload. Used for digital signing process.
    public byte[] decrypt(PublicKey key, byte[] ciphertext) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    // Signs original data using the private key.
    public byte[] signingData(String originalData, Context context) throws Exception
    {
        byte[] originalByte = originalData.getBytes();
        PrivateKey privateKey = readPrivateKey("private.der", context);
        byte[] encryptedData = encrypt(privateKey, originalByte);
        return encryptedData;
    }

    // Verifies encrypted data matches that of the original data after decryption using public key.
    public boolean verifySignature(String originalData, byte[] encryptedData, Context context) throws Exception
    {
        // Initialize verified to false.
        boolean verified = false;
        byte[] originalByte = originalData.getBytes();

        PublicKey publicKey = readPublicKey("public.der", context);
        byte[] decryptedData = decrypt(publicKey, encryptedData);

        // Compare the byte arrays for equality. When equal, update verified to true.
        if (Arrays.equals(originalByte, decryptedData))
        {
            verified = true;
        }
        return verified;
    }

}
