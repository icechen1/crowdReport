package com.icechen1.crowdreport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.icechen1.crowdreport.data.Issue;
import com.icechen1.crowdreport.dummy.DummyContent;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class IssueListingFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "IS_ONLY_USER";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private boolean mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static IssueListingFragment newInstance(boolean only_user, String param2) {
        IssueListingFragment fragment = new IssueListingFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, only_user);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IssueListingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getBoolean(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(mParam1){
            ArrayList<Issue> filtered = new ArrayList<>();
            for(Issue i: CrowdReportApplication.mList){
                Log.d("a", String.valueOf(filtered.size()));
                if(i.getUserId() != null)
                    if(i.getUserId().equals(CrowdReportApplication.getInstance().mClient.getCurrentUser().getUserId())) filtered.add(i);
            }
            Log.d("a", String.valueOf(filtered.size()));
            mAdapter = new IssueAdapter(getActivity(), filtered);
        }else{
            mAdapter = new IssueAdapter(getActivity(), CrowdReportApplication.mList);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issue, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent detailAct = new Intent(getActivity(),DetailActivity.class);

            //detailAct.putExtra("time", ((Issue)mAdapter.getItem(position)).getTime());
            detailAct.putExtra("lat", ((Issue)mAdapter.getItem(position)).getLat());
            detailAct.putExtra("lon", ((Issue)mAdapter.getItem(position)).getLon());
            detailAct.putExtra("description",((Issue)mAdapter.getItem(position)).getDescription());
            detailAct.putExtra("picture",((Issue)mAdapter.getItem(position)).getPicture());
            detailAct.putExtra("category",((Issue)mAdapter.getItem(position)).getCategory());
            detailAct.putExtra("status",((Issue)mAdapter.getItem(position)).getStatus());

            getActivity().startActivity(detailAct);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
    public class IssueAdapter extends ArrayAdapter<Issue> {
        public IssueAdapter(Context context, ArrayList<Issue> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Issue issue = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_issue, parent, false);
            }
            // Lookup view for data population
            TextView tvCat = (TextView) convertView.findViewById(R.id.category);
            TextView tvLoc = (TextView) convertView.findViewById(R.id.location);
            TextView tvStatus = (TextView) convertView.findViewById(R.id.status);
            // Populate the data into the template view using the data object
            tvCat.setText(issue.getCategory());
            tvLoc.setText(issue.getLat() + " " + issue.getLon());
            int submit_status = issue.getStatus();
            if(submit_status == 0){
                tvStatus.setText("Pending");
                tvStatus.setTextColor(Color.DKGRAY);
            }

            if(submit_status == 1){
                tvStatus.setText("Rejected");
                tvStatus.setTextColor(Color.RED);
            }
            if(submit_status == 2){
                tvStatus.setText("Acknowledged");
                tvStatus.setTextColor(Color.parseColor("#8BC34A"));
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
