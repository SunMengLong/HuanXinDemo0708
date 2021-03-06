package com.xingfulieche.huanxindemo0708;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.xingfulieche.huanxindemo0708.app.MyApplication;
import com.xingfulieche.huanxindemo0708.db.EaseCommonUtils;
import com.xingfulieche.huanxindemo0708.db.EaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GoodFriendsList extends AppCompatActivity {
    protected List<EaseUser> contactList = new ArrayList<EaseUser>();
    protected ListView listView;
    private Map<String, EaseUser> contactsMap;
    private ContactAdapter adapter;

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_friends_list);
        this.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(GoodFriendsList.this, NewFriendActivity.class));
            }

        });
        listView = (ListView) this.findViewById(R.id.listView);
        getContactList();
        adapter = new ContactAdapter(this, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                startActivity(new Intent(GoodFriendsList.this,ChatMessageActivity.class).putExtra("username", adapter.getItem(arg2).getUsername()));
                finish();
//                Toast.makeText(GoodFriendsList.this, "跳转到好友界面", Toast.LENGTH_SHORT).show();
//                Intent intent=new Intent(GoodFriendsList.this,ChatMessageActivity.class);
//                startActivity(intent);
            }

        });

    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    protected void getContactList() {
        contactList.clear();
        // 获取联系人列表
        contactsMap = MyApplication.getInstance().getContactList();
        if (contactsMap == null) {
            return;
        }
        synchronized (this.contactsMap) {
            Iterator<Map.Entry<String, EaseUser>> iterator = contactsMap.entrySet().iterator();
            List<String> blackList = EMClient.getInstance().contactManager().getBlackListUsernames();
            while (iterator.hasNext()) {
                Map.Entry<String, EaseUser> entry = iterator.next();
                // 兼容以前的通讯录里的已有的数据显示，加上此判断，如果是新集成的可以去掉此判断
                if (!entry.getKey().equals("item_new_friends") && !entry.getKey().equals("item_groups")
                        && !entry.getKey().equals("item_chatroom") && !entry.getKey().equals("item_robots")) {
                    if (!blackList.contains(entry.getKey())) {
                        // 不显示黑名单中的用户
                        EaseUser user = entry.getValue();
                        EaseCommonUtils.setUserInitialLetter(user);
                        contactList.add(user);
                    }
                }
            }
        }

        // 排序
        Collections.sort(contactList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });

    }

    class ContactAdapter extends BaseAdapter {
        private Context context;
        private List<EaseUser> users;
        private LayoutInflater inflater;

        public ContactAdapter(Context context_, List<EaseUser> users) {

            this.context = context_;
            this.users = users;
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return users.size();
        }

        @Override
        public EaseUser getItem(int position) {
            // TODO Auto-generated method stub
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_contact, parent, false);

            }

            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_name.setText(getItem(position).getUsername());

            return convertView;
        }

    }
}
