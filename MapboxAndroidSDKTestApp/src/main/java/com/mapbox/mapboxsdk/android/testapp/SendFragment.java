package com.mapbox.mapboxsdk.android.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Alex on 2/18/16.
 */
public class SendFragment extends Fragment
{
    public SendFragment()
    {
        // Empty constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Pass in arguments from MainActivity regarding CustomInfoWindow Strings.
        String address = getArguments().getString("address");
        String cityState = getArguments().getString("cityState");

        // Create view to allow the altering of contained objects.
        final View view = inflater.inflate(R.layout.fragment_send, container, false);

        // Try to set the TextView values. Prevent program from crashing in case of failure.
        try
        {
            TextView addressText = (TextView) view.findViewById(R.id.street_address_text);
            addressText.setText(address);

            TextView cityStateText = (TextView) view.findViewById(R.id.city_state_text);
            cityStateText.setText(cityState);
        }
        catch (Exception ex) {

        }

        // Inflate layout for designated fragment.
        return view;
    }
}
