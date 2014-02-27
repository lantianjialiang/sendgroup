package com.lantianjialiang.sendgroup;

import java.util.ArrayList;
import java.util.List;

import com.lantianjialiang.sendgroup.pojo.Contact;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseContactsActivity extends Activity {
	
	public final static String EXTRA_GROUP = "com.lantianjialiang.sendgroup.GROUP";
	public final static String EXTRA_RESULT = "com.lantianjialiang.sendgroup.RESULT";
	public final static String EXTRA_RESULT2 = "com.lantianjialiang.sendgroup.RESULT2";
	
	private MyAdapter adapter = null;
	private List<Contact> contacts = null;
	private ListView lv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main_contacts);
		
		
		adapter = new MyAdapter();
		contacts = Utils.getAllContact(this.getContentResolver());
		adapter.setList(contacts);
		lv = (ListView) findViewById(R.id.gropuList2);
		lv.setAdapter(adapter);
		
		Button button = (Button) findViewById(R.id.btnCancel);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra(EXTRA_RESULT, false);
				setResult(Activity.RESULT_OK, resultIntent);
				
				ChooseContactsActivity.this.finish();
			}
			
		});		
		
		button = (Button) findViewById(R.id.btnAdd);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Intent intent = ChooseContactsActivity.this.getIntent();
//				Group group = intent.getExtras().getParcelable(EXTRA_GROUP);
				String[] contacts = getSelectedContacts();
				
				Intent resultIntent = new Intent();
				resultIntent.putExtra(EXTRA_RESULT, true);
				resultIntent.putExtra(EXTRA_RESULT2, contacts);
				setResult(Activity.RESULT_OK, resultIntent);
				
				ChooseContactsActivity.this.finish();
			}
			
		});	
	}

	protected String[] getSelectedContacts() {
		List<String> contacts2 = new ArrayList<String>();
		for(int i = 0; i < contacts.size(); i ++) {
			if(contacts.get(i).isSend) {
				contacts2.add(contacts.get(i).name);
			}
		}
		return contacts2.toArray(new String[0]);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.choose_contacts, menu);
//		return true;
//	}
	
	private class MyAdapter extends BaseAdapter {
		private List<Contact> list;

		public void setList(List<Contact> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			final int myPosition = position;
			if (convertView == null) {

				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.contacts, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.text_contact_name);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.check_contact);
				convertView.setTag(holder);

				holder.checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						if (cb.isChecked()) {
							list.get(myPosition).isSend = true;
						}
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.name.setText(list.get(position).name);
			
			return convertView;
		}
		
		  private class ViewHolder {
			   TextView name;
			   CheckBox checkBox;
			  }
	}

}
