package com.herasoft.due.Fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.herasoft.due.Activity.MainActivity;
import com.herasoft.due.R;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab1Fragment.Tab1FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab1Fragment extends Fragment {

    public static int speaking=1;
    TextView textdias, texthoras, textminutos, textsegundos, data, noivos;
    VideoView videoView;
    com.getbase.floatingactionbutton.FloatingActionButton som, album, casal;
    FloatingActionsMenu menu;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Codes").child(MainFragment.codigo).child("video");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Tab1FragmentInteractionListener mListener;

    public Tab1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab1Fragment newInstance(String param1, String param2) {
        Tab1Fragment fragment = new Tab1Fragment();
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

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        textdias = (TextView) view.findViewById(R.id.dias);
        texthoras = (TextView) view.findViewById(R.id.horas);
        textminutos = (TextView) view.findViewById(R.id.minutos);
        textsegundos = (TextView) view.findViewById(R.id.segundos);

        data = (TextView) view.findViewById(R.id.textView7);
        noivos = (TextView) view.findViewById(R.id.textView8);

        videoView = (VideoView) view.findViewById(R.id.videoView);

        menu = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        som = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.som);
        album = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.album);
        casal = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.casal);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                data.setText(dataSnapshot.child("datastring").getValue(String.class));
                noivos.setText(dataSnapshot.child("casal").getValue(String.class));

                String date = dataSnapshot.child("data").getValue().toString();

                SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                java.util.Date d = null;
                java.util.Date d1 = null;
                Calendar calendar = Calendar.getInstance();
                try {
                    d = dfDate.parse(date+" 19:00:00");
                    d1 = dfDate.parse(dfDate.format(calendar.getTime()));
                } catch (java.text.ParseException e){
                    e.printStackTrace();
                }

                long milisegundos = (d.getTime() - d1.getTime());

                if(milisegundos>0){

                new CountDownTimer(milisegundos,1000){

                    double correcao = 1000*60*60*24;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int dias = (int) (millisUntilFinished / correcao);
                        textdias.setText(String.valueOf(dias));

                        int horas = (int) (((millisUntilFinished / correcao) - dias) * 24);
                        texthoras.setText(String.valueOf(horas));

                        int minutos = (int) (((((millisUntilFinished / correcao) - dias) * 24) - horas) * 60);
                        textminutos.setText(String.valueOf(minutos));

                        int segundos = (int) (((((((millisUntilFinished / correcao) - dias) * 24) - horas) * 60) - minutos) *60);
                        textsegundos.setText(String.valueOf(segundos));
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();

                }else{

                    LinearLayout cont = (LinearLayout) view.findViewById(R.id.contagem);
                    cont.setVisibility(View.INVISIBLE);

                }

                String uripath = dataSnapshot.child("urlVideo").getValue(String.class);
                Uri src = Uri.parse(uripath);
                videoView.setVideoURI(src);
                videoView.requestFocus();
                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setVolume(0,0);
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.start();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        MainActivity.mediaPlayer.setVolume(1.0f,1.0f);

        if(MainActivity.mediaPlayer.isPlaying()) {
            som.setIcon(R.drawable.pause);
        }
        else{
            som.setIcon(R.drawable.play);
        }

        som.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(speaking==1){

                    speaking = 0;
                    som.setIcon(R.drawable.play);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.pausemusic();

                }
                else{

                    speaking = 1;
                    som.setIcon(R.drawable.pause);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.startmusic();

                }

            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainactivity = (MainActivity) getActivity();
                mainactivity.loadPhotos();

            }
        });

        casal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainactivity = (MainActivity) getActivity();
                mainactivity.loadHistory();

            }
        });

            return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTab1FragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Tab1FragmentInteractionListener) {
            mListener = (Tab1FragmentInteractionListener) context;
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

    public interface Tab1FragmentInteractionListener {
        // TODO: Update argument type and name
        void onTab1FragmentInteraction(Uri uri);
    }

}
