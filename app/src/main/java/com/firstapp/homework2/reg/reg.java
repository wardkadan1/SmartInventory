package com.firstapp.homework2.reg;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.firstapp.homework2.MainActivity;
import com.firstapp.homework2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reg extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText emailEditText, passEditText, rePassEditText, phoneEditText;
    private TextInputLayout emailLayout, passLayout, rePassLayout, phoneLayout;

    public reg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reg.
     */
    // TODO: Rename and change types and number of parameters
    public static reg newInstance(String param1, String param2) {
        reg fragment = new reg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reg, container, false);

        emailEditText = view.findViewById(R.id.Email);
        passEditText = view.findViewById(R.id.Pass);
        rePassEditText = view.findViewById(R.id.rePass);
        phoneEditText = view.findViewById(R.id.Phone);

        emailLayout = view.findViewById(R.id.textInputLayoutEmail);
        passLayout = view.findViewById(R.id.textInputLayoutPass);
        rePassLayout = view.findViewById(R.id.textInputLayoutRePass);
        phoneLayout = view.findViewById(R.id.textInputLayoutPhone);

        Button button1 = view.findViewById(R.id.button3tofrag1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateInputs()) {
                    MainActivity mainActivity = (MainActivity) getActivity();

                    mainActivity.rege(new MainActivity.RegCallback() {
                        @Override
                        public void onRegSuccess() {
                            Log.d("DebugCheck", "Reg successful! Navigating to next fragment.");
                            Navigation.findNavController(view).navigate(R.id.action_reg_to_login);
                        }

                        @Override
                        public void onRegFailure() {
                            Log.d("DebugCheck", "Reg failed. Staying on the login screen.");
                        }
                    });
                }
            }

        });
        return view;
    }

    private boolean validateInputs() {
        String email = emailEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();
        String rePassword = rePassEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        boolean isValid = true;

        // Reset Errors
        emailLayout.setError(null);
        passLayout.setError(null);
        rePassLayout.setError(null);
        phoneLayout.setError(null);

        // Check if fields are empty
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passLayout.setError("Password is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(rePassword)) {
            rePassLayout.setError("Please confirm your password");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Phone number is required");
            isValid = false;
        } else if (!phone.matches("\\d{10}")) { // Ensures exactly 10 digits
            phoneLayout.setError("Phone number must be exactly 10 digits");
            isValid = false;
        }

        // Check if passwords match
        if (!password.equals(rePassword)) {
            rePassLayout.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }
}