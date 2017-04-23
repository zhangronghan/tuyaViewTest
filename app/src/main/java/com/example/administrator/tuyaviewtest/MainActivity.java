package com.example.administrator.tuyaviewtest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,onSaveFinishListener, SeekBar.OnSeekBarChangeListener {
    private FrameLayout mFrameLayout;
    private Button btnSave;
    private Button btnUndo;
    private Button btnRecover;
    private Button btnredo;
    private Button btnpaintStyle;
    private Button btnpaintSize;
    private Button btnpaintColor;
    private MyView myView;
    private SeekBar mSeekBar;
    private int select_paint_color_index = 0;
    private int select_paint_style_index = 0;
    private String paintStyle[]={"画笔","橡皮擦"};
    private String paintColor[]={"红色","蓝色","绿色","黄色","黑色"};
    private int seekbarNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListener();
        init();

    }

    private void init() {
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight();
        myView = new MyView(this,this,screenWidth,screenHeight);
        mFrameLayout.addView(myView);
        myView.requestFocus();

    }


    private void initViews() {
        btnSave= (Button) findViewById(R.id.btn_save);
        btnUndo= (Button) findViewById(R.id.btn_undo);
        btnredo= (Button) findViewById(R.id.btn_redo);
        btnRecover= (Button) findViewById(R.id.btn_recover);
        btnpaintColor= (Button) findViewById(R.id.btn_paintColor);
        btnpaintSize= (Button) findViewById(R.id.btn_paintSize);
        btnpaintStyle= (Button) findViewById(R.id.btn_paintStyle);
        mFrameLayout= (FrameLayout) findViewById(R.id.framelayout);
        mSeekBar= (SeekBar) findViewById(R.id.seekbar);
    }

    private void initListener() {
        btnSave.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnpaintStyle.setOnClickListener(this);
        btnpaintSize.setOnClickListener(this);
        btnpaintColor.setOnClickListener(this);
        btnRecover.setOnClickListener(this);
        btnredo.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                myView.saveToSDCard();
                break;

            case R.id.btn_undo:
                myView.undo();
                break;

            case R.id.btn_recover:
                myView.recover();
                break;

            case R.id.btn_redo:
                myView.redo();
                break;

            case R.id.btn_paintColor:
                mSeekBar.setVisibility(View.GONE);
                showPaintColorDialog(v);
                break;

            case R.id.btn_paintSize:
                mSeekBar.setVisibility(View.VISIBLE);
                myView.selectPaintSize(seekbarNum);
                break;

            case R.id.btn_paintStyle:
                mSeekBar.setVisibility(View.GONE);
                showPaintStyleDialog(v);
                break;

        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekbarNum=progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("MainActivity","SeekBar Start");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("MainActivity","SeekBar Stop");
    }


    private void showPaintColorDialog(View v) {
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("选择画笔颜色：");
        alert.setSingleChoiceItems(paintColor, select_paint_color_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_color_index=which;
                myView.selectPaintColor(which);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();

    }

    private void showPaintStyleDialog(View parent) {
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        alertBuilder.setTitle("选择画笔或者橡皮擦");
        alertBuilder.setSingleChoiceItems(paintStyle, select_paint_style_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_style_index=which;
                myView.selectPaintStyle(which);
                dialog.dismiss();
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.create().show();
    }

    @Override
    public void onSaveFinish(String absolute) {
        Toast.makeText(this, "图片路径为:"+absolute, Toast.LENGTH_SHORT).show();
    }




}
