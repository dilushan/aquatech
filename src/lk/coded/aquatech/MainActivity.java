package lk.coded.aquatech;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;

public class MainActivity extends ActionBarActivity {

	private static final String DEBUG_TAG = "HttpExample";
	private static String stringUrl = "http://www.dilushan.tk/aquatech/aquatech.php?get=";
	String[] temp2 = new String[40];
	String[] temp3;
	private EditText editText;
	private TextView textView2;
	private FrameLayout layout;
	private GraphViewSeries exampleSeries; 
	private GraphView graphView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
		editText = (EditText) findViewById(R.id.editText2);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView2.setMovementMethod(new ScrollingMovementMethod()); // scrolling
																	// added to
																	// textview

		layout = (FrameLayout) findViewById(R.id.customlayout);
		exampleSeries = new GraphViewSeries(
				new GraphViewData[] { new GraphViewData(1,0) });
		graphView = new BarGraphView(this /* context */, "Water Level" /* heading */);

	}

	public void graphDraw() {

		exampleSeries.resetData(new GraphViewData[] {
				new GraphViewData(1, Double.parseDouble(temp2[3])),
				new GraphViewData(2, Double.parseDouble(temp2[7])),
				new GraphViewData(3, Double.parseDouble(temp2[11])),
				new GraphViewData(4, Double.parseDouble(temp2[15])),
				new GraphViewData(5, Double.parseDouble(temp2[19])),
				new GraphViewData(6, Double.parseDouble(temp2[23])),
				new GraphViewData(7, Double.parseDouble(temp2[27])),
				new GraphViewData(8, Double.parseDouble(temp2[31])),
				new GraphViewData(9, Double.parseDouble(temp2[35])),
				new GraphViewData(10, Double.parseDouble(temp2[39])) });

		layout.removeView(graphView);
		graphView.addSeries(exampleSeries); // data
		graphView.setManualYAxisBounds(12, 2);
		graphView.getGraphViewStyle().setTextSize((float) 15.0);
		graphView.getGraphViewStyle().setNumVerticalLabels(6);
		graphView.getGraphViewStyle().setNumHorizontalLabels(10);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(30); // width of
																	// lables
		layout.addView(graphView);

	}

	public void button1ActionPerformed(View view) {

		hideKeyBoard();
		String temp = editText.getText().toString();

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadWebpageTask().execute(stringUrl + temp);
		} else {
			Toast.makeText(getApplicationContext(),
					"No network. Check connection", Toast.LENGTH_LONG).show();
		}

	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String downloadUrl(String myurl) throws IOException {
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 500;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Log.d(DEBUG_TAG, "The response is: " + response);
			is = conn.getInputStream();

			// Convert the InputStream into a string
			String contentAsString = readIt(is, len);
			return contentAsString;

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream, int len) throws IOException,
			UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// System.out.println(result);
			result = result.replace("<br>", "\n"); // This will format the input
													// string to Viewable
			String[] temp;
			temp = result.split("<");
			temp[0]=temp[0].substring(0, temp[0].length()-1);
			result=temp[0].replace("\n", " m\n");
			textView2.setText(result);

			temp3 = temp[0].split("\\s+");

			int j = 0;
			for (j = 0; j < temp3.length&&j<40; j++)
				temp2[j] = temp3[j];
			for (int i = j; i < 40; i++)
				temp2[i] = "2";

			graphDraw();
		}
	}
	
	public void hideKeyBoard(){
		
		InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(),0);  

	}

	
}
