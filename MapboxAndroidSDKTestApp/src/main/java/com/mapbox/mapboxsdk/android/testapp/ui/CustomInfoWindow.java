package com.mapbox.mapboxsdk.android.testapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import java.io.BufferedReader;
import java.io.Reader;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class CustomInfoWindow extends InfoWindow
{
    // Original Overload for CustomInfoWindow.
    public CustomInfoWindow(MapView mv)
    {
        super(R.layout.infowindow_custom, mv);

        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Demonstrate custom onTouch() control
                    Toast.makeText(mView.getContext(), R.string.customInfoWindowOnTouchMessage, Toast.LENGTH_SHORT).show();

                    // Still close the InfoWindow though
                    close();
                }

                // Return true as we're done processing this event
                return true;
            }
        });
    }

    double latitude;
    double longitude;

    // Overload for InfoWindow to include LatLng DataType parameter for navigation destination.
    public CustomInfoWindow(final MapView mapView, final LatLng navigateTo)
    {
        super(R.layout.infowindow_custom, mapView);

        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    mapView.closeCurrentTooltip();

                    Geocoder geo;
                    String finalProvider;
                    List<Address> addressList = null;
                    LocationManager locationManager = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);

                    Criteria criteria = new Criteria();
                    finalProvider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(finalProvider);
                    geo = new Geocoder(v.getContext());

                    try
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        // Case for when the location can be determined.
                        if (location != null)
                        {
                            addressList = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latitude = (double) addressList.get(0).getLatitude();
                            longitude = (double) addressList.get(0).getLongitude();
                            mapView.addMarker(new Marker(mapView, addressList.get(0).getAddressLine(0), addressList.get(0).getLocality() + ", " + addressList.get(0).getAdminArea(), new LatLng(latitude, longitude)));

                        }
                        // Case for when the location can not be determined.
                        else
                        {
                            latitude = 39.1321095;
                            longitude = -84.5177543;
                            mapView.addMarker(new Marker(mapView, "University of Cincinnati", "2600 Clifton Avenue" + ", " + "Cincinnati, Ohio", new LatLng(latitude, longitude)));
                        }

                        String sURL = "https://api.mapbox.com/v4/directions/mapbox.driving/" + longitude + "," + latitude + ";" + navigateTo.getLongitude() + "," + navigateTo.getLatitude() + ".json?access_token=pk.eyJ1IjoicmVzZXJhZCIsImEiOiJjaWs4dzdubWgwMHhvdXhrdXN2eTd5djVoIn0.nTcJFOD8ofmioyrjiADLRA";

                        URL url = new URL(sURL);
                        HttpURLConnection request = (HttpURLConnection) url.openConnection();

                        request.setRequestMethod("GET");
                        request.setRequestProperty("Content-length", "0");
                        request.setUseCaches(false);
                        request.setAllowUserInteraction(false);
                        request.connect();

                        JSONObject jsonObject = null;
                        jsonObject = readJsonFromUrl(sURL);
                        displayRoutes(mapView, v.getContext(), jsonObject);
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                // Return true as we're done processing this event
                return true;
            }
        });
    }

    private static String readAll(Reader reader) throws IOException
    {
        int temp;
        StringBuilder sb = new StringBuilder();

        while ((temp = reader.read()) != -1)
        {
            sb.append((char) temp);
        }

        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException
    {
        InputStream is = new URL(url).openStream();

        try
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject jsonObject = new JSONObject(jsonText);
            return jsonObject;
        }
        finally
        {
            is.close();
        }
    }

    // Display route from read JSON object.
    private void displayRoutes(final MapView mv, final Context context, final JSONObject jsonObject)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View promptsView = layoutInflater.inflate(R.layout.fragment_route, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        double metersToMilesMultiplier = 0.000621371;

        try
        {
            LinearLayout buttonLayout = (LinearLayout) promptsView.findViewById(R.id.buttonLayout);
            for (int i = 0; i < jsonObject.getJSONArray("routes").length(); i++)
            {
                double distance = Double.parseDouble(jsonObject.getJSONArray("routes").getJSONObject(i).get("distance").toString());

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout nested_linear_layout = new LinearLayout(context);
                nested_linear_layout.setOrientation(LinearLayout.HORIZONTAL);
                nested_linear_layout.setLayoutParams(layoutParams);
                nested_linear_layout.setPadding(5,5,5,5);

                TextView displayTextView = new TextView(context);
                displayTextView.setText("Route " + (i + 1));
                displayTextView.setTextColor(Color.WHITE);
                displayTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                nested_linear_layout.addView(displayTextView);

                TextView displayTextViewMiles = new TextView(context);
                displayTextViewMiles.setText(round(distance * metersToMilesMultiplier, 1) + " miles");
                displayTextViewMiles.setTextColor(Color.WHITE);
                displayTextViewMiles.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                nested_linear_layout.addView(displayTextViewMiles);

                Button navigateToButton = new Button(context);
                navigateToButton.setBackgroundResource(R.color.mapboxBlue);
                navigateToButton.setText("SELECT");
                navigateToButton.setTextColor(Color.WHITE);
                navigateToButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                nested_linear_layout.addView(navigateToButton);

                buttonLayout.addView(nested_linear_layout);
                navigateToButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (Overlay o: mv.getOverlays())
                        {
                            mv.removeOverlay(o);
                        }
                        drawLines(mv, context, jsonObject);
                        alertDialog.dismiss();
                    }
                });
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    // Draw lines between start location and destination location.
    public void drawLines(MapView mv, Context context, JSONObject jsonObject)
    {
        try
        {
            PathOverlay pathOverlay  = new PathOverlay();
            pathOverlay.getPaint().setStyle(Paint.Style.STROKE);

            JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONArray tempJsonArray = jsonArray.getJSONArray(i);
                pathOverlay.addPoint(Double.parseDouble(tempJsonArray.get(1).toString()), Double.parseDouble(tempJsonArray.get(0).toString()));
            }

            pathOverlay.getPaint().setColor(Color.BLUE);
            mv.addOverlay(pathOverlay);

            mv.setZoom(14);
            mv.setCenter(new LatLng(latitude, longitude));

        }
        catch (JSONException e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Round values.
    private static double round (double value, int precision)
    {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    /**
     * Dynamically set the content in the CustomInfoWindow
     * @param overlayItem The tapped Marker
     */
    @Override
    public void onOpen(Marker overlayItem) {
        String title = overlayItem.getTitle();
        ((TextView) mView.findViewById(R.id.customTooltip_title)).setText(title);

        String description = overlayItem.getDescription();
        ((TextView) mView.findViewById(R.id.customTooltip_Description)).setText(description);
    }
}