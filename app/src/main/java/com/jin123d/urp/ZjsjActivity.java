package com.jin123d.urp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;
import android.widget.Toast;

import com.jin123d.adapter.ZjsjAdapter;
import com.jin123d.app.BaseActivity;
import com.jin123d.models.ZjsjModels;
import com.jin123d.util.HttpUtil;
import com.jin123d.util.JsoupUtil;
import com.jin123d.util.okgo.JsoupCallBack;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ZjsjActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private ListView lv_zjsj;
    private ZjsjAdapter adapter;
    private List<ZjsjModels> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zjsj);
        getData();
    }

    @Override
    protected void initView() {
        lv_zjsj = (ListView) findViewById(R.id.lv_zjsj);
    }

    @Override
    protected void initData() {
        progressDialog = new ProgressDialog(ZjsjActivity.this);
        progressDialog.setMessage(getString(R.string.getData));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        lists = new ArrayList<>();
        adapter = new ZjsjAdapter(lists, ZjsjActivity.this);
        lv_zjsj.setAdapter(adapter);
    }


    private void getData() {
        HttpUtil.getZjsj(this, new JsoupCallBack() {
            @Override
            public void onSuccess(Document document, Call call, Response response) {
                progressDialog.dismiss();
                if (getString(R.string.webTitle).equals(document.title())) {
                    Toast.makeText(ZjsjActivity.this, getString(R.string.loginFail),
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else if (getString(R.string.webTitleError).equals(document.title())) {
                    String ErrorContent = document.body().text();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ZjsjActivity.this);
                    dialog.setTitle(document.title());
                    dialog.setMessage(ErrorContent);
                    dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ZjsjActivity.this.finish();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.create().show();
                } else {
                    lists.clear();
                    lists.addAll(JsoupUtil.getZjsj(document));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


}
