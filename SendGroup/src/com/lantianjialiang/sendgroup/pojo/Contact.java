package com.lantianjialiang.sendgroup.pojo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable{
	
	public String name = "";
	
	public List<String> telNumbers = null;

	public boolean isSend = false;
	
	public Contact() {
		telNumbers = new ArrayList<String>();
	}

	public Contact(Parcel in) {
		name = in.readString();
		String[] telNumbers2 = (String[]) (in.readArray((String.class.getClassLoader())));
		telNumbers = new ArrayList<String>();
		for(int i = 0; i < telNumbers2.length; i ++) {
			telNumbers.add(telNumbers2[i]);
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeArray(telNumbers.toArray(new String[0]));
	}
	
	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};

}
