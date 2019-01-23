package info.androidhive.dano_notice_board;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BTCommandActivity extends Activity {
    private OutputStream globeOutStream = null;
    private BluetoothSocket datmmSocket = null;
    TextView cmd1;
    List<BluetoothBondedDeviceHelper> dataModel = new ArrayList<BluetoothBondedDeviceHelper>();
    MyArrayAdapter myBondedDevicesArrayAdapter = null;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;

    boolean BT_ON_OFF = false;
    TextView con_status_title;
    private boolean showing_bonded_devices = false;
    private boolean amConnected = false;
    private TextView con_status_sub_title;
    private TextView real_comm;
    private ImageButton con_status_sub_img;
    private ImageView bt_img_send;
    private boolean decFlag=false;
   int global_BT_error_correction =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_btcommand);
        global_BT_error_correction =0;
        cmd1 = (TextView) findViewById(R.id.main_comm_bt);
        con_status_title = (TextView) findViewById(R.id.txt_of_connection);
        con_status_sub_title = (TextView) findViewById(R.id.r_command);
        con_status_sub_img = (ImageButton) findViewById(R.id.connection_est);
        real_comm = (TextView) findViewById(R.id.main_comm_bt);
        bt_img_send = (ImageView) findViewById(R.id.bt_img1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ImageButton btn_connect_bt = (ImageButton) findViewById(R.id.connection_est);
        btn_connect_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   automateVoiceServices();

                Thread Transmit_begin = new ConnectedThread(datmmSocket);
                Transmit_begin.start();
               /* try {
                    String test_comm = real_comm.getText().toString();



                           datmmSocket.getOutputStream().write(test_comm.getBytes());
                           Toast.makeText(getApplicationContext(), "Announcement Sent", Toast.LENGTH_SHORT).show();

                     //Do nothing
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
        String result = getIntent().getExtras().get("command").toString();
        cmd1.setText(result);
        decFlag=getIntent().getExtras().getBoolean("decFlag");

        ListView showingBondedDevices = (ListView) findViewById(R.id.bon_devices_listv);
        showingBondedDevices.setOnItemClickListener(CustormList);

        myBondedDevicesArrayAdapter = new MyArrayAdapter();
        showingBondedDevices.setAdapter(myBondedDevicesArrayAdapter);
    //    autoConnectBT();
        automateVoiceServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            datmmSocket.close();
        }catch (IOException e){

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            datmmSocket.close();
        }catch (IOException e){

        }
    }

    private void automateVoiceServices() {
        if (!mBluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

        } else {
            if (datmmSocket == null && decFlag==true) {
                con_status_title.setTextColor(Color.parseColor("#9054E932"));
                con_status_title.setText("Bluetooth turned on");
                Set<BluetoothDevice> bondedDevices = mBluetoothAdapter
                        .getBondedDevices();
                if (bondedDevices.isEmpty()) {

                    Toast.makeText(getApplicationContext(),
                            "Please Pair the Device first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (showing_bonded_devices == false) {
                        for (BluetoothDevice iterator : bondedDevices) {

                            BluetoothBondedDeviceHelper bond = new BluetoothBondedDeviceHelper();

                            bond.setName(iterator.getName());
                            bond.setMAC_address(iterator.getAddress());
                            myBondedDevicesArrayAdapter.add(bond);
                            if (iterator.getAddress()
                                    //20:16:03:30:98:38 SURVEY ROBOT
                                    //00:21:13:01:1A:A0
                                    .equals("00:21:13:01:1A:A0")) {
                                mBTDevice = iterator;


                                break;

                            }
                        }

                        showing_bonded_devices = true;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Already showing bonded Devices",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                autoConnectBT();


            } else {
             //   sendMyCommand();


            }
        }
    }

    private void sendMyCommand() {
        Thread Transmit_begin = new ConnectedThread(datmmSocket);
        Transmit_begin.start();
        try {
            String test_comm = real_comm.getText().toString();
            if (test_comm.equals("open")) {
                datmmSocket.getOutputStream().write("1".toString().getBytes());
                Toast.makeText(getApplicationContext(), "Command Sent", Toast.LENGTH_SHORT).show();
            } else if (test_comm.equals("lock")) {
                datmmSocket.getOutputStream().write("0".toString().getBytes());
                Toast.makeText(getApplicationContext(), "Command Sent", Toast.LENGTH_SHORT).show();

            } else ; //Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!(requestCode == 1 && resultCode == RESULT_OK)) {
            //Attempt establishing connection here.

        } else {

            con_status_title.setTextColor(Color.GREEN);
        }
    }


    private AdapterView.OnItemClickListener CustormList = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            BluetoothBondedDeviceHelper r = dataModel.get(position);

            // autoConnectBT();

        }

    };

    private void autoConnectBT() {
        if (datmmSocket == null && mBluetoothAdapter.isEnabled()) {
            Thread create_SPP_con = new ConnectThread(mBTDevice);
            create_SPP_con.start();
        } else {
            Toast.makeText(getApplicationContext(), "Connected to HC-05 already", Toast.LENGTH_SHORT).show();

            // Implement write section here.
        }
    }

    class MyArrayAdapter extends ArrayAdapter<BluetoothBondedDeviceHelper> {

        public MyArrayAdapter() {
            super(BTCommandActivity.this, R.layout.bonded_device_list, dataModel);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int position, View Convertview, ViewGroup parent) {
            ViewHolder holder;
            View row = Convertview;
            if (row == null) {
                LayoutInflater inflater = BTCommandActivity.this.getLayoutInflater();
                row = inflater.inflate(R.layout.bonded_device_list, parent,
                        false);
                holder = new ViewHolder();
                holder.deviceName = (TextView) row
                        .findViewById(R.id.DeviceNamefromList);
                holder.deviceMAC = (TextView) row
                        .findViewById(R.id.deviceMACfromList);
                row.setTag(holder);

            } else {
                holder = (ViewHolder) row.getTag();
            }
            BluetoothBondedDeviceHelper blueT_data = dataModel.get(position);


            if (blueT_data != null) {

                holder.deviceName.setText(blueT_data.getName());
                holder.deviceMAC.setText(blueT_data.getMAC_address());

            }
            return row;

        }
    }

    static class ViewHolder {
        // For List in MainActivity class
        TextView deviceName;
        TextView deviceMAC;

        // For List StoreData class
        TextView obID;
        TextView temperature;
        TextView gasIntensity;
        TextView rec_time;
        TextView rec_date;

    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID
                .fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
            datmmSocket = mmSocket;
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            android.util.Log.d("Turnyur", "got it");
            try {
                mmSocket.connect();
                amConnected = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        con_status_title.setText("Established Connection with the Microcontroller");
                        con_status_sub_title.setTextColor(Color.parseColor("#9054E932"));
                        //con_status_sub_title.setText("Transmission medium ready");
                        real_comm.setBackgroundColor(Color.argb(220, 34, 63, 00));
                        con_status_sub_img.setImageResource(R.drawable.apps_xboxsmartglass);
                    }
                });

                //automating send
                if(mmSocket.isConnected()) {


                    Thread Transmit_begin = new ConnectedThread(datmmSocket);
                    Transmit_begin.start();


                    try {

                        String test_comm = real_comm.getText().toString();



                        datmmSocket.getOutputStream().write(test_comm.getBytes());
                       // Toast.makeText(getApplicationContext(), "Announcement Sent", Toast.LENGTH_SHORT).show();

                        //Do nothing
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }




            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                //return;


            }


        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            globeOutStream = mmOutStream;


        }

        @Override
        public void run() {


        }

        public void mywrite(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


}
