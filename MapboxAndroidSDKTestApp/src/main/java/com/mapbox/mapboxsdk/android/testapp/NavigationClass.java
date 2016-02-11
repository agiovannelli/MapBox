package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
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
import java.util.List;
import java.util.Locale;

public class NavigationClass extends Fragment implements TabLayout.OnTabSelectedListener
{
    private MapView mapView = null;
    private LatLng navigationLatLng = null;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_navigation_explorer, container, false);
        Button search = (Button)view.findViewById(R.id.search);
        final EditText input = (EditText)view.findViewById(R.id.input);
        mapView = (MapView) view.findViewById(R.id.mapview);

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
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return p1;
    }
}