package com.example.taskmodel.fragments;

import android.os.Build;
import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodel.MainActivity;
import com.example.taskmodel.R;
import com.example.taskmodel.element.ElementModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class EditElementFragment extends Fragment {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        final List<ElementModel> elementModels = (List<ElementModel>) bundle.getSerializable("valuesList");
        final long itemPosition = bundle.getLong("editItemPosition");

        View view = inflater.inflate(R.layout.edit_element_fragment, container, false);
        final EditText etNaziv = view.findViewById(R.id.etEditNaziv);
        final EditText etPocetak = view.findViewById(R.id.etEditPocetak);
        final EditText etTag = view.findViewById(R.id.etEditTag);
        final TimePicker timePicker = view.findViewById(R.id.editTimepicker);
        Button btnSaveEdit = view.findViewById(R.id.btnSaveEditElement);
        Button btnCancelEdit = view.findViewById(R.id.btnCancelEditElement);

        etNaziv.setText(elementModels.get((int) itemPosition).getNaziv());
        etPocetak.setText(elementModels.get((int) itemPosition).getPocetak().toString());
        etTag.setText(elementModels.get((int) itemPosition).getTag());
        timePicker.setHour(Integer.parseInt(elementModels.get((int) itemPosition).getKraj().toString()));

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                elementModels.get((int) itemPosition).setNaziv(etNaziv.getText().toString());
                elementModels.get((int) itemPosition).setPocetak(Long.valueOf(etPocetak.getText().toString()));
                elementModels.get((int) itemPosition).setKraj((long) timePicker.getHour());
                elementModels.get((int) itemPosition).setTag(etTag.getText().toString());

                RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerviewMain);
                ((MainActivity) getActivity()).prepareElementData();
                recyclerView.getAdapter().notifyDataSetChanged();

                getFragmentManager().beginTransaction().remove(EditElementFragment.this).commit();
                FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.floating_action_button);
                fab.show();
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