package com.lantianjialiang.sendgroup.pojo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable{

	public String name;
	public List<Contact> contacts = null;
	
	public Group() {
		name = "";
		contacts = new ArrayList<Contact>();
	}

	public Group(Parcel in) {
		name = in.readString();
		Object[] dd = in.readArray((Contact.class.getClassLoader()));
		Contact[] contacts2 = (Contact[]) dd;
		contacts = new ArrayList<Contact>();
		for(int i = 0; i < contacts2.length; i ++) {
			contacts.add(contacts2[i]);
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeArray(contacts.toArray(new Contact[contacts.size()]));
	}
	
	public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
		public Group createFromParcel(Parcel in) {
			return new Group(in);
		}

		public Group[] newArray(int size) {
			return new Group[size];
		}
	};

}
