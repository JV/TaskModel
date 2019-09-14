package com.example.taskmodel.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.MainActivity;
import com.example.taskmodel.R;
import com.example.taskmodel.adapters.ElementModelViewAdapter;
import com.example.taskmodel.backgroundTasks.UiHandlerThread;
import com.example.taskmodel.element.ElementModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddElementFragment extends Fragment {

    private List<ElementModel> elementModels;
    private SharedPreferences sharedPreferences;
    private Context mContext;
    private Handler handler = new Handler();
    private UiHandlerThread uiHandlerThread;
    private ElementModelViewAdapter mmAdapter;
    private RecyclerView recyclerViewMain;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private float screenHeight;
    protected MainActivity mainActivity;
    private FragmentManager fragmentManager;

    public AddElementFragment(UiHandlerThread handlerThread, ElementModelViewAdapter mmAdapter,
                              RecyclerView recyclerViewMain, Float screenHeight, MainActivity mainActivity) {
        this.uiHandlerThread = handlerThread;
        this.mmAdapter = mmAdapter;
        this.recyclerViewMain = recyclerViewMain;
        this.screenHeight = screenHeight;
        this.mainActivity = mainActivity;
        fragmentManager = this.mainActivity.getSupportFragmentManager();
    }

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

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                Gson gson = new Gson();
                String json5 = sharedPreferences.getString("MyObjectsList", "");
                Type type5 = new TypeToken<List<ElementModel>>() {
                }.getType();
                elementModels = gson.fromJson(json5, type5);

                ElementModel elementModel = new ElementModel();
                elementModel.setId(elementModels.get(elementModels.size() - 1).getId() + 1);
                Log.d("ADDCLICKED", "onClick: " + elementModels.get(elementModels.size() - 1).getId());
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

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String json1 = gson.toJson(elementModels);
                editor.putString("MyObjectsList", json1);
                editor.apply();

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putSerializable("valuesList", (Serializable) elementModels);
                message.setData(bundle);
                uiHandlerThread.getHandler().sendMessage(message);

                String json21 = sharedPreferences.getString("CoordinatesList", "");
                Type type = new TypeToken<List<List<Integer>>>() {
                }.getType();
                coordinates = gson.fromJson(json21, type);

                mmAdapter = new ElementModelViewAdapter(elementModels, mContext,
                        mainActivity, coordinates, sharedPreferences, screenHeight, uiHandlerThread);
                recyclerViewMain.setAdapter(mmAdapter);
                mmAdapter.notifyDataSetChanged();

                fragmentManager.beginTransaction().remove(AddElementFragment.this)
                        .commitAllowingStateLoss();
                FloatingActionButton fab = ((MainActivity) getActivity())
                        .findViewById(R.id.floating_action_button);
                fab.show();
                fragmentManager.popBackStackImmediate();
            }
        });
        btnCancelAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().remove(AddElementFragment.this)
                        .commitAllowingStateLoss();
                FloatingActionButton fab = ((MainActivity) getActivity())
                        .findViewById(R.id.floating_action_button);
                fab.show();
                fragmentManager.popBackStackImmediate();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = ((MainActivity) getActivity())
                .findViewById(R.id.floating_action_button);
        fab.show();
    }
}
