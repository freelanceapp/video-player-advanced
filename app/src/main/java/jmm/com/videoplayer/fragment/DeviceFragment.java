package jmm.com.videoplayer.fragment;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.ArrayList;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.adapter.ShowVideoAdapter;
import jmm.com.videoplayer.model.ShowVideo;

public class DeviceFragment extends Fragment {

    RecyclerView rv_showvideo;
    ShowVideoAdapter showVideoAdapter;
    ArrayList<ShowVideo> arrayList = new ArrayList<>();
    Spinner navigationSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_device, container, false);

    /*    rv_showvideo = view.findViewById(R.id.rv_showvideo);
        rv_showvideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        showVideoAdapter = new ShowVideoAdapter(arrayList, getActivity());
        rv_showvideo.setAdapter(showVideoAdapter);


*/
        return view ;
    }
}