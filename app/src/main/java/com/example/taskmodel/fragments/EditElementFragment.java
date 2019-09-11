package com.example.taskmodel.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.taskmodel.MainActivity;
import com.example.taskmodel.R;
import com.example.taskmodel.element.ElementModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.List;

public class EditElementFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private Context mContext;
    private List<ElementModel> elementModels;
    private long itemPosition;
    EditText etNaziv;
    EditText etPocetak;
    EditText etTag;
    TimePicker timePicker;
    Button btnSaveEdit;
    Button btnCancelEdit;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        Bundle bundle = this.getArguments();
        final List<ElementModel> elementModels = (List<ElementModel>) bundle.getSerializable("valuesList");
        final long itemPosition = bundle.getLong("editItemPosition");

        View view = inflater.inflate(R.layout.edit_element_fragment, container, false);
        etNaziv = view.findViewById(R.id.etEditNaziv);
        etPocetak = view.findViewById(R.id.etEditPocetak);
        etTag = view.findViewById(R.id.etEditTag);
        timePicker = view.findViewById(R.id.editTimepicker);
        btnSaveEdit = view.findViewById(R.id.btnSaveEditElement);
        btnCancelEdit = view.findViewById(R.id.btnCancelEditElement);

        etNaziv.setText(elementModels.get((int) itemPosition).getNaziv());
        etPocetak.setText(elementModels.get((int) itemPosition).getPocetak().toString());
        etTag.setText(elementModels.get((int) itemPosition).getTag());

        long totalTimeGet = elementModels.get((int) itemPosition).getKraj();
        long hourSet = totalTimeGet / 60;
        long minuteSet = totalTimeGet % 60;
        timePicker.setHour((int) hourSet);
        timePicker.setMinute((int) minuteSet);

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                elementModels.get((int) itemPosition).setNaziv(etNaziv.getText().toString().trim());
                elementModels.get((int) itemPosition).setPocetak(Long.parseLong(etPocetak.getText().toString().trim()));
                long hour = timePicker.getHour();
                long minute = timePicker.getMinute();
                long totalTime = hour * 60 + minute;
                elementModels.get((int) itemPosition).setKraj(totalTime);
                elementModels.get((int) itemPosition).setTag(etTag.getText().toString().trim());

                Gson gson = new Gson();
                String json = gson.toJson(elementModels);

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("MyObjectsList", json);
                editor.apply();

                MainActivity activity = (MainActivity) getActivity();
                activity.doWork();

                FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.floating_action_button);
                fab.show();

                getFragmentManager().beginTransaction().remove(EditElementFragment.this).commit();

            }
        });

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().beginTransaction().remove(EditElementFragment.this).commit();
                FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.floating_action_button);
                fab.show();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.floating_action_button);
        fab.show();
    }
}