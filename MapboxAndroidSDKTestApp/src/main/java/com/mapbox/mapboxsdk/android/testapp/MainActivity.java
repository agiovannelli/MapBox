package com.mapbox.mapboxsdk.android.testapp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import java.lang.Object;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	private DrawerLayout          mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavigationView        mNavigationView;
	private Menu                  testFragmentNames;
	private int selectedFragmentIndex = 0;

	public String addressString;
	public String cityStateString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		   MapView.setDebugMode(true); //make sure to call this before the view is created!
           */
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
		mNavigationView.setNavigationItemSelectedListener(this);

		// Set the adapter for the list view
		testFragmentNames = mNavigationView.getMenu();
		int i = 0;

		// Display new button labeled "Navigation" and set it's case value manually to '20'.
		testFragmentNames.add(Menu.NONE, 20, Menu.NONE, getString(R.string.navigationDialog));

		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.mainTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.alternateTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.markersTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.itemizedOverlayTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.localGeoJSONTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.localOSMTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.diskCacheDisabledTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.offlineCacheTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.programmaticTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.webSourceTileTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.locateMeTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.pathTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.bingTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.saveMapOfflineTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.tapForUTFGridTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.customMarkerTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.rotatedMapTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.clusteredMarkersTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.mbTilesTestMap));
        testFragmentNames.add(Menu.NONE, i, Menu.NONE, getString(R.string.draggableMarkersTestMap));



		// Set the drawer toggle as the DrawerListener
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigationdrawer_open, R.string.navigationdrawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		setSupportActionBar(toolbar);

		// Set MainTestFragment
		selectItem(0);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item);
	}

	/**
	 * Swaps fragments in the main content view
	 */
	private void selectItem(int position) {
		final MenuItem menuItem = mNavigationView.getMenu().findItem(position);
		setTitle(menuItem.getTitle());

		selectedFragmentIndex = position;
		// Create a new fragment and specify the planet to show based on position
		Fragment fragment;

		switch (position) {
			case 0:
				fragment = new MainTestFragment();
				break;
			case 1:
				fragment = new AlternateMapTestFragment();
				break;
			case 2:
				fragment = new MarkersTestFragment();
				break;
			case 3:
				fragment = new ItemizedIconOverlayTestFragment();
				break;
			case 4:
				fragment = new LocalGeoJSONTestFragment();
				break;
			case 5:
				fragment = new LocalOSMTestFragment();
				break;
			case 6:
				fragment = new DiskCacheDisabledTestFragment();
				break;
			case 7:
				fragment = new OfflineCacheTestFragment();
				break;
			case 8:
				fragment = new ProgrammaticTestFragment();
				break;
			case 9:
				fragment = new WebSourceTileTestFragment();
				break;
			case 10:
				fragment = new LocateMeTestFragment();
				break;
			case 11:
				fragment = new PathTestFragment();
				break;
			case 12:
				fragment = new BingTileTestFragment();
				break;
			case 13:
				fragment = new SaveMapOfflineTestFragment();
				break;
			case 14:
				fragment = new TapForUTFGridTestFragment();
				break;
			case 15:
				fragment = new CustomMarkerTestFragment();
				break;
			case 16:
				fragment = new RotatedMapTestFragment();
				break;
			case 17:
				fragment = new ClusteredMarkersTestFragment();
				break;
			case 18:
				fragment = new MBTilesTestFragment();
				break;
            case 19:
                fragment = new DraggableMarkersTestFragment();
                break;
			case 20:
				fragment = new NavigationClass();
				//fragment = new SendFragment();
				break;
			default:
				fragment = new MainTestFragment();
				break;
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commit();

		mDrawerLayout.closeDrawer(mNavigationView);
	}

	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

	@Override
	public boolean onNavigationItemSelected(final MenuItem menuItem) {
		selectItem(menuItem.getItemId());
		return true;
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
		{
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else
		{
			super.onBackPressed();
		}
	}

	public void openContactDialog(View view)
	{
		// Lets get the Street Address and City, State information from the CustomInfoWindow instance.
		TextView titleText = (TextView)view.findViewById(R.id.customTooltip_title);
		TextView descriptionText = (TextView)view.findViewById(R.id.customTooltip_Description);

		// We create these global variables to store the address and city, state information for future use.
		addressString = titleText.getText().toString();
		cityStateString = descriptionText.getText().toString();

		// We create a Bundle to pass addressString and cityStateString as arguments to fragment.
		Bundle args = new Bundle();
		args.putString("address", addressString);
		args.putString("cityState", cityStateString);

		// Instantiate the SendFragment.
		Fragment fragment = new SendFragment();
		fragment.setArguments(args);

		// Switch support fragment to SendFragment.
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	// When the Add to Contact button is pressed, do necessary security measures and pass address
	// information to the Contact Manager application as text.
	public void addContactInformation(View view) throws Exception
	{
		// Concatenation of address and city/state strings.
		String finalString = addressString + ", " + cityStateString;

		// Digital Signature authentication.
		byte[] signedData = signingData(finalString);
		boolean verifiedSignature = verifySignature(finalString, signedData);

		// When digital signature is verified, we continue to do encryption of data for passage to Contact Manager.
		if (verifiedSignature)
		{
			byte[] finalStringByte = finalString.getBytes(Charset.forName("UTF-8"));
			PublicKey publicKey = readPublicKey("public.der");

			byte[] finalStringEncrypted = encrypt(publicKey, finalStringByte);

			// Generate new intent.
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, finalStringEncrypted);

			startActivity(intent);
		}
	}

	public PublicKey readPublicKey(String keyFile) throws Exception
	{
		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(getKey(keyFile));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(publicSpec);
	}

	public PrivateKey readPrivateKey(String keyFile) throws Exception
	{
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(getKey(keyFile));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

	public byte[] getKey(String keyFile) throws Exception
	{
		AssetManager assetManager = getAssets();
		InputStream inputStream = assetManager.open(keyFile);
		byte[] keyBytes = null;

		if (inputStream != null)
		{
			keyBytes = IOUtils.toByteArray(inputStream);
			inputStream.close();
		}

		return keyBytes;
	}

	// encrypt PublicKey Overload. Used for encryption between MapBox and ContactManager.
	public byte[] encrypt(PublicKey key, byte[] plaintext) throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(plaintext);
	}

	// encrypt PrivateKey Overload. Used for digital signing process.
	public byte[] encrypt(PrivateKey key, byte[] plaintext) throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(plaintext);
	}

	// decrypt PrivateKey Overload. Used for decryption between MapBox and ContactManager.
	public byte[] decrypt(PrivateKey key, byte[] ciphertext) throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(ciphertext);
	}

	// decrypt PublicKey Overload. Used for digital signing process.
	public byte[] decrypt(PublicKey key, byte[] ciphertext) throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(ciphertext);
	}

	// Signs original data using the private key.
	public byte[] signingData(String originalData) throws Exception
	{
		byte[] originalByte = originalData.getBytes();
		PrivateKey privateKey = readPrivateKey("private.der");
		byte[] encryptedData = encrypt(privateKey, originalByte);
		return encryptedData;
	}

	// Verifies encrypted data matches that of the original data after decryption using public key.
	public boolean verifySignature(String originalData, byte[] encryptedData) throws Exception
	{
		// Initialize verified to false.
		boolean verified = false;
		byte[] originalByte = originalData.getBytes();

		PublicKey publicKey = readPublicKey("public.der");
		byte[] decryptedData = decrypt(publicKey, encryptedData);

		// Compare the byte arrays for equality. When equal, update verified to true.
		if (Arrays.equals(originalByte, decryptedData))
		{
			verified = true;
		}
		return verified;
	}
}
