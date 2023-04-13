package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mButtonOpen  = null; //Кнопка открытия сокета
    private Button mButtonSend  = null; //Кнопка передачи сообщения через сокет
    private Button mButtonClose = null; //Кнопка закрытия сокета
    private LaptopServer mServer = null; //экземпляр нашего класса LaptopSever
    private static final String LOG_TAG = "myServerApp"; //тег для наших сообщений в логах


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonOpen = (Button) findViewById(R.id.button_open_connection);
        mButtonSend = (Button) findViewById(R.id.button_send_connection);
        mButtonClose = (Button) findViewById(R.id.button_close_connection);

        /* при старте приложения будет активна только одна кнопка - «open», а две остальные - нет.*/

        mButtonSend.setEnabled(false);
        mButtonClose.setEnabled(false);

        mButtonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* создаем объект для работы с сервером*/
                mServer = new LaptopServer();
                /* Открываем соединение. Открытие должно происходить в отдельном потоке от ui */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mServer.openConnection();
                /*
                    устанавливаем активные кнопки для отправки данных
                    и закрытия соедиения. Все данные по обновлению интерфеса должны
                    обрабатывается в Ui потоке, а так как мы сейчас находимся в
                    отдельном потоке, нам необходимо вызвать метод  runOnUiThread()
                */
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    mButtonSend.setEnabled(true);
                                    mButtonClose.setEnabled(true);
                                }
                            });
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                            mServer = null;
                        }
                    }
                }).start();
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServer == null) {
                    Log.e(LOG_TAG, "Сервер не создан");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            /* отправляем на сервер данные */
                            mServer.sendData("Send text to server".getBytes());
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                }).start();
            }
        });

        mButtonClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Закрываем соединение */
                mServer.closeConnection();
                /* устанавливаем неактивными кнопки отправки и закрытия */
                mButtonSend.setEnabled(false);
                mButtonClose.setEnabled(false);
            }
        });
    }

}