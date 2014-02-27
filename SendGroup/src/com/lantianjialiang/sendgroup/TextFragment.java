package com.lantianjialiang.sendgroup;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lantianjialiang.sendgroup.pojo.Group;

public class TextFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "text_section_number";

	private final List<String> list = new ArrayList<String>();

	private ArrayAdapter<String> dataAdapter = null;

	public TextFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_text,
				container, false);

		Spinner groupSpinner = (Spinner) rootView
				.findViewById(R.id.spinner_group);

		list.add("list 1");
		list.add("list 2");
		list.add("list 3");

		dataAdapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groupSpinner.setAdapter(dataAdapter);

		return rootView;
	}

	public void updateGroupList(List<Group> groups) {
		list.clear();

		for (int i = 0; i < groups.size(); i++) {
			list.add(groups.get(i).name);
		}

		dataAdapter.notifyDataSetChanged();
	}
}