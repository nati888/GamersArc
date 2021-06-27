package il.ac.hit.gamersarc;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import il.ac.hit.gamersarc.CreateEventVM;
import il.ac.hit.gamersarc.InviteFriendsDialog;
import il.ac.hit.gamersarc.UserInstance;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventFragment extends Fragment implements il.ac.hit.gamersarc.InviteFriendsDialog.PassInvitedFriendsIdsToParentListener {

    private il.ac.hit.gamersarc.CreateEventVM viewModel;
    private static Bundle bundle;
    private int eventYear;
    private int eventMonth;
    private int eventDayOfMonth;
    private int eventHourOfDay;
    private int eventMinute;
    private String runningLevel;
    private String eventStatus;
    private TextView locationEt;
    private TextView dateEt;
    private String eventDate;
    private String eventTime;
    private ArrayList<String> invitedFriendsIds = new ArrayList<>();
    private UserInstance userInstance = UserInstance.getInstance();


    interface OnMapListener{
        void onMapOkClick();
    }

    interface OnBackFromCreateEventListener{
        void toHomeFromCreateEvent();
    }

    private OnMapListener mapCallback;
    private OnBackFromCreateEventListener backFromCreateEventCallback;

    private static il.ac.hit.gamersarc.CreateEventFragment createEventFragment = null;


    public static il.ac.hit.gamersarc.CreateEventFragment getCreateEventFragment(boolean isNew){
        bundle = new Bundle();
        if(createEventFragment == null || isNew ){
            bundle.putBoolean("isNew", true);
            createEventFragment = new il.ac.hit.gamersarc.CreateEventFragment();
        }
        else
        {
            bundle.putBoolean("isNew", false);
        }

        createEventFragment.setArguments(bundle);
        return createEventFragment;
    };

    public CreateEventFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mapCallback = (il.ac.hit.gamersarc.CreateEventFragment.OnMapListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Activity must implement OnMapListener");
        }

        try {
            backFromCreateEventCallback = (il.ac.hit.gamersarc.CreateEventFragment.OnBackFromCreateEventListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Activity must implement OnBackFromCreateEventListener");
        }

        viewModel = new ViewModelProvider(getActivity()).get(il.ac.hit.gamersarc.CreateEventVM.class);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.create_event_fragment,container,false);

        dateEt=root.findViewById(R.id.eventDateEt);
        final TextView timeEt=root.findViewById(R.id.eventTimeEt);
        locationEt=root.findViewById(R.id.eventLocationEt);
        LinearLayout inviteFriendsBtn=root.findViewById(R.id.invite_friend_layout);
        final RadioGroup eventStatusRg = root.findViewById(R.id.event_status);
        RadioGroup eventRunningLevelRg = root.findViewById(R.id.levelGroupCreateEvent);


        Button doneBtn=root.findViewById(R.id.doneBtn);

        if(getArguments()!=null){
            if(getArguments().getBoolean("isNew")){
               clearPage();
            }
        }


        dateEt.setText(viewModel.getEventDate());
        timeEt.setText(viewModel.getEventTime());


        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                int year1=calendar.get(calendar.YEAR);
                final int month1=calendar.get(calendar.MONTH);
                int dayOfMonth1=calendar.get(calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        eventYear=year;
                        eventMonth=month+1;
                        eventDayOfMonth=dayOfMonth;

                        String eventMonth1;
                        String eventDayOfMonth1;

                        if(eventMonth < 10)
                            eventMonth1 = "0"+eventMonth;
                        else
                            eventMonth1=eventMonth+"";

                        if(eventDayOfMonth < 10)
                            eventDayOfMonth1 = "0"+eventDayOfMonth;
                        else
                            eventDayOfMonth1 = eventDayOfMonth+"";

                        eventDate=eventDayOfMonth1+"/"+eventMonth1+'/'+eventYear;
                        dateEt.setText(eventDate);
                        viewModel.setEventDate(eventDate);
                        viewModel.setEventYear(year);
                        viewModel.setEventMonth(month+1);
                        viewModel.setEventDayOfMonth(dayOfMonth);


                    }
                },year1,month1,dayOfMonth1);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();

            }
        });

        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar=Calendar.getInstance();
                int currentHourOfDay=calendar.get(calendar.HOUR_OF_DAY);
                int currentMinute=calendar.get(calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String chosenHour;
                        String chosenMinute;

                        if(hourOfDay < 10)
                            chosenHour="0"+hourOfDay;
                        else
                            chosenHour=hourOfDay+"";

                        if(minute < 10)
                            chosenMinute="0"+minute;
                        else
                            chosenMinute=minute+"";

                        eventHourOfDay=hourOfDay;
                        eventMinute=minute;

                        eventTime = chosenHour+":"+chosenMinute;
                        timeEt.setText(eventTime);
                        viewModel.setEventTime(eventTime);
                        viewModel.setEventHourOfDay(hourOfDay);
                        viewModel.setEventMinute(minute);
                    }
                }, currentHourOfDay,currentMinute,true);
                timePickerDialog.show();
            }
        });

        locationEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapCallback.onMapOkClick();

            }
        });

       eventRunningLevelRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               switch (checkedId){
                   case R.id.easyRBCreateEvent:{
                       runningLevel = getString(R.string.easy);
                       viewModel.setRunningLevel(runningLevel);
                       break;
                   }
                   case R.id.mediumRBCreateEvent:{
                       runningLevel = getString(R.string.medium);
                       viewModel.setRunningLevel(runningLevel);
                       break;
                   }
                   case R.id.expertRBCreateEvent:{
                       runningLevel = getString(R.string.expert);
                       viewModel.setRunningLevel(runningLevel);
                       break;
                   }
               }
           }
       });

        eventStatusRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.public_event:{
                        eventStatus = "publicEvent";
                        viewModel.setEventStatus(eventStatus);
                        break;
                    }
                    case R.id.private_event:{
                        eventStatus = "privateEvent";
                        viewModel.setEventStatus("privateEvent");
                        break;
                    }
                }
            }
        });


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEventData();
                if(dateEt.getText().equals("") || timeEt.getText().equals("") ||
                       locationEt.getText().equals("")|| runningLevel==null || eventStatus==null ){
                    Snackbar.make(getView(),"all_fields", Snackbar.LENGTH_LONG).show();

                }
                else{
                    viewModel.setEventData(eventYear,eventMonth,eventDayOfMonth,eventHourOfDay,
                            eventMinute,runningLevel,eventStatus,invitedFriendsIds);

                    Snackbar.make(getView(),"new_event_snack_bar", Snackbar.LENGTH_LONG).show();
                    backFromCreateEventCallback.toHomeFromCreateEvent();

                }

            }
        });

        viewModel.getStreetAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                locationEt.setText(s);

            }
        });

        inviteFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                il.ac.hit.gamersarc.InviteFriendsDialog dialog = il.ac.hit.gamersarc.InviteFriendsDialog.getInstance(invitedFriendsIds);
                dialog.setCancelable(true);
                dialog.setTargetFragment(il.ac.hit.gamersarc.CreateEventFragment.this,300);
                assert fm != null;
                dialog.show(fm,"inviteFriendsFragment");

            }
        });

        return root;
    }

    @Override
    public void onFinishEditDialog(ArrayList<String> invitedFriendsIds) {
        this.invitedFriendsIds = invitedFriendsIds;
        viewModel.setInvitedUsersIds(invitedFriendsIds);
    }

    public void clearPage(){
        viewModel.setEventDate("");
        viewModel.setEventTime("");
        viewModel.setStreetAddress("");
        viewModel.setEventStatus(null);
        viewModel.setRunningLevel(null);
        viewModel.setInvitedUsersIds(null);
        bundle.putBoolean("isNew", false);
    }

    public void updateEventData(){
        eventYear = viewModel.getEventData().getYear();
        eventMonth = viewModel.getEventData().getMonth();
        eventDayOfMonth = viewModel.getEventData().getDayOfMonth();
        eventHourOfDay = viewModel.getEventData().getHourOfDay();
        eventMinute = viewModel.getEventData().getMinute();
        runningLevel = viewModel.getEventData().getRunningLevel();
        eventStatus = viewModel.getEventData().getEventStatus();
        invitedFriendsIds = viewModel.getInvitedUsersIds();
    }



}


