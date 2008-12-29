package com.penbase.dma.Dalyo.Database;

import java.util.ArrayList;
import java.util.HashMap;

import com.penbase.dma.Constant.DatabaseField;
import com.penbase.dma.View.ApplicationView;

import android.database.Cursor;
import android.provider.Contacts;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.Organizations;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;

public class Contact {
	private Cursor contactsCursor;
	private String tableId;
	private HashMap<String, ArrayList<String>> fields;
	
	//Names
	private String firstName = null;
	private String lastName = null;
	
	//Phone numbers
	private String workPhone = null;
	private String homePhone = null;
	private String mobilePhone = null;
	private String companyPhone = null;
	private String otherPhone = null;
	private String faxWork = null;
	
	//Addresses
	private String email = null;
	private String emailWork = null;
	private String imWork = null;
	private String street = null;
	private String streetWork = null;
	
	//Organization
	private String company = null;
	
	public Contact(String tableId, HashMap<String, ArrayList<String>> fields) {
		this.tableId = tableId;
		this.fields = fields;
		String[] contactsProjection = new String[] {People._ID, People.NAME};
		contactsCursor = ApplicationView.getCurrentView().managedQuery(People.CONTENT_URI, contactsProjection, null, null, null);
		readContacts();
	}
	
	private void readContacts() {
		int contactsCursorCount = contactsCursor.getCount();
		if (contactsCursorCount > 0) {
			contactsCursor.moveToFirst();
			for (int i=0; i<contactsCursor.getCount(); i++) {
				ArrayList<Integer> fieldsList = new ArrayList<Integer>();
				ArrayList<Object> record = new ArrayList<Object>();
				Cursor allRowsCursor = DatabaseAdapter.selectQuery(tableId, null, null);
				int newId = allRowsCursor.getCount()+1;
				closeCursor(allRowsCursor);
				record.add(newId);
				String[] contactsColumns = contactsCursor.getColumnNames();
				setValue(contactsCursor, contactsColumns);
				for (String key : fields.keySet()) {
					fieldsList.add(Integer.valueOf(key));
					if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.LASTNAME) {
						record.add(lastName);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.FIRSTNAME) {
						record.add(firstName);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.PHONEWORK) {
						record.add(workPhone);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.HOMEPHONE) {
						record.add(homePhone);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.MOBILEPHONE) {
						record.add(mobilePhone);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.PHONE2WORK) {
						record.add(otherPhone);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.FAXWORK) {
						record.add(faxWork);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.COMPANYPHONE) {
						record.add(companyPhone);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.EMAIL) {
						record.add(email);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.EMAILWORK) {
						record.add(emailWork);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.IMWORK) {
						record.add(imWork);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.STREET) {
						record.add(street);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.STREETWORK) {
						record.add(streetWork);
					}
					else if (Integer.valueOf(fields.get(key).get(1)) == DatabaseField.COMPANY) {
						record.add(company);
					}
					else {
						record.add(null);
					}
				}
				//insert contact value to db
				DatabaseAdapter.addQuery(Integer.valueOf(tableId), fieldsList, record);
				contactsCursor.moveToNext();
			}
		}
		closeCursor(contactsCursor);
	}
	
	private void setValue (Cursor cursor, String[] columns) {
		for (String column : columns) {
			if (column.equals(People.NAME)) {
				String name = cursor.getString(cursor.getColumnIndex(column));
			
				if (!cursor.isNull(cursor.getColumnIndex(column))) {
					firstName = name.split(" ")[0];
					if (name.indexOf(" ") != -1) {
						lastName = name.substring(name.indexOf(" ")+1, name.length());
					}
				}
			}
			else if (column.equals(People._ID)) {
				String whereClause = "person="+contactsCursor.getString(contactsCursor.getColumnIndex(column));
				
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
								workPhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_HOME:
								homePhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_MOBILE:
								mobilePhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_CUSTOM:
								companyPhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_OTHER:
								otherPhone = phonesCursor.getString(numberColumn);
								break;
							case Phones.TYPE_FAX_WORK:
								faxWork = phonesCursor.getString(numberColumn);
								break;
						}
						phonesCursor.moveToNext();
					}
				}
				closeCursor(phonesCursor);

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
										email = addressesCursor.getString(dataColumn);
										break;
									//address
									case 2:
										street = addressesCursor.getString(dataColumn);
										break;
								}
								break;
							case ContactMethods.TYPE_WORK:
								switch (addressesCursor.getInt(kindColumn)) {
									case 1:
										emailWork = addressesCursor.getString(dataColumn);
										break;
									case 2:
										streetWork = addressesCursor.getString(dataColumn);
										break;
								}
								break;
							case ContactMethods.TYPE_OTHER:
								imWork = addressesCursor.getString(dataColumn);
								break;
						}
						addressesCursor.moveToNext();
					}
				}
				closeCursor(addressesCursor);
				
				String[] organizationsProjection = new String[] {Organizations.COMPANY};
				Cursor organizationsCursor = ApplicationView.getCurrentView().managedQuery(Contacts.Organizations.CONTENT_URI, organizationsProjection, whereClause, null, null);
				int organizationsCursorCount = organizationsCursor.getCount();
				if (organizationsCursorCount > 0) {
					organizationsCursor.moveToFirst();
					int organizationColumn = organizationsCursor.getColumnIndex(Organizations.COMPANY);
					for (int j=0; j<organizationsCursorCount; j++) {
						company = organizationsCursor.getString(organizationColumn);
						organizationsCursor.moveToNext();
					}
				}
				closeCursor(organizationsCursor);
			}
		}
	}
	
	private void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close(); 
		}
	}
}
