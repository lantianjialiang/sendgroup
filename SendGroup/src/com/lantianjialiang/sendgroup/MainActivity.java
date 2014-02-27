package com.lantianjialiang.sendgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.lantianjialiang.sendgroup.pojo.Contact;
import com.lantianjialiang.sendgroup.pojo.Group;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	GroupFragment groupFragment =  null;
	TextFragment textFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
		  boolean isAdd = data.getBooleanExtra(ChooseContactsActivity.EXTRA_RESULT, false);
		  if(!isAdd) {
			  return;
		  }
		  
		  String[] contacts = data.getStringArrayExtra(ChooseContactsActivity.EXTRA_RESULT2);
		  if(contacts == null || contacts.length == 0) {
			  return;
		  }
		  
		  groupFragment.addContactsToGroup(contacts);
		  
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_create_group:
//            	createGroup();
//                return true;
            case R.id.action_send:
            	sendMessage();
                return true;
        }
        return false;
    }

	private void sendMessage() {
		// 1. get all contact
		List<Contact> contacts = Utils.getAllContact(getContentResolver());
		if(contacts.size() == 0) {
			return;
		}
		
		Spinner mSpinner = (Spinner)findViewById(R.id.spinner_group);
		String groupName = (String)mSpinner.getSelectedItem();
		
		List<Group> groups = groupFragment.getGroups();
		Group group = Utils.getGroupByName(groups, groupName);
		contacts = Utils.getContactsFromGroup(group.contacts, contacts);
		if(contacts.size() == 0) {
			return;
		}
		
		// 2. send message
		EditText mEdit = (EditText)findViewById(R.id.section_label);
		String text = mEdit.getText().toString();
		if(text.length() <= 0) {
			return;
		}
		
		for(int i = 0; i < contacts.size(); i ++) {
			sendMessage(contacts.get(i), text);
		}	
		
		showResult(contacts);
	}
	
	private void showResult(List<Contact> contacts) {
		int failCount = 0;
		for(int i = 0; i < contacts.size(); i ++) {
			if(!contacts.get(i).isSend) {
				failCount = failCount + 1;
			}
		}
		
		
		Builder builder = new AlertDialog.Builder(this);
		String message = "We totally send " + contacts.size() + " messages. \n";
		if(failCount > 0) {
			message = message + "failed to send messages count is " + failCount;
		}
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton("OK", new OkOnClickListener());
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			// do nothing
		}
	}
	
	private void sendMessage(Contact contact, String text) {
		if (contact.telNumbers.size() <= 0) {
			return;
		}
		
		String sms = "Hi, " + contact.name + ". " + text;

		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(contact.telNumbers.get(0), null, sms, null, null);
			contact.isSend = true;
		} catch (Exception e) {
			contact.isSend = false;
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			Bundle args = new Bundle();
			
			if(position == 1) {
				textFragment = new TextFragment();
				groupFragment.setTextFragment(textFragment);
				fragment = textFragment;
				args.putInt(TextFragment.ARG_SECTION_NUMBER, position + 1);
			} else if(position == 0) {
				groupFragment = new GroupFragment();
				fragment = groupFragment;
				args.putInt(GroupFragment.ARG_SECTION_NUMBER, position + 1);
			}			
			
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}



}
