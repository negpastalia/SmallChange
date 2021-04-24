package id.ac.umn.leleair.kelompok.smallchange;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import id.ac.umn.leleair.kelompok.smallchange.Model.Data;

public class Outcome extends Fragment {
    private Spinner filter;
    private ConstraintLayout PageTitle;
    private ImageView backgroundBox;
    private FloatingActionButton fabAddOutcome;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mOutcomeDatabase;

    //RecyclerView
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outcome, container, false);

        //Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mOutcomeDatabase = FirebaseDatabase.getInstance().getReference().child("OutcomeData").child(uid);

        filter = view.findViewById(R.id.filterOutcome);
        PageTitle = view.findViewById(R.id.PageTitleOutcome);
        backgroundBox = view.findViewById(R.id.backgroundBoxOutcome);
        fabAddOutcome = view.findViewById((R.id.fabAddOutcome));
        recyclerView = view.findViewById((R.id.recyclerViewOutcome));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        initializeFilter();

        fabAddOutcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertOutcomeData();
            }
        });

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.income_item,
                        MyViewHolder.class,
                        mOutcomeDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int position) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());
            }
        };

        //Set Recycler view adapter
        recyclerView.setAdapter(adapter);
    }

    public void insertOutcomeData(){
        //New Transaction Form
        Dialog mdialog = new Dialog(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myviewm = inflater.inflate(R.layout.insert_data_form, null);
        mdialog.setContentView(myviewm);
        mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText editAmount = myviewm.findViewById(R.id.editTransactionAmount);
        EditText editType = myviewm.findViewById(R.id.editTransactionName);
        EditText editNote = myviewm.findViewById(R.id.editTransactionNote);

        Button btnSave = myviewm.findViewById(R.id.btnSaveTransaction);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if(TextUtils.isEmpty(amount)){
                    editAmount.setError("Required Field");
                    return;
                }
                int ouramountint = Integer.parseInt((amount));
                if(TextUtils.isEmpty(type)){
                    editType.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    editNote.setError("Required Field");
                    return;
                }

                // Save transaction to database
                String id = mOutcomeDatabase.push().getKey();
                if(id != null){
                    String mDate = DateFormat.getDateInstance().format(new Date());
                    Data data = new Data(ouramountint, type, note, id, mDate);
                    mOutcomeDatabase.child(id).setValue(data);
                    Toast.makeText(getActivity(), "Transaction added successfully", Toast.LENGTH_SHORT).show();

                    mdialog.dismiss();
                }
            }
        });
        mdialog.show();
    }

    public void playAnimIn(){
        backgroundBox.animate().translationY(0).alpha(1).setDuration(600);
        PageTitle.animate().translationY(0).alpha(1).setDuration(400);
        filter.animate().alpha(1).setDuration(400).setStartDelay(600);
        fabAddOutcome.animate().translationY(0).alpha(1).setDuration(400);
        recyclerView.animate().translationY(0).alpha(1).setDuration(400).setStartDelay(600);
    }

    public void playAnimOut(){
        backgroundBox.animate().translationY(300).alpha(0).setDuration(200);
        PageTitle.animate().translationY(-130).alpha(0).setDuration(200);
        filter.animate().alpha(0).setDuration(200);
        fabAddOutcome.animate().translationY(100).alpha(0).setDuration(200);
        recyclerView.animate().translationY(100).alpha(0).setDuration(200);
    }

    private void initializeFilter() {
        String[] value = {"Show All", "Today", "7 days ago", "31 days ago"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(value));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.customview_spinner,arrayList){

            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(R.id.tvFilter);
                text.setTextColor(getResources().getColor(R.color.biru));

                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(R.id.tvFilter);
                text.setTextColor(getResources().getColor(R.color.white));

                return view;

            }
        };
        filter.setAdapter(arrayAdapter);
    }
}