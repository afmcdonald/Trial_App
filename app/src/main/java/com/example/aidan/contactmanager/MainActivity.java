package com.example.aidan.contactmanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.example.aidan.contactmanager.MESSAGE";

    private static final int EDIT = 0, DELETE = 1;

    EditText title, price, keywords, description;
    ImageView itemImage;
    List<Contact> Contacts = new ArrayList<Contact>();
    ListView contactListView;
    Uri imageUri = Uri.parse("android.resource://com.example.aidan.contactmanager/drawable/add_icon.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Contact> contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (EditText) findViewById(R.id.itemTitle); //title of selling post
        price = (EditText) findViewById(R.id.itemPrice);
        keywords = (EditText) findViewById(R.id.itemKeywords);
        description = (EditText) findViewById(R.id.itemDescription);
        contactListView = (ListView) findViewById(R.id.listView);
        itemImage = (ImageView) findViewById(R.id.addImage);
        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(contactListView);

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.sellItem);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //displays pop up with contact created
                Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(title.getText()), String.valueOf(price.getText()),String.valueOf(keywords.getText()), String.valueOf(description.getText()), imageUri );
                if(!contactExists(contact)) {
                    dbHandler.createContact(contact);
                    Contacts.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(title.getText()) +" has been added to your Contacts!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(title.getText()) + " already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addBtn.setEnabled(!String.valueOf(title.getText()).trim().isEmpty());//enabled if txt != nothing
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        itemImage.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");//like sending email or something or pick images
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
            }
        });

        if (dbHandler.getContactsCount()!= 0)
            Contacts.addAll(dbHandler.getAllContacts());


        populateList();
    }

    public void openItem(View view){
        Intent intent = new Intent(this,DisplayItem.class);
        startActivity(intent);
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Contact Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Contact");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Contact");
    }

    public boolean onContextItemSelected(MenuItem item){
        switch(item.getItemId()){
            case EDIT:
                //TODO: Implement editing a contact
                break;
            case DELETE:
                dbHandler.deleteContact(Contacts.get(longClickedItemIndex));
                Contacts.remove(longClickedItemIndex);
                contactAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean contactExists(Contact contact){
        String name = contact.getTitle();
        int contactCount = Contacts.size();

        for (int i = 0; i < contactCount; i++){
            if(name.compareToIgnoreCase(Contacts.get(i).getTitle())==0){//0 if match
                return true;
            }
        }
        return false;
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if (resCode== RESULT_OK){
            if (reqCode == 1){
                imageUri = (Uri) data.getData();
                itemImage.setImageURI(data.getData());
            }

        }
    }

    private void populateList() {
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);//what is this adapter?
    }


    private class ContactListAdapter extends ArrayAdapter<Contact>{ //Here's the list
        public ContactListAdapter(){
            super(MainActivity.this, R.layout.listview_item, Contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            if(view == null)//what does this inflater do?
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.buyTitle);
            name.setText(currentContact.getTitle());
            TextView phone = (TextView) view.findViewById(R.id.buyPrice);
            phone.setText(currentContact.getPrice());
            TextView email = (TextView) view.findViewById(R.id.buyKeywords);
            email.setText(currentContact.getKeywords());
            TextView address = (TextView) view.findViewById(R.id.buyDescription);
            address.setText(currentContact.getDescription());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(currentContact.getImageURI());

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
