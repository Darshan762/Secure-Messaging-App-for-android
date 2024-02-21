package com.encrypted.sms;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;




public class DecryptionComposeMain extends Fragment {
    private String message;
    private String key;
    private Button Switch;
    private Button Encrypt_Buuton;
    private Button Decrypt_Buuton;
    private PlayFair p;
    private TextView Answer;
    private EditText Textfield_Text;
    private EditText Textfield_Key;
    private TextView Matrix_value;
    private TextView Play_Fair_VALUE;
    Spinner from;
    EditText to,subject,compose;
    Button button;
    String date;
    ProgressDialog dialog;

    private static final String URL="http://wizzie.tech/EncryptedSMS/sendmail.php";
    ArrayAdapter adapter;
    ArrayList mails =new ArrayList();


    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.decryption_main, container, false);


       ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Switch = view.findViewById(R.id.Swtich);
        Encrypt_Buuton = view.findViewById(R.id.Encrypt_Buuton);
        Decrypt_Buuton = view.findViewById(R.id.Decrypt_Buuton);
        Answer = view.findViewById(R.id.Answer);
        Textfield_Text = view.findViewById(R.id.TextArea);
        Textfield_Key = view.findViewById(R.id.Key);
        Matrix_value = view.findViewById(R.id.Matrix);
        Play_Fair_VALUE = view.findViewById(R.id.Play_Fair_VALUE);
        from=view.findViewById(R.id.from);
        to = view.findViewById(R.id.to);
        subject = view.findViewById(R.id.subject);
        compose = view.findViewById(R.id.compose);
        button = view.findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (to.getText().toString().isEmpty()) {
                    Toast.makeText(view.getContext(), "To Address is empty", Toast.LENGTH_SHORT).show();
                }else if(subject.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(), "Subject is empty", Toast.LENGTH_SHORT).show();
                }else if(compose.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(), "Enter a message to Encrypt", Toast.LENGTH_SHORT).show();
                }
                else {
                    fun();
                }
            }
        });
        mails.add(MainActivity.email);
        adapter =new ArrayAdapter(view.getContext(),android.R.layout.simple_list_item_1,mails);
        from.setAdapter(adapter);


        return view;


    }

    private void fun() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
        date = df.format(Calendar.getInstance().getTime());

        dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("Loading, please wait.");
        dialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                // Toast.makeText(Compose.this, ""+response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jsonObject=new JSONObject(response);


                    if (jsonObject.getString("result").equals("success")){

                        Toast.makeText(view.getContext(), "Mail sent successfully", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(view.getContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String, String>();
                params.put("from",from.getSelectedItem().toString().trim());
                params.put("mobile",MainActivity.m.trim());
                params.put("to",to.getText().toString().trim());
                params.put("sub",subject.getText().toString().trim());
                params.put("mail_data",compose.getText().toString().trim());
                params.put("dt",date.toString().trim());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
    }


    public void encrypt(View view) throws Exception {


        if (Textfield_Text.length() == 0) {
            Toast.makeText(view.getContext(), "Enter a message to Encrypt", Toast.LENGTH_SHORT).show();
            return;
        }
        message = String.valueOf(Textfield_Text.getText());
        key = String.valueOf(Textfield_Key.getText());
        String Algorithm = String.valueOf(Switch.getText());
        switch (Algorithm) {
            case "Advanced Encryption Standard":
                AES aes = new AES();
                String enc = aes.AESencrypt(key.getBytes("UTF-16LE"), message.getBytes("UTF-16LE"));
                Answer.setText(enc);
                break;
            case "Triple Data Encryption Standard":
                DES des = new DES();
                String encData = des.encrypt(key.getBytes("UTF-16LE"), message.getBytes("UTF-16LE"));
                Answer.setText(encData);
                break;
            case "Caesar Cipher":
                if (key.isEmpty()) {
                    Toast.makeText(view.getContext(), "Enter a key to Encrypt", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key.length() > 26) {
                    Toast.makeText(view.getContext(), "The Key must be less than 26 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                Caesarcipher c = new Caesarcipher();
                Answer.setText(c.caesarcipherEnc(message, Integer.parseInt(key)));
                break;
            case "Vigenere Cipher":
                if (Textfield_Key.length() == 0) {
                    Toast.makeText(view.getContext(), "Enter a key to Encrypt", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (char i : message.toUpperCase().toCharArray()) {
                    if (i < 'A' || i > 'Z') {
                        Toast.makeText(view.getContext(), "Only Letters are allowed here", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                for (char i : key.toUpperCase().toCharArray()) {
                    if (i < 'A' || i > 'Z') {
                        Toast.makeText(view.getContext(), "Only Letters are allowed here", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                Vigenere v = new Vigenere();
                Answer.setText(v.Vigenereencrypt(message, key));
                break;
            case "Play Fair":
                try {
                    p = new PlayFair("");
                    Play_Fair_VALUE.setText(p.Encrypt(message, key));
                    Matrix_value.setText(p.getT1());
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "Only Letters are allowed here", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void decrypt(View view) throws Exception {
        if (Textfield_Text.length() == 0) {
            Toast.makeText(view.getContext(), "Enter a message to Decrypt", Toast.LENGTH_SHORT).show();
            return;
        }
        message = String.valueOf(Textfield_Text.getText());
        key = String.valueOf(Textfield_Key.getText());
        String SwitchValue = Switch.getText().toString();
        switch (SwitchValue) {
            case "Advanced Encryption Standard":
                AES aes = new AES();
                try {
                    String decData = aes.AESdecrypt(key, Base64.decode(message.getBytes("UTF-16LE"), Base64.DEFAULT));
                    Answer.setText(decData);
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "Your key is wrong", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Triple Data Encryption Standard":
                DES des = new DES();
                try {
                    String decData = des.decrypt(key, Base64.decode(message.getBytes("UTF-16LE"), Base64.DEFAULT));
                    Answer.setText(decData);
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "Your key is wrong", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Caesar Cipher":
                if (Textfield_Key.length() == 0) {
                    Toast.makeText(view.getContext(), "Enter a key", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(key) >= 26) {
                    Toast.makeText(view.getContext(), "The Key must be less than 26 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                Caesarcipher c = new Caesarcipher();
                Answer.setText(c.caesarcipherDec(message, Integer.parseInt(key)));
                break;
            case "Vigenere Cipher":
                if (Textfield_Key.length() == 0) {
                    Toast.makeText(view.getContext(), "Enter a key to Decrypt", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (char i : message.toUpperCase().toCharArray()) {
                    if (i < 'A' || i > 'Z') {
                        Toast.makeText(view.getContext(), "Only Letters are allowed here", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                for (char i : key.toUpperCase().toCharArray()) {
                    if (i < 'A' || i > 'Z') {
                        Toast.makeText(view.getContext(), "Only Letters are allowed here", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Vigenere v = new Vigenere();
                Answer.setText(v.Vigeneredecrypt(message, key));
                break;

            case "Play Fair":
                try {
                    Play_Fair_VALUE.setText(p.Decrypt(message, key));
                    Matrix_value.setText(p.getT1());
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "Only Letters are allowed here", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


    public void switchAlgho(View view) {
        reset(null);
        String SwitchValue = Switch.getText().toString();
        switch (SwitchValue) {
            case "Advanced Encryption Standard":
                Switch.setText("Triple Data Encryption Standard");
                break;
            case "Triple Data Encryption Standard":
                Textfield_Key.setInputType(InputType.TYPE_CLASS_NUMBER);
                Switch.setText("Caesar Cipher");
                break;
            case "Caesar Cipher":
                Textfield_Key.setInputType(InputType.TYPE_CLASS_TEXT);
                Switch.setText("Vigenere Cipher");
                break;
            case "Vigenere Cipher":
                Textfield_Key.setVisibility(View.VISIBLE);
                Answer.setVisibility(View.GONE);
                Matrix_value.setVisibility(View.VISIBLE);
                Play_Fair_VALUE.setVisibility(View.VISIBLE);
                Switch.setText("Play Fair");
                break;
            case "Play Fair":
                Answer.setVisibility(View.VISIBLE);
                Matrix_value.setVisibility(View.GONE);
                Play_Fair_VALUE.setVisibility(View.GONE);
                Switch.setText("Advanced Encryption Standard");
                break;
        }
    }


    public void reset(View view) {
        Textfield_Text.setText("");
        Textfield_Key.setText("");
        Answer.setText("");
        Play_Fair_VALUE.setText("");
        Matrix_value.setText("");
        if(view!=null)
            Toast.makeText(view.getContext(), "All data has been deleted", Toast.LENGTH_SHORT).show();
    }

    public void copyToClipboard(View view) {
        if (Play_Fair_VALUE.length() == 0) {
            String copyText = String.valueOf(Answer.getText());
            if (Answer.length() == 0) {
                Toast.makeText(view.getContext(), "There is no message to copy", Toast.LENGTH_SHORT).show();
                return;
            }

            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                        view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copyText);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("Your message :", copyText);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(view.getContext(),
                    "Your message has be copied", Toast.LENGTH_SHORT).show();
        } else {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                        view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(Play_Fair_VALUE.getText().toString());
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("Your message :", Play_Fair_VALUE.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(view.getContext(), "Your message has be copied", Toast.LENGTH_SHORT).show();
        }


    }


}

