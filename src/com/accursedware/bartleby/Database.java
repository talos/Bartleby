/**
_ * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.caustic.Request;
import net.caustic.http.Cookies;
import net.caustic.http.HashtableCookies;
import net.caustic.util.CollectionStringMap;
import net.caustic.util.StringMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author talos
 *
 */
class Database extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "bartleby";
	private static final int DATABASE_VERSION = 4;
	
	private static final String DATA = "data";
	private static final String RELATIONSHIPS = "relationships";
	private static final String COOKIES = "cookies";
	private static final String WAIT = "wait";
	private static final String RETRY = "retry";
	
	private static final String SOURCE = "source";
	private static final String SCOPE = "scope";
	private static final String NAME = "name";
	private static final String VALUE = "value";
	private static final String INPUT = "input";
	private static final String MISSING_TAGS = "missing_tags";
	private static final String URI = "uri";
	private static final String INSTRUCTION = "instruction";
	private static final String COOKIE = "cookie";
	private static final String HOST = "host";
	private static final String DESCRIPTION = "description";
	
	private SQLiteDatabase db;
	private final List<DatabaseListener> listeners = new ArrayList<DatabaseListener>();
	
	Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.db = getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// relationship table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + RELATIONSHIPS +
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SOURCE + " VARCHAR, " +
				SCOPE  + " VARCHAR, " +
				NAME   + " VARCHAR, " +
				VALUE  + " VARCHAR)");
		
		// data table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA +
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SCOPE + " VARCHAR, " +
				NAME  + " VARCHAR, " +
				VALUE + " VARCHAR)");
		
		// wait table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + WAIT +
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SCOPE       + " VARCHAR, " +
				INSTRUCTION + " VARCHAR, " +
				DESCRIPTION + " VARCHAR, " +
				URI         + " VARCHAR)");

		// retry table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + RETRY +
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SCOPE        + " VARCHAR, " +
				INSTRUCTION  + " VARCHAR, " +
				INPUT        + " VARCHAR, " +
				URI          + " VARCHAR, " +
				MISSING_TAGS + " VARCHAR)");

		// cookies (browser state) table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + COOKIES +
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SCOPE  + " VARCHAR, " +
				HOST   + " VARCHAR, " +
				COOKIE + " VARCHAR)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		android.util.Log.i(DATABASE_NAME, "upgrading, this throws away old db");
		db.execSQL("DROP TABLE IF EXISTS " + RELATIONSHIPS);
		db.execSQL("DROP TABLE IF EXISTS " + DATA);
		db.execSQL("DROP TABLE IF EXISTS " + WAIT);
		db.execSQL("DROP TABLE IF EXISTS " + RETRY);
		db.execSQL("DROP TABLE IF EXISTS " + COOKIES);
		onCreate(db); // re-create db
	}
	
	void addListener(DatabaseListener listener) {
		listeners.add(listener);
	}
	
	void saveData(String scope, String name, String value) {
		ContentValues cv = new ContentValues(3);
		cv.put(SCOPE, scope);
		cv.put(NAME, name);
		cv.put(VALUE, value);
		db.insert(DATA, null, cv);
		
		notifyListeners(scope);
	}
	
	void saveRelationship(String scope, String source, String name, String value) {
		ContentValues cv = new ContentValues(2);
		cv.put(SOURCE, source);
		cv.put(SCOPE, scope);
		cv.put(NAME, name);
		cv.put(VALUE, value);
		db.insert(RELATIONSHIPS, null, cv);
	}
	
	void saveCookies(String scope, Cookies cookies) {
		ContentValues cv = new ContentValues(3);
		cv.put(SCOPE, scope);
		String[] hosts = cookies.getHosts();
		for(String host : hosts) {
			cv.put(HOST, host);
			String[] cookiesForHost = cookies.get(host);
			for(String cookie : cookiesForHost) {
				cv.put(COOKIE, cookie);
				db.insert(COOKIES, null, cv);
			}
		}
	}
	
	void saveWait(String scope, String instruction, String uri, String description) {
		ContentValues cv = new ContentValues(3);
		cv.put(SCOPE, scope);
		cv.put(INSTRUCTION, instruction);
		cv.put(URI, uri);
		cv.put(DESCRIPTION, description);
		db.insert(WAIT, null, cv);
		
		notifyListeners(scope);
	}
	
	void saveMissingTags(String scope, String instruction, String uri, String input, String[] missingTags) {

		ContentValues cv = new ContentValues(4);
		cv.put(SCOPE, scope);
		cv.put(INSTRUCTION, instruction);
		cv.put(URI, uri);
		cv.put(INPUT, input);
		cv.put(MISSING_TAGS, new JSONArray(Arrays.asList(missingTags)).toString()); // serialize missing tags via JSON
		db.insert(RETRY, null, cv);
	}
	
	/**
	 * The returned Wait request will have force enabled.
	 * @param scope
	 * @return A map of {@link Request}s keyed by name.
	 */
	Map<String, Request> getWait(String scope) {
		Cursor cursor = db.query(WAIT, new String[] { INSTRUCTION, URI, DESCRIPTION }, 
				SCOPE + " = ?", new String[] { scope },
				null, null, null);
		
		Map<String, Request> waits = new HashMap<String, Request>(cursor.getCount(), 1);
		while(cursor.moveToNext()) {
			String instruction = cursor.getString(0);
			String uri = cursor.getString(1);
			String description = cursor.getString(2);
			
			String name;
			// try to pull a name out of description.
			if(description != null) {
				try {
					JSONObject obj = new JSONObject(description);
					name = obj.getString("name");
				} catch(JSONException e) {
					name = description;
				}
			} else {
				name = instruction;
			}
				
			// input is null
			waits.put(name, new Request(scope, instruction, uri, null,
					new CollectionStringMap(getData(scope)), getCookies(scope), true));
		}
		cursor.close();
		return waits;
	}
	
	/**
	 * This will only return Requests that were missing tags that can now be executed.
	 * They will be removed from the database.  They will not be forced.
	 * @param scope
	 * @return
	 */
	List<Request> popMissingTags(String scope) {
		Cursor cursor = db.query(RETRY, new String[] { INSTRUCTION, URI, INPUT, MISSING_TAGS },
				SCOPE + " = ?", new String[] { scope },
				null, null, null);
		
		StringMap tags = new CollectionStringMap(getData(scope));
		Cookies cookies = null; // these are lazily loaded in the event that there actually are requests
		List<Request> result = new ArrayList<Request>(cursor.getCount());
		while(cursor.moveToNext()) {
			String instruction = cursor.getString(0);
			String uri = cursor.getString(1);
			String input = cursor.getString(2);
			
			// only return requests that are no longer missing tags.
			boolean isReady = true;
			try {
				JSONArray missingTagsJSON = new JSONArray(cursor.getString(3));
				for(int i = 0 ; i < missingTagsJSON.length() ; i ++) {
					if(tags.get(missingTagsJSON.getString(i)) == null) {
						isReady = false;
						break;
					}
				}
			} catch(JSONException e) { // this shouldn't happen!!
				throw new RuntimeException("Invalid JSON in database", e);
			}
			
			if(isReady) {
				if(cookies == null) {
					cookies = getCookies(scope);
				}
				result.add(new Request(scope, instruction, uri, input, tags, cookies, false));
			}
		}
		cursor.close();
		
		return result;
	}
	
	Map<String, String> getData(String scope) {
		Map<String, String> parentData = new HashMap<String, String>();
		Map<String, String> thisData = getDataInScope(scope);
		
		// loop through source scopes.
		while((scope = getSource(scope)) != null) {
			// This ensures that child keys overwrite parent keys.
			Map<String, String> interData = getDataInScope(scope);
			interData.putAll(parentData);
			parentData = interData;
		}
		
		parentData.putAll(thisData);
		return parentData;
	}
	
	/**
	 * 
	 * @param source
	 * @return A map of children ID : branch value maps, keyed by name.
	 */
	Map<String, Map<String, String>> getChildren(String source) {
		Cursor cursor = db.query(RELATIONSHIPS, new String[] { SCOPE, NAME, VALUE }, 
				SOURCE + " = ?", new String[] { source }, 
				null, null, null);
		
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		while(cursor.moveToNext()) {
			String scope = cursor.getString(0);
			String name = cursor.getString(1);
			String value = cursor.getString(2);
			final Map<String, String> child;
			
			// create list if it doesn't already exist in result
			if(!result.containsKey(name)) {
				child = new HashMap<String, String>();
				result.put(name, child);
			} else {
				child = result.get(name);
			}
			child.put(scope, value);
		}
		cursor.close();
		
		return result;
	}
	
	/**
	 * 
	 * @param scope The {@link String} scope whose source should be found.
	 * @return A {@link String} source scope, if one exists; <code>null</code> otherwise.
	 */
	String getSource(String scope) {
		Cursor cursor = db.query(RELATIONSHIPS, new String[] { SOURCE },
				SCOPE + " = ?", new String[] { scope },
				null, null, null);
		
		String source = cursor.moveToFirst() ? cursor.getString(0) : null;
		cursor.close();
		return source;
	}
	
	Cookies getCookies(String scope) {
		Cookies thisCookies = getCookiesInScope(scope);
		HashtableCookies parentCookies = new HashtableCookies();
		
		while((scope = getSource(scope)) != null) {
			parentCookies.extend(getCookiesInScope(scope));
		}
		
		parentCookies.extend(thisCookies);
		return parentCookies;
	}
	
	Cookies getCookiesInScope(String scope) {
		HashtableCookies cookies = new HashtableCookies();
		Cursor cursor = db.query(COOKIES, new String[] { HOST, COOKIE },
				SCOPE + " = ?", new String[] { scope },
				null, null, null);
		
		while(cursor.moveToNext()) {
			String host = cursor.getString(0);
			cookies.add(host, cursor.getString(1));
		}
		cursor.close();
		return cookies;
	}
	
	Map<String, String> getDataInScope(String scope) {
		Map<String, String> data = new HashMap<String, String>();

		Cursor cursor = db.query(
				DATA, new String[] { NAME, VALUE },
				SCOPE + " = ?", new String[] { scope },
				null, null, null);

		while(cursor.moveToNext()) {
			data.put(cursor.getString(0), cursor.getString(1));
		}
		cursor.close();
		
		return data;
	}
	
	private void notifyListeners(String scope) {
		for(DatabaseListener listener : listeners) {
			listener.updated(scope);
		}
	}
}
