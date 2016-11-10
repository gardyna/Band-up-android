package com.melodies.bandup.SoundCloudFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.repositories.BandUpRepository;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoundCloudLoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoundCloudLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundCloudLoginFragment extends Fragment implements View.OnClickListener {
    private ImageButton mLoginButton;

    private OnFragmentInteractionListener mListener;

    public SoundCloudLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SoundCloudLoginFragment.
     */
    public static SoundCloudLoginFragment newInstance() {
        SoundCloudLoginFragment fragment = new SoundCloudLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sound_cloud_login, container, false);
        getViews(rootView);
        return rootView;
    }

    private void getViews(View rootView){
        mLoginButton = (ImageButton) rootView.findViewById(R.id.soundcloud_login_btn);
        mLoginButton.setOnClickListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Dialog myDialog = createLoginDialog();
        myDialog.show();
    }

    private Dialog createLoginDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View mDialog = inflater.inflate(R.layout.dialog_soundcloud_signin,  null);

        builder.setView(mDialog)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // sign in user
                        EditText email = (EditText) mDialog.findViewById(R.id.dialog_email);
                        EditText pass  = (EditText) mDialog.findViewById(R.id.dialog_password);
                        logIn(email.getText().toString(), pass.getText().toString());

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SoundCloudLoginFragment.this.cancel();
            }
        });

        return builder.create();
    }

    private void cancel() {
    }

    private void logIn(final String email, final String pass){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiWrapper soundcloud = new ApiWrapper(getResources().getString(R.string.soundCloudClient),
                        getResources().getString(R.string.soundCloudSecret),
                        null, null);
                try {
                    Token token = soundcloud.login(email, pass, Token.SCOPE_NON_EXPIRING);
                    HttpResponse resp = soundcloud.get(Request.to("/me"));
                    JSONObject json = Http.getJSON(resp);
                    JSONObject requestJSON = new JSONObject();
                    requestJSON.put("soundCloudId", json.getInt("id"));
                    DatabaseSingleton.getInstance(getContext()).getBandUpDatabase().sendSoundCloudId(requestJSON,
                            new BandUpResponseListener() {
                                @Override
                                public void onBandUpResponse(Object response) {
                                    finish();
                                }
                            }, new BandUpErrorListener() {
                                @Override
                                public void onBandUpErrorResponse(VolleyError error) {
                                    //Toast.makeText(getContext(), "server error", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void finish() {
        // reload activity
        Activity curr = getActivity();
        curr.finish();
        curr.startActivity(curr.getIntent());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
