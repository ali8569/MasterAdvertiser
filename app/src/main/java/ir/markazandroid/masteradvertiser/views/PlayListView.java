package ir.markazandroid.masteradvertiser.views;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import org.json.JSONObject;

import java.io.Serializable;

import ir.markazandroid.masteradvertiser.MasterAdvertiserApplication;
import ir.markazandroid.masteradvertiser.network.JSONParser.Parser;
import ir.markazandroid.masteradvertiser.network.JSONParser.annotations.JSON;
import ir.markazandroid.masteradvertiser.util.ContentAdapter;
import ir.markazandroid.masteradvertiser.views.Widget;
import ir.markazandroid.masteradvertiser.views.playlist.data.Data;

/**
 * Coded by Ali on 6/22/2019.
 */
public class PlayListView extends FrameLayout implements Widget {

    private final Parser parser;
    private Extras extras;
    private ContentAdapter adapter;
    private FragmentManager fragmentManager;


    public PlayListView(Context context) {
        super(context);
        parser=((MasterAdvertiserApplication)context.getApplicationContext()).getParser();
    }

    @Override
    public void init(JSONObject extrasJSON) {
        extras=parser.get(Extras.class,extrasJSON);
        adapter=new ContentAdapter(getContext(),fragmentManager,extras.getData().getEntities(),this);
        adapter.gotoNext();
    }

    @Override
    public void dispose() {
        if (adapter!=null)
            adapter.dispose();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static class Extras implements Serializable{
        private Data data;

        @JSON(classType = JSON.CLASS_TYPE_OBJECT,clazz = Data.class)
        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

    }

}
