package com.gazematic.gojekcontacts.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gazematic.gojekcontacts.R;
import com.gazematic.gojekcontacts.databinding.ActivityMainBinding;
import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.utils.CircularContactView;
import com.gazematic.gojekcontacts.utils.PinnedHeaderListView;
import com.gazematic.gojekcontacts.utils.SearchablePinnedHeaderListViewAdapter;
import com.gazematic.gojekcontacts.utils.StringArrayAlphabetIndexer;
import com.gazematic.gojekcontacts.viewmodel.ContactsViewModel;
import com.gazematic.gojekcontacts.viewmodel.ContactsViewModelContract;

import net.redwarp.library.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity implements ContactsViewModelContract.MainView {

    private LayoutInflater inflater;
    private PinnedHeaderListView listView;
    private ContactsAdapter contactsAdapter;
   // private ArrayList<Contact> contacts;
    @BindView(R.id.no_contacts)
    AppCompatTextView noContacts;
    DatabaseHelper helper;
    @BindView(R.id.layout_view)
    CoordinatorLayout coordinatorLayout;
    ContactsViewModel contactsViewModel;
    ActivityMainBinding activityMainBinding;
    ContactsViewModelContract.MainView mainView = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(MainActivity.this);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);
        ContactsViewModel contactsViewModel = new ContactsViewModel(mainView, getApplicationContext());
        activityMainBinding.setContactsViewModel(contactsViewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.add_new:
                startActivity(new Intent(this, AddContactActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class ContactsAdapter extends SearchablePinnedHeaderListViewAdapter<Contact> {
        private ArrayList<Contact> mContacts;
        private final int CONTACT_PHOTO_IMAGE_SIZE;
        private final int[] PHOTO_TEXT_BACKGROUND_COLORS;

        @Override
        public CharSequence getSectionTitle(int sectionIndex) {
            return ((StringArrayAlphabetIndexer.AlphaBetSection) getSections()[sectionIndex]).getName();
        }

        public ContactsAdapter(final ArrayList<Contact> contacts) {
            setData(contacts);
            PHOTO_TEXT_BACKGROUND_COLORS = getResources().getIntArray(R.array.contacts_text_background_colors);
            CONTACT_PHOTO_IMAGE_SIZE = getResources().getDimensionPixelSize(
                    R.dimen.list_item__contact_imageview_size);
        }

        public void setData(final ArrayList<Contact> contacts) {
            this.mContacts = contacts;
            final String[] generatedContactNames = generateContactNames(contacts);
            setSectionIndexer(new StringArrayAlphabetIndexer(generatedContactNames, true));
        }

        private String[] generateContactNames(final List<Contact> contacts) {
            final ArrayList<String> contactNames = new ArrayList<String>();
            if (contacts != null)
                for (final Contact contactEntity : contacts)
                    contactNames.add(contactEntity.getFirstName());
            return contactNames.toArray(new String[contactNames.size()]);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            final View rootView;
            if (convertView == null) {
                holder = new ViewHolder();
                rootView = inflater.inflate(R.layout.contact_listview_item, parent, false);
                holder.profileCircularContactView = (CircularContactView) rootView
                        .findViewById(R.id.listview_item__image);
                holder.profileCircularContactView.getTextView().setTextColor(0xFFffffff);
                holder.userName = (TextView) rootView
                        .findViewById(R.id.listview_item__name);
                holder.headerView = (TextView) rootView.findViewById(R.id.header_text);
                rootView.setTag(holder);
            } else {
                rootView = convertView;
                holder = (ViewHolder) rootView.getTag();
            }
            final Contact contact = getItem(position);
            final String displayName = contact.getFirstName();
            holder.userName.setText(displayName);

            final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                    % PHOTO_TEXT_BACKGROUND_COLORS.length];
            if (TextUtils.isEmpty(displayName))
                holder.profileCircularContactView.setImageResource(R.drawable.ic_person_24dp,
                        backgroundColorToUse);
            else {
                final String characterToShow = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1).toUpperCase(Locale.getDefault());
                holder.profileCircularContactView.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
            }
            bindSectionHeader(holder.headerView, null, position);
            return rootView;
        }

        @Override
        public boolean doFilter(final Contact item, final CharSequence constraint) {
            if (TextUtils.isEmpty(constraint))
                return true;
            final String displayName = item.getFirstName();
            return !TextUtils.isEmpty(displayName) && displayName.toLowerCase(Locale.getDefault())
                    .contains(constraint.toString().toLowerCase(Locale.getDefault()));
        }

        @Override
        public ArrayList<Contact> getOriginalList() {
            return mContacts;
        }


    }

    private static class ViewHolder {
        public CircularContactView profileCircularContactView;
        TextView userName, headerView;
    }


    @Override
    public Context getContext() {
        return MainActivity.this;
    }

    @Override
    public void loadData(final ArrayList<Contact> contacts) {
        //final ArrayList<Contact> contacts = getContacts();
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                char lhsFirstLetter = TextUtils.isEmpty(lhs.getFirstName()) ? ' ' : lhs.getFirstName().charAt(0);
                char rhsFirstLetter = TextUtils.isEmpty(rhs.getFirstName()) ? ' ' : rhs.getFirstName().charAt(0);
                int firstLetterComparison = Character.toUpperCase(lhsFirstLetter) - Character.toUpperCase(rhsFirstLetter);
                if (firstLetterComparison == 0)
                    return lhs.getFirstName().compareTo(rhs.getFirstName());
                return firstLetterComparison;
            }
        });
        listView = (PinnedHeaderListView) findViewById(list);
        contactsAdapter = new ContactsAdapter(contacts);

        int pinnedHeaderBackgroundColor = getResources().getColor(R.color.pinned_header_text_bg);
        contactsAdapter.setPinnedHeaderBackgroundColor(pinnedHeaderBackgroundColor);
        contactsAdapter.setPinnedHeaderTextColor(getResources().getColor(R.color.pinned_header_text));
        listView.setPinnedHeaderView(inflater.inflate(R.layout.pinned_header_listview_side_header, listView, false));
        listView.setAdapter(contactsAdapter);
        listView.setOnScrollListener(contactsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", contacts.get(i).getId());
                startActivity(intent);
                Log.v("Kontak", "listview item click: " + i);
            }
        });
    }
}
