package silent.kuasapmaterial.libs;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public abstract class ImgurUploadTask extends AsyncTask<Void, Integer, String> {

	private WeakReference<Context> wContext;
	private Uri mImageUri; // local Uri to upload

	private static final String UPLOAD_URL = "https://api.imgur.com/3/image";

	public ImgurUploadTask(Context context, Uri imageUri) {
		this.wContext = new WeakReference<>(context);
		this.mImageUri = imageUri;
	}

	protected void onGetTotalSize(int size) {
	}

	@Override
	protected String doInBackground(Void... params) {
		InputStream imageIn;
		try {
			Context context = wContext.get();
			if (context == null) {
				cancel(true);
				return null;
			}
			imageIn = context.getContentResolver().openInputStream(mImageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		HttpURLConnection conn = null;
		InputStream responseIn = null;

		try {
			conn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
			conn.setDoOutput(true);
			int length = imageIn.available();
			onGetTotalSize(length);
			conn.setFixedLengthStreamingMode(length);

			// add access token to header
			addToHttpURLConnection(conn);

			OutputStream out = conn.getOutputStream();
			copy(imageIn, out);
			out.flush();
			out.close();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				responseIn = conn.getInputStream();
				return onInput(responseIn);
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				responseIn.close();
				conn.disconnect();
				imageIn.close();
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Copy content from InputStream to OutputStream.
	 *
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	private int copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[8192];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
			publishProgress(count);
		}
		return count;
	}

	/**
	 * Get imgur upload result.
	 *
	 * @param in : InputStream
	 * @return
	 * @throws Exception
	 */
	private String onInput(InputStream in) throws Exception {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(in);
		while (scanner.hasNext()) {
			sb.append(scanner.next());
		}

		JSONObject root = new JSONObject(sb.toString());
		String id = root.getJSONObject("data").getString("id");
		// String deletehash =
		// root.getJSONObject("data").getString("deletehash");
		scanner.close();
		return id;
	}

	/**
	 * Add access token to header.
	 *
	 * @param conn
	 */
	private void addToHttpURLConnection(HttpURLConnection conn) {
		Context context = wContext.get();
		if (context == null) {
			return;
		}

		String accessToken = Memory.getString(context, "access_token", null);

		if (!TextUtils.isEmpty(accessToken)) {
			// accessToken is not empty, add it to Authorization header
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		} else {
			// accessToken is empty, add client id to Authorization header
			conn.setRequestProperty("Authorization", "Client-ID " + Constant.IMGUR_CLIENT_ID);
		}
	}
}
