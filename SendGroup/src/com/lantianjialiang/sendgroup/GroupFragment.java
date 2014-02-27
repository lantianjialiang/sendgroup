package com.lantianjialiang.sendgroup;

import java.util.ArrayList;
import java.util.List;

import com.lantianjialiang.sendgroup.pojo.Contact;
import com.lantianjialiang.sendgroup.pojo.Group;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GroupFragment extends Fragment {
	
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "group_section_number";
	 
	private List<Group> groups = new ArrayList<Group>();
	
	private MyAdapter adapter = null;
	private ListView lv = null;
	private Group currentGroup = null;
	private TextFragment textFragment = null;
	
	public GroupFragment() {
	}
	
	public void setTextFragment(TextFragment textFragment){
		this.textFragment = textFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_group,
				container, false);
		
		adapter = new MyAdapter();
		adapter.setList(groups);
		lv = (ListView) rootView.findViewById(R.id.gropuList);
		lv.setAdapter(adapter);
		
		Button button = (Button) rootView.findViewById(R.id.btnCreate);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				createGroup();
			}
			
		});
		
		this.registerForContextMenu(lv);
		
		return rootView;
	}
	
	public List<Group> getGroups() {
		return groups;
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
//		Toast.makeText(getActivity(),
//				"You have selected " + FRUITS[position], Toast.LENGTH_SHORT)
//				.show();
		
		Object currentGroup = lv.getSelectedItem();
		currentGroup.toString();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Group Operation");
		menu.add(0, v.getId(), 0, "Rename");
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Add Contacts");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Group group = groups.get(info.position);
		if(group == null || group.name == null) {
			return false;
		}
		
		if (item.getTitle() == "Add Contacts") {
			addContactsToGroup(group);
		} else if (item.getTitle() == "Rename") {
			renameGroup(group.name);
		} else if (item.getTitle() == "Delete") {
			deleteGroup(group.name);
		} else {
			return false;
		}
		return true;
	}

	private void createGroup() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

		alert.setTitle("Create group");
		alert.setMessage("Please enter group name:");

		final EditText input = new EditText(this.getActivity());
		alert.setView(input);

		final boolean[] isSuccess = new boolean[1];
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				isSuccess[0] = addGroup(value);
				
				String message = "";
				if(isSuccess[0]) {
					message = "Create group done.";
				} else {
					message = "Create group failed.";
				}
				
				Builder builder = new AlertDialog.Builder(GroupFragment.this.getActivity());
				
				builder.setMessage(message);
				builder.setCancelable(true);
				builder.setPositiveButton("OK", new OkOnClickListener());
				AlertDialog dialog2 = builder.create();
				dialog2.show();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}
	
	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			// do nothing
		}
	}
	
	protected void addContactsToGroup(Group group) {
		currentGroup = group;
	    Intent intent = new Intent(this.getActivity(), ChooseContactsActivity.class);
	    intent.putExtra(ChooseContactsActivity.EXTRA_GROUP, group);
	    startActivityForResult(intent, 1);
	}
	
	public void addContactsToGroup(String[] contacts) {
		if(currentGroup == null) {
			return;
		}
		
		Contact c = null;
		for(int i = 0; i < contacts.length; i ++) {
			c = new Contact();
			c.name = contacts[i];
			currentGroup.contacts.add(c);
		}
	}

	protected void deleteGroup(final String value) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

		alert.setTitle("Delete group");
		alert.setMessage("Are you sure to delet this group?");
		
		final boolean[] isSuccess = new boolean[1];
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				isSuccess[0] = deleteGroup2(value);
				
				String message = "";
				if(isSuccess[0]) {
					message = "Delete group done.";
				} else {
					message = "Delete group failed.";
				}
				
				Builder builder = new AlertDialog.Builder(GroupFragment.this.getActivity());
				
				builder.setMessage(message);
				builder.setCancelable(true);
				builder.setPositiveButton("OK", new OkOnClickListener());
				AlertDialog dialog2 = builder.create();
				dialog2.show();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
		
	}

	protected void renameGroup(final String oldValue) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

		alert.setTitle("Rename group");
		alert.setMessage("Please enter the new group name:");

		final EditText input = new EditText(this.getActivity());
		alert.setView(input);

		final boolean[] isSuccess = new boolean[1];
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				isSuccess[0] = renameGroup(oldValue,value);
				
				String message = "";
				if(isSuccess[0]) {
					message = "Rename group done.";
				} else {
					message = "Rename group failed.";
				}
				
				Builder builder = new AlertDialog.Builder(GroupFragment.this.getActivity());
				
				builder.setMessage(message);
				builder.setCancelable(true);
				builder.setPositiveButton("OK", new OkOnClickListener());
				AlertDialog dialog2 = builder.create();
				dialog2.show();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
		
	}
	
	protected boolean addGroup(String value) {
		if(value == null || value.trim().length() == 0) {
			return false;
		}
		
		for(int i = 0; i < groups.size(); i ++) {
			if(groups.get(i).name.equals(value)) {
				return false;
			}
		}
		
		Group group = new Group();
		group.name = value;
		groups.add(group);
				
		adapter.notifyDataSetChanged();
		
		if(textFragment != null) {
			textFragment.updateGroupList(groups);	
		}
		
		return true;
	}
	
	protected boolean renameGroup(String oldName, String value) {
		if(oldName == null || oldName.trim().length() == 0 ||
				value == null || value.trim().length() == 0) {
			return false;
		}
		
		Group group = null;
		for(int i = 0; i < groups.size(); i ++) {
			if(groups.get(i).name.equals(oldName)) {
				group = groups.get(i);
			}
		}
		
		if(group == null) {
			return false;
		}
		
		group.name = value;
		adapter.notifyDataSetChanged();
		
		return true;
	}
	
	protected boolean deleteGroup2(String value) {
		if(value == null || value.trim().length() == 0) {
			return false;
		}
		
		int index = -1;
		for(int i = 0; i < groups.size(); i ++) {
			if(groups.get(i).name.equals(value)) {
				index = i;
			}
		}
		
		if(index == -1) {
			return false;
		}
		
		groups.remove(index);
		
		adapter.notifyDataSetChanged();
		
		return true;
	}
	
	private class MyAdapter extends BaseAdapter {
		private List<Group> list;

		public void setList(List<Group> list) {
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
			/*
			 * convertViewitemitemitem item
			 */
			TextView textView = null;
			if (convertView == null) { // item View
				textView = new TextView(getActivity());
				/*
				 * item
				 */
			} else {
				textView = (TextView) convertView; //
			}
			textView.setText(list.get(position).name);
			textView.setPadding(20, 20, 20, 20);
			textView.setBackgroundColor(Color.GREEN);
			return textView;
		}
	}

}