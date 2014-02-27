package com.lantianjialiang.sendgroup;

import java.util.ArrayList;
import java.util.List;

import com.lantianjialiang.sendgroup.pojo.Contact;
import com.lantianjialiang.sendgroup.pojo.Group;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

public class Utils {
	public static List<Contact> getAllContact(ContentResolver cr) {
		List<Contact> contacts = new ArrayList<Contact>();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cur.getCount() <= 0) {
        	return contacts;
        } 
        
		while (cur.moveToNext()) {
			String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			
			Contact contact = new Contact();
			contact.name = name;
			
			if (Integer.parseInt(cur.getString(cur
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				Cursor pCur = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[] { id }, null);
				while (pCur.moveToNext()) {
					String phoneNo = pCur.getString(pCur
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					contact.telNumbers.add(phoneNo);
				}
				pCur.close();
			}
			
			contacts.add(contact);
		}
		
		return contacts;
	}

	public static List<Contact> getContactsFromGroup(List<Contact> specifiedContacts,
			List<Contact> contacts) {
		for(int i = 0; i < specifiedContacts.size(); i ++) {
			for(int j = 0 ; j < contacts.size(); j ++) {
				if(specifiedContacts.get(i).name.equals(contacts.get(j).name)) {
					specifiedContacts.get(i).telNumbers = contacts.get(j).telNumbers;
				}
			}
		}
		
		return specifiedContacts;
	}

	public static Group getGroupByName(List<Group> groups, String groupName) {
		for(int i = 0; i < groups.size(); i ++) {
			if(groups.get(i).name.equals(groupName)) {
				return groups.get(i);
			}
			
		}
		return null;
	}
}
