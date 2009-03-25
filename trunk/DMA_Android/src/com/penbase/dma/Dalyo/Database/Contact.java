package com.penbase.dma.Dalyo.Database;

import android.database.Cursor;
import android.provider.Contacts;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.Organizations;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.View.ApplicationView;

import java.util.ArrayList;
import java.util.HashMap;

public class Contact {
	private Cursor mContactsCursor;
	private String mTableId;
	private HashMap<String, ArrayList<String>> mFields;
	
	//Names
	private String mFirstName = null;
	private String mLastName = null;
	
	//Phone numbers
	private String mWorkPhone = null;
	private String mHomePhone = null;
	private String mMobilePhone = null;
	private String mCompanyPhone = null;
	private String mOtherPhone = null;
	private String mFaxWork = null;
	
	//Addresses
	private String mEmail = null;
	private String mEmailWork = null;
	private String mImWork = null;
	private String mStreet = null;
	private String mStreetWork = null;
	
	//Organization
	private String mCompany = null;
	
	public Contact(String tableId, HashMap<String, ArrayList<String>> fields) {
		this.mTableId = tableId;
		this.mFields = fields;
		String[] contactsProjection = new String[] {People._ID, People.NAME};
		mContactsCursor = ApplicationView.getCurrentView().managedQuery(People.CONTENT_URI, contactsProjection, null, null, null);
		readContacts();
	}
	
	private void readContacts() {
		int contactsCursorCount = mContactsCursor.getCount();
		if (contactsCursorCount > 0) {
			mContactsCursor.moveToFirst();
			for (int i=0; i<contactsCursorCount; i++) {
				ArrayList<Integer> fieldsList = new ArrayList<Integer>();
				ArrayList<Object> record = new ArrayList<Object>();
				Cursor allRowsCursor = DatabaseAdapter.selectQuery(mTableId, null, null);
				int newId = allRowsCursor.getCount()+1;
				DatabaseAdapter.closeCursor(allRowsCursor);
				record.add(newId);
				String[] contactsColumns = mContactsCursor.getColumnNames();
				setValue(mContactsCursor, contactsColumns);
				for (String key : mFields.keySet()) {
					fieldsList.add(Integer.valueOf(key));
					if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.LASTNAME) {
						record.add(mLastName);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.FIRSTNAME) {
						record.add(mFirstName);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.PHONEWORK) {
						record.add(mWorkPhone);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.HOMEPHONE) {
						record.add(mHomePhone);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.MOBILEPHONE) {
						record.add(mMobilePhone);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.PHONE2WORK) {
						record.add(mOtherPhone);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.FAXWORK) {
						record.add(mFaxWork);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.COMPANYPHONE) {
						record.add(mCompanyPhone);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.EMAIL) {
						record.add(mEmail);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.EMAILWORK) {
						record.add(mEmailWork);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.IMWORK) {
						record.add(mImWork);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.STREET) {
						record.add(mStreet);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.STREETWORK) {
						record.add(mStreetWork);
					}
					else if (Integer.valueOf(mFields.get(key).get(1)) == DatabaseAttribute.COMPANY) {
						record.add(mCompany);
					}
					else {
						record.add(null);
					}
				}
				//insert contact value to db
				DatabaseAdapter.addQuery(Integer.valueOf(mTableId), fieldsList, record);
				mContactsCursor.moveToNext();
			}
		}
		DatabaseAdapter.closeCursor(mContactsCursor);
	}
	
	private void setValue (Cursor cursor, String[] columns) {
		for (String column : columns) {
			if (column.equals(People.NAME)) {
				String name = cursor.getString(cursor.getColumnIndex(column));
			
				if (!cursor.isNull(cursor.getColumnIndex(column))) {
					mFirstName = name.split(" ")[0];
					if (name.indexOf(" ") != -1) {
						mLastName = name.substring(name.indexOf(" ")+1, name.length());
					}
				}
			}
			else if (column.equals(People._ID)) {
				String whereClause = "person="+mContactsCursor.getString(mContactsCursor.getColumnIndex(column));
				
				String[] phonesProjection = new String[] {Phones.NUMBER, Phones.TYPE};
				Cursor phonesCursor = ApplicationView.getCurrentView().managedQuery(Contacts.Phones.CONTENT_URI, phonesProjection, whereClause, null, null);
				int phonesCursorCount = phonesCursor.getCount();
				if (phonesCursorCount > 0) {
					phonesCursor.moveToFirst();
					int numberColumn = phonesCursor.getColumnIndex(Phones.NUMBER);
					int typeColumn = phonesCursor.getColumnIndex(Phones.TYPE);
					for (int j=0; j<phonesCursorCount; j++) {
						switch (phonesCursor.getInt(typeColumn)) {
							case Phones.TYPE_WORK:
								mWorkPhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_HOME:
								mHomePhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_MOBILE:
								mMobilePhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_CUSTOM:
								mCompanyPhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_OTHER:
								mOtherPhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_FAX_WORK:
								mFaxWork = phonesCursor.getString(numberColumn);
								break;
						}
						phonesCursor.moveToNext();
					}
				}
				DatabaseAdapter.closeCursor(phonesCursor);

				String[] addressesProjection = new String[] {ContactMethods.DATA, ContactMethods.TYPE, ContactMethods.KIND};
				Cursor addressesCursor = ApplicationView.getCurrentView().managedQuery(Contacts.ContactMethods.CONTENT_URI, addressesProjection, whereClause, null, null);
				int addressesCursorCount = addressesCursor.getCount();
				if (addressesCursorCount > 0) {
					addressesCursor.moveToFirst();
					int dataColumn = addressesCursor.getColumnIndex(ContactMethods.DATA);
					int typeColumn = addressesCursor.getColumnIndex(ContactMethods.TYPE);
					int kindColumn = addressesCursor.getColumnIndex(ContactMethods.KIND);
					for (int j=0; j<addressesCursorCount; j++) {
						switch (addressesCursor.getInt(typeColumn)) {
							case ContactMethods.TYPE_HOME:
								switch (addressesCursor.getInt(kindColumn)) {
									//mail
									case 1:
										mEmail = addressesCursor.getString(dataColumn);
										break;
									//address
									case 2:
										mStreet = addressesCursor.getString(dataColumn);
										break;
								}
								break;
							case ContactMethods.TYPE_WORK:
								switch (addressesCursor.getInt(kindColumn)) {
									case 1:
										mEmailWork = addressesCursor.getString(dataColumn);
										break;
									case 2:
										mStreetWork = addressesCursor.getString(dataColumn);
										break;
								}
								break;
							case ContactMethods.TYPE_OTHER:
								mImWork = addressesCursor.getString(dataColumn);
								break;
						}
						addressesCursor.moveToNext();
					}
				}
				DatabaseAdapter.closeCursor(addressesCursor);
				
				String[] organizationsProjection = new String[] {Organizations.COMPANY};
				Cursor organizationsCursor = ApplicationView.getCurrentView().managedQuery(Contacts.Organizations.CONTENT_URI, organizationsProjection, whereClause, null, null);
				int organizationsCursorCount = organizationsCursor.getCount();
				if (organizationsCursorCount > 0) {
					organizationsCursor.moveToFirst();
					int organizationColumn = organizationsCursor.getColumnIndex(Organizations.COMPANY);
					for (int j=0; j<organizationsCursorCount; j++) {
						mCompany = organizationsCursor.getString(organizationColumn);
						organizationsCursor.moveToNext();
					}
				}
				DatabaseAdapter.closeCursor(organizationsCursor);
			}
		}
	}
}
