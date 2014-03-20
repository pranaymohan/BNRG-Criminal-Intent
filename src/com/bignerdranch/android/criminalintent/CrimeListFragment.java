package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	
	//Logging tag
	private static final String TAG = "CrimeListFragment";
	
	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleShown = false;
	private Button mAddCrimeButton;
	
	//Retrieves each crime instance from mCrimes and formats it into
	//a convenient list item view, as described in layout.list_item_crime
	private class CrimeAdapter extends ArrayAdapter<Crime> {
		
		public CrimeAdapter(ArrayList<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
			}
			
			//Configure view for this crime
			Crime c = getItem(position);
			
			TextView titleTextView = (TextView)convertView.findViewById(R.id.crime_list_item_title_text);
			titleTextView.setText(c.getTitle());
			
			TextView dateTextView = (TextView)convertView.findViewById(R.id.crime_list_item_date_text);
			dateTextView.setText(c.getSimpleDate());
			
			CheckBox solvedCheckBox = (CheckBox)convertView.findViewById(R.id.crime_list_item_solved_check_box);
			solvedCheckBox.setChecked(c.isSolved());
			
			return convertView;
		}
	}
	
	//For whenever a button needs to add a crime
	private void addCrime() {
		//Create a new crime, add it to the Crime array in CrimeLab
		Crime c = new Crime();
		CrimeLab.get(getActivity()).addCrime(c);
		//Package its id and call CrimePagerActivity to edit new crime
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivityForResult(i, 0);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		//To let fragment manager know that this fragment needs to receive options menu callbacks
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.crimes_title);
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		
		//Create an adapter for CrimeListFragment's default ListView
		//Constructor uses a context (the activity), the layout (a pre-defined list layout), and the array (mCrimes)
		//ArrayAdapter<Crime> adapter = new ArrayAdapter<Crime>(getActivity(), android.R.layout.simple_list_item_1, mCrimes);
		//Use a ListFragment convenience method to set the adapter of the default ListView
		
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		//View v = super.onCreateView(inflater, parent, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_list_crime, null);
		
		//In case of empty list, need to inflate button to add crime
		mAddCrimeButton = (Button)v.findViewById(R.id.add_new_crime);
		mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addCrime();
			}
		});
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleShown) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		
		//Get the list view and register for context menu
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			//Use floating context menu in any pre-honeycomb devices
			registerForContextMenu(listView);
		} else {
			//Show context menu on action bar for any post-honeycomb devices
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		}
		
		//Create a MultiChoiceModeListener to receive the events for the CHOICE_MODE_MULTIPLE_MODAL
		//that was set on the listView above.
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			//Whenever the listview is in action mode, and one of the lis items is clicked, this method is executed
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				//Case where "Delete Crime" item was selected
					case R.id.menu_item_delete_crime:
						CrimeAdapter crimeAdapter = (CrimeAdapter)getListAdapter();
						//for (int i=crimeAdapter.getCount()-1; i>=0; i--) { <WHY NOT THE ONE BELOW>
						for (int i=crimeAdapter.getCount(); i>=0; i--) {
							if (getListView().isItemChecked(i)) {
								Crime c = crimeAdapter.getItem(i);
								CrimeLab.get(getActivity()).deleteCrime(c);
							}
						}
						//Exits the action mode
						mode.finish();
						crimeAdapter.notifyDataSetChanged();
						return true;
					default: return false;
				}
			}
			//This method is called when action mode is created
			//Inflates the context menu on top of the action bar
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.crime_list_item_context, menu);
				return true;
			}
			public void onDestroyActionMode(ActionMode mode) {
				//Required method, but not used in this implementation	
			}
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				//Required method, but not used in this implementation
				return false;
			}
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				//Required method, but not used in this implementation
			}
		});
		
		return v;
	}
	
	//Override onListItemClick method of ListFragment to actually do something
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Crime c = (Crime)getListAdapter().getItem(position);
		//Log.d(TAG, c.getTitle()+" was clicked!");
		//Create intent to start the new activity
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(i);
	}
	
	//Override onResume so that list refreshes every time CrimeListFragment resumes from being paused
	//This is useful when someone views a Crime detail from the list, modifies it and then hits back.
	//This pops the detail fragment off the stack and resumes the list fragment, where it should now be updated
	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent i) {
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		
		MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleShown && subtitleItem != null) {
			subtitleItem.setTitle(R.string.hide_subtitle);
		}
	}
	
	//Regarding show subtitle text:
	//Only have to warn off lint, and don't need to wrap code in if(Build>11) statements
	//because the specific menu resource id will only be presented if the build > 11
	@TargetApi(11)	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Use a switch statement to have the options menu behave differently
		//based on which item id was received
		switch (item.getItemId()) {
			//Case where "New Crime" item was selected
			case R.id.menu_item_new_crime:
				addCrime();
				return true;
			case R.id.menu_item_show_subtitle:
				if (getActivity().getActionBar().getSubtitle() == null) {
					getActivity().getActionBar().setSubtitle(R.string.subtitle);
					item.setTitle(R.string.hide_subtitle);
					mSubtitleShown = true;
				} else {
					getActivity().getActionBar().setSubtitle(null);
					item.setTitle(R.string.show_subtitle);
					mSubtitleShown = false;
				}
				return true;
			default:
				//Otherwise, use default methods (which is do nothing)
				return super.onOptionsItemSelected(item);
		}	
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		CrimeAdapter crimeAdapter = (CrimeAdapter)getListAdapter();
		Crime c = crimeAdapter.getItem(position);
		
		switch (item.getItemId()) {
		//Case where "Delete Crime" item was selected
		case R.id.menu_item_delete_crime:
			CrimeLab.get(getActivity()).deleteCrime(c);
			crimeAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

}
