package com.icechen1.crowdreport;

import android.content.Intent;
import android.os.Bundle;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;


public class MainActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {
        this.disableLearningPattern();
        addMultiPaneSupport();
        // add accounts
        if(CrowdReportApplication.getInstance().mClient.getCurrentUser() != null){
            String s = CrowdReportApplication.getInstance().mClient.getCurrentUser().getUserId();
            MaterialAccount account = new MaterialAccount(this.getResources(),s,
                    "me@gmail.com", R.drawable.ic_action_image_photo_camera, R.drawable.bkg);
            this.addAccount(account);
        }

        // create sections
        this.addSection(newSection(getResources().getString(R.string.title_activity_maps), R.drawable.ic_action_maps_map, MapFragment.newInstance()));
        this.addSection(newSection(getResources().getString(R.string.action_submit),  R.drawable.ic_action_action_backup, new Intent(this, SubmitActivity.class)));
        this.addSubheader("Listings");
        this.addSection(newSection(getResources().getString(R.string.title_listing),  R.drawable.ic_action_action_list, IssueListingFragment.newInstance(false,null)));
        this.addSection(newSection(getResources().getString(R.string.title_my_listing),  R.drawable.ic_action_action_list, IssueListingFragment.newInstance(true,null)));
        //this.addSection(newSection("Section 2",new FragmentIndex()));
        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
    }

    @Override
    public void onResume(){
        super.onResume();
        this.closeDrawer();
        allowArrowAnimation();
    }
}
