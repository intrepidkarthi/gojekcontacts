package com.gazematic.gojekcontacts.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.data.KontakFactory;
import com.gazematic.gojekcontacts.model.Contact;
import com.gazematic.gojekcontacts.utils.CircularContactView;
import com.gazematic.gojekcontacts.utils.PinnedHeaderListView;
import com.gazematic.gojekcontacts.utils.SearchablePinnedHeaderListViewAdapter;
import com.gazematic.gojekcontacts.utils.StringArrayAlphabetIndexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {

    private LayoutInflater mInflater;
    private PinnedHeaderListView mListView;
    private ContactsAdapter mAdapter;
    private ArrayList<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(MainActivity.this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        KontakAPIInterface kontakAPIInterface =
                KontakFactory.getClient().create(KontakAPIInterface.class);

        //Get all contacts
        Call<List<Contact>> getContactsCall = kontakAPIInterface.getContactsList();
        getContactsCall.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                Log.v("Kontak", "getContactsCall response: " + response.body());
                contacts = new ArrayList<>(response.body().size());
                contacts.addAll(response.body());
                Log.v("Kontak", "I test response: " + contacts.get(0).getFirstName());
                //contacts = new ArrayList<Contact>(response.body());
                setContactsList();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.v("Kontak", "getContactsCall failure response: " + t.toString());
            }
        });










    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.action_newItem).setVisible(true);
//        super.onPrepareOptionsMenu(menu);
//    }

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

    void setContactsList()
    {
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
        mListView = (PinnedHeaderListView) findViewById(list);
        mAdapter = new ContactsAdapter(contacts);

        int pinnedHeaderBackgroundColor = getResources().getColor(R.color.pinned_header_text_bg);
        mAdapter.setPinnedHeaderBackgroundColor(pinnedHeaderBackgroundColor);
        mAdapter.setPinnedHeaderTextColor(getResources().getColor(R.color.pinned_header_text));
        mListView.setPinnedHeaderView(mInflater.inflate(R.layout.pinned_header_listview_side_header, mListView, false));
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", contacts.get(i).getId());
                startActivity(intent);
                Log.v("Kontak", "listview item click: " + i);
            }
        });



    }

























    private class ContactsAdapter extends SearchablePinnedHeaderListViewAdapter<Contact> {
        private ArrayList<Contact> mContacts;
        private final int CONTACT_PHOTO_IMAGE_SIZE;
        private final int[] PHOTO_TEXT_BACKGROUND_COLORS;
        //private final AsyncTaskThreadPool mAsyncTaskThreadPool = new AsyncTaskThreadPool(1, 2, 10);

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
                rootView = mInflater.inflate(R.layout.contact_listview_item, parent, false);
                holder.friendProfileCircularContactView = (CircularContactView) rootView
                        .findViewById(R.id.listview_item__friendPhotoImageView);
                holder.friendProfileCircularContactView.getTextView().setTextColor(0xFFffffff);
                holder.friendName = (TextView) rootView
                        .findViewById(R.id.listview_item__friendNameTextView);
                holder.headerView = (TextView) rootView.findViewById(R.id.header_text);
                rootView.setTag(holder);
            } else {
                rootView = convertView;
                holder = (ViewHolder) rootView.getTag();
            }
            final Contact contact = getItem(position);
            final String displayName = contact.getFirstName();
            holder.friendName.setText(displayName);
            //boolean hasPhoto = !TextUtils.isEmpty(contact.photoId);
//            if (holder.updateTask != null && !holder.updateTask.isCancelled())
//                holder.updateTask.cancel(true);
//            final Bitmap cachedBitmap = hasPhoto ? ImageCache.INSTANCE.getBitmapFromMemCache(contact.photoId) : null;
//            if (cachedBitmap != null)
//                holder.friendProfileCircularContactView.setImageBitmap(cachedBitmap);
//            else {
                final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                        % PHOTO_TEXT_BACKGROUND_COLORS.length];
                if (TextUtils.isEmpty(displayName))
                    holder.friendProfileCircularContactView.setImageResource(R.drawable.ic_person_24dp,
                            backgroundColorToUse);
                else {
                    final String characterToShow = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1).toUpperCase(Locale.getDefault());
                    holder.friendProfileCircularContactView.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
                }
//                if (hasPhoto) {
//                    holder.updateTask = new AsyncTaskEx<Void, Void, Bitmap>() {
//
//                        @Override
//                        public Bitmap doInBackground(final Void... params) {
//                            if (isCancelled())
//                                return null;
//                            final Bitmap b = ContactImageUtil.loadContactPhotoThumbnail(MainActivity.this, contact.photoId, CONTACT_PHOTO_IMAGE_SIZE);
//                            if (b != null)
//                                return ThumbnailUtils.extractThumbnail(b, CONTACT_PHOTO_IMAGE_SIZE,
//                                        CONTACT_PHOTO_IMAGE_SIZE);
//                            return null;
//                        }
//
//                        @Override
//                        public void onPostExecute(final Bitmap result) {
//                            super.onPostExecute(result);
//                            if (result == null)
//                                return;
//                            ImageCache.INSTANCE.addBitmapToCache(contact.photoId, result);
//                            holder.friendProfileCircularContactView.setImageBitmap(result);
//                        }
//                    };
//                    mAsyncTaskThreadPool.executeAsyncTask(holder.updateTask);
//                }
            //}
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
        public CircularContactView friendProfileCircularContactView;
        TextView friendName, headerView;
        //public AsyncTaskEx<Void, Void, Bitmap> updateTask;
    }
}
