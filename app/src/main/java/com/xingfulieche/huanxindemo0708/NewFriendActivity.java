package com.xingfulieche.huanxindemo0708;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.xingfulieche.huanxindemo0708.adapter.NewFriendsMsgAdapter;
import com.xingfulieche.huanxindemo0708.db.InviteMessage;
import com.xingfulieche.huanxindemo0708.db.InviteMessgeDao;

import java.util.List;

public class NewFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private Button but_add_btn;
    private EditText et_username;
    private ProgressDialog progressDialog;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);
        initView();
        getMessage();
    }

    private void getMessage() {
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        list.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);
    }

    private void initView() {
        but_add_btn = (Button) findViewById(R.id.but_add_btn);
        et_username = (EditText) findViewById(R.id.et_username);
        list = (ListView) findViewById(R.id.list);
        but_add_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.but_add_btn:
                String username = et_username.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "请输入内容...", Toast.LENGTH_SHORT).show();
                    return;
                }
                addContact(username);
                break;
            default:
                break;
        }
    }
    public void addContact(final String username) {
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    // demo写死了个reason，实际应该让用户手动填入
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(username, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
