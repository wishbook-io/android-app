package com.wishbook.catalog.Utils.contactsutil;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

public class ContentProviderUtil {
	private static ContentProviderUtil singleton;

	private static String TAG = ContentProviderUtil.class.getSimpleName();

	Context context;



	private ContentProviderUtil(Context context) {
		this.context = context;
	}
	
	public static ContentProviderUtil getInstance(Context context) {
		if (singleton == null) {
			singleton = new ContentProviderUtil(context);
		}
		return singleton;
	}

	public void logCursor(String TAG, Cursor cursor) {
		Log.d(TAG, "count:" + cursor.getCount());
		cursor.moveToFirst();
		int count = 0;
		while (!cursor.isAfterLast()) {
			if(count < 100) {
				Log.d(TAG, "==========row:" + cursor.getPosition() +"===========");
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					Log.d(TAG,
							"===>column " + cursor.getColumnName(i) + ":"
									+ cursor.getString(i));

				}
			}
			count++;
			cursor.moveToNext();
		}
	}

	public ContentProviderResult[] applyBatch(Context context, String authority,
			ArrayList<ContentProviderOperation> operations) {
		ContentResolver cr = context.getContentResolver();
		if(cr!=null) {
			try {
				ContentProviderResult[] results = cr.applyBatch(authority,
						operations);
				return results;
			} catch (NullPointerException e) {
				e.printStackTrace();
			}catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void logContentProviderResult(String TAG, ContentProviderResult[] results) {
		for (ContentProviderResult result : results) {
			Log.d(TAG, result.toString());
		}
	}

	public ArrayList<ContentProviderOperation> getDeleteRawContactOperations(
			String dataId) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation
				.newDelete(ContactsContract.RawContacts.CONTENT_URI)
				.withSelection(ContactsContract.RawContacts._ID + "=?",
						new String[] { dataId }).build());
		return ops;
	}

	public ArrayList<Long> getRawContactIdByName(String name)
	{
		ContentResolver contentResolver = context.getContentResolver();

		// Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

		// Create query column array.
		String queryColumnArr[] = {ContactsContract.RawContacts._ID};

		// Create where condition clause.
		String displayName = name;
		String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

		// Query raw contact id through RawContacts uri.
		Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

		// Return the query cursor.
		Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

		ArrayList<Long> rawContactId = new ArrayList<>();

		if(cursor!=null) {
			// Get contact count that has same display name, generally it should be one.
			int queryResultCount = cursor.getCount();
			Log.d("TAG", "getDataIdByName: ===>"+queryResultCount );
			cursor.moveToFirst();
			// This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
			if(queryResultCount > 0)
			{
				while (!cursor.isAfterLast()) {
					rawContactId.add(cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID))); ;
					cursor.moveToNext();
				}
			}

			cursor.close();
		}

		Log.d(TAG, "getRawContact ID: "+rawContactId );

		return rawContactId;
	}

	public ArrayList<Long> getDataIdByName(String fname, String lname)
	{
		ContentResolver contentResolver = context.getContentResolver();

		// Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

		// Create query column array.
		String queryColumnArr[] = {ContactsContract.Data._ID,ContactsContract.Data.MIMETYPE};

		// Create where condition clause.
		String displayName = fname + " " + lname;
		String whereClause = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

		// Query raw contact id through RawContacts uri.
		Uri rawContactUri = ContactsContract.Data.CONTENT_URI;

		// Return the query cursor.
		Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

		ArrayList<Long> rawContactId = new ArrayList<>();

		if(cursor!=null)
		{
			// Get contact count that has same display name, generally it should be one.
			int queryResultCount = cursor.getCount();
			cursor.moveToFirst();
			// This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
			if(queryResultCount > 0)
			{
				while (!cursor.isAfterLast()) {

					if(cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE)).equalsIgnoreCase(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
						rawContactId.add(cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID))); ;
					}
					//rawContactId.add(cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID))); ;
					cursor.moveToNext();
				}
			}

			cursor.close();
		}

		Log.d(TAG, "getRawContact ID: "+rawContactId );
		return rawContactId;
	}

	public ArrayList<ContentProviderOperation> updateContactNumber(
			String dataId, String mobileNumber) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation.newUpdate(
				ContactsContract.Data.CONTENT_URI)
				.withSelection(ContactsContract.CommonDataKinds.Phone._ID + "=? AND " +
								ContactsContract.Data.MIMETYPE + "='" +
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
						new String[]{dataId})
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber).build());
		return ops;
	}

	public ArrayList<ContentProviderOperation> updateContactName(
			String dataId, String name) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(ContactsContract.Data._ID + "=?",
						new String[] { dataId })
				.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

		return ops;
	}
}
