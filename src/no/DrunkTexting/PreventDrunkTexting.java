package no.DrunkTexting;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;


/**
 * Created by Ch on 1/3/14.
 */
public class PreventDrunkTexting extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static String[] SMS_PROJECTION = new String[]{
            "_id",
            "address",
            "body",
            "read"
    };
    private CursorAdaptor adaptor;

    private Cursor model;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        adaptor = new CursorAdaptor(this,R.layout.list_item,
                null,new String[] {"read","address","body"},
                new int[] {R.id.read_status,R.id.person,
                        R.id.snipit});
        setListAdapter(adaptor);

        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id)
    {
        //Intent i = new Intent(Conversations.this, DetailForm.class);
        //i.putExtra(DetailForm.DETAIL_EXTRA, String.valueOf(id));
        //startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, ConversationProvider.CONVERSATIONS_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaptor.swapCursor(null);
    }


    /**
     * This static class is used to populate the ConversationAdapter rows
     * @author Chris
     *
     */
    static class CursorAdaptor extends SimpleCursorAdapter
    {
        private Context context, appContext;
        private int layout;
        private Cursor list;
        private final LayoutInflater inflate;

        public CursorAdaptor(Context context,int layout, Cursor c,
                             String[] from, int[] to){
            super(context,layout,c,from,to,
                    SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            this.context = context;
            this.layout = layout;
            inflate = LayoutInflater.from(context);
            list = c;
        }

        @Override
        public View newView(Context context, Cursor cursor,ViewGroup parent){
            super.newView(context,cursor,parent);
            return inflate.inflate(layout,null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor){
            super.bindView(view,context,cursor);

            ImageView read_not = (ImageView)view.findViewById(R.id.read_status);
            TextView contact = (TextView)view.findViewById(R.id.person);
            TextView snipit = (TextView)view.findViewById(R.id.snipit);

            read_not.setImageResource(cursor.getInt(cursor.getColumnIndex("read")) == 0 ?
                    R.drawable.blue_dot : R.drawable.empty_image);

            contact.setText(cursor.getString(cursor.getColumnIndex("address")));
            snipit.setText(cursor.getString(cursor.getColumnIndex("body")).substring(0,20)+" ...");

        }
    }
}