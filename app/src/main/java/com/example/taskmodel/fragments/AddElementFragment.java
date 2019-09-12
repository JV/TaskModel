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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taskmodel.MainActivity;
import com.example.taskmodel.R;
import com.example.taskmodel.element.ElementModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.List;

public class AddElementFragment extends Fragment {

    private List<ElementModel> elementModels;
    private SharedPreferences sharedPreferences;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        elementModels = (List<ElementModel>) bundle.getSerializable("valuesList");
        mContext = getActivity().getApplicationContext();
        return inflater.inflate(R.layout.fragment_add_element, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText etNaziv = view.findViewById(R.id.etNaziv);
        final EditText etPocetak = view.findViewById(R.id.etPocetak);
        final EditText etTag = view.findViewById(R.id.etTag);
        final TimePicker timePicker = view.findViewById(R.id.timepicker);
        Button btnAddElement = view.findViewById(R.id.btnAddElement);
        Button btnCancelAddElement = view.findViewById(R.id.btnCancelAddElement);

        btnAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ElementModel elementModel = new ElementModel();
                elementModel.setId(elementModels.get(elementModels.size() - 1).getId() + 1);
                if (etNaziv.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter name", Toast.LENGTH_LONG).show();
                    etNaziv.requestFocus();
                    return;
                } else {
                    elementModel.setNaziv(etNaziv.getText().toString().trim());
                }

                if (etPocetak.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter beginning", Toast.LENGTH_LONG).show();
                    etPocetak.requestFocus();
                    return;
                } else {
                    elementModel.setPocetak(Long.valueOf((etPocetak.getText().toString().trim())));
                }

                if (etTag.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter tag", Toast.LENGTH_LONG).show();
                    etTag.requestFocus();
                    return;
                } else {
                    elementModel.setTag(etTag.getText().toString().trim());
                }

                elementModel.setId(elementModels.get(elementModels.size() - 1).getId() + 1);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    long hour, minute;
                    hour = (long) timePicker.getHour();
                    minute = (long) timePicker.getMinute();

                    long totalTime = hour * 60 + minute;

                    elementModel.setKraj(totalTime);
                }
                elementModels.add(0, elementModel);

                Gson gson = new Gson();
                String json = gson.toJson(elementModels);

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("MyObjectsList", json);
                editor.apply();

                MainActivity activity = (MainActivity) getActivity();
                activity.doWork();

                getFragmentManager().beginTransaction().remove(AddElementFragment.this).commitAllowingStateLoss();

                FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.floating_action_button);
                fab.show();

            }
        });
        btnCancelAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(AddElementFragment.this).commitAllowingStateLoss();
                FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.floating_action_button);
                fab.show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.floating_action_button);
        fab.show();
    }
}
