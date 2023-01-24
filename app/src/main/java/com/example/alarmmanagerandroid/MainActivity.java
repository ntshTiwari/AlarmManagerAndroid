package com.example.alarmmanagerandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.alarmmanagerandroid.broadcast_receiver.AlarmReceiver;
import com.example.alarmmanagerandroid.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MaterialTimePicker picker;
    private Calendar calender;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.selectTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        binding.setAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });

        binding.cancelAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });

        createNotificationChannel();
    }

    private void cancelAlarm() {
        ///////// copied from setAlarm, can probably be reused, as the pendingIntent should exactly match the defined intent
        /// create a Broadcast intent
        Intent intent = new Intent(this, AlarmReceiver.class);

        /// create a pendingIntent with the Broadcast intent
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        /////////

        /// if alarm manager is null, set it. Can be the case if setAlarm, is not called yet
        if(alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /// create a Broadcast intent
        Intent intent = new Intent(this, AlarmReceiver.class);

        /// create a pendingIntent with the Broadcast intent
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        /// setInexactRepeating means will be called roughly around the given time, need be exact,
        /// there are other similar methods for different scenarios
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        /// to use setExact, we need to add extra permissions to androidManifest

        Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
    }

    private void showTimePicker() {
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm time")
                .build();

        picker.show(getSupportFragmentManager(), "Time picker fragment");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedTimeText = (picker.getHour() > 12) ?
                        String.format("%02d",(picker.getHour()-12)) + " : "+ String.format("%02d",picker.getMinute())+" PM" :
                        picker.getHour()+" : "+picker.getMinute()+" AM";
                binding.selectedTime.setText(selectedTimeText);

                /// store the selected time in the calender instance
                calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calender.set(Calendar.MINUTE, picker.getMinute());
                calender.set(Calendar.SECOND, 0);
                calender.set(Calendar.MILLISECOND, 0);
            }
        });
    }


    private void createNotificationChannel() {
        /// this has to be created for android versions greater than oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            /// we create the name of our notification channel
            CharSequence notificationChannelName = "DemoAlarmManagerNotificationChannel";

            String description = "Channel for Alarm Manager";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            /// this NotificationChannel id should be same as that of BroadcastReceiver's notification id
            NotificationChannel channel = new NotificationChannel("notificationChannelId", notificationChannelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}