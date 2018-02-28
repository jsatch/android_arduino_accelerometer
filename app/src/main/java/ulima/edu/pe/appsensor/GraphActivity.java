package ulima.edu.pe.appsensor;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import ulima.edu.pe.appsensor.model.XYZData;
import ulima.edu.pe.appsensor.utils.SensorUtils;

public class GraphActivity extends AppCompatActivity {
    // En esta pantalla debemos hacer 3 cosas:
    // 1. Configurar los elementos de la pantalla.
    // 2. Iniciar la recepción del handset de la trama que envía el arduino mediante Bluetooth
    // 3. Procesar la trama y mostrarla en los gráficos.

    private ProgressDialog pd;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothSocket mBtSocket = null;
    private boolean isBtConnected = false;
    private String mAddress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private long mInitTimestamp = 0;
    private LineGraphSeries<DataPoint> mSeriesX;
    private LineGraphSeries<DataPoint> mSeriesY;
    private LineGraphSeries<DataPoint> mSeriesZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    private void iniciarRecepcionDeBluetooth() {
        new BTConnection(new OnDeviceDataListener() {
            @Override
            public void onDataObtained(byte[] trama) {



            }
        }).execute();
    }

    // ===========================================================================================//

    // NO MODIFICAR A PARTIR DE ACÁ



    private void procesarDataObtenida(byte[] data) {
        XYZData dataxyz = SensorUtils.parseData(data, mInitTimestamp);

        mSeriesX.appendData(new DataPoint(dataxyz.getTimestamp(),
                dataxyz.getX() + 7), false, 1000);
        mSeriesY.appendData(new DataPoint(dataxyz.getTimestamp(),
                dataxyz.getY()+ 7), false, 1000);
        mSeriesZ.appendData(new DataPoint(dataxyz.getTimestamp(),
                dataxyz.getZ() - 84), false, 1000);
        Log.i("data", dataxyz.getX() + "," + dataxyz.getY() + "," + dataxyz.getZ());
    }

    private void configurarElementosPantalla() {
        setContentView(R.layout.activity_graph);

        GraphView graphX = findViewById(R.id.graph_x);
        GraphView graphY = findViewById(R.id.graph_y);
        GraphView graphZ = findViewById(R.id.graph_z);

        graphZ.getViewport().setYAxisBoundsManual(true);
        graphZ.getViewport().setMinY(-10);
        graphZ.getViewport().setMaxY(10);

        mAddress = getIntent().getStringExtra("DEVICE_ADDRESS");

        mSeriesX = new LineGraphSeries<>();
        graphX.addSeries(mSeriesX);
        graphX.getViewport().setXAxisBoundsManual(true);
        graphX.getViewport().setMinX(0);
        graphX.getViewport().setMaxX(1000);


        mSeriesY = new LineGraphSeries<>();
        graphY.addSeries(mSeriesY);
        graphY.getViewport().setXAxisBoundsManual(true);
        graphY.getViewport().setMinX(0);
        graphY.getViewport().setMaxX(1000);

        mSeriesZ = new LineGraphSeries<>();
        graphZ.addSeries(mSeriesZ);
        graphZ.getViewport().setXAxisBoundsManual(true);
        graphZ.getViewport().setMinX(0);
        graphZ.getViewport().setMaxX(1000);
    }

    private class BTConnection extends AsyncTask<Void, Void, Void>{
        private boolean mExito = false;
        private OnDeviceDataListener mListener;

        public BTConnection(OnDeviceDataListener listener){
            mListener = listener;
        }

        @Override
        public void onPreExecute(){
            pd = ProgressDialog.show(GraphActivity.this, "Conectando...",
                    "Realizando la conexión");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (mBtSocket == null || !isBtConnected){
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bd = mBluetoothAdapter.getRemoteDevice(mAddress);

                    mBtSocket = bd.createRfcommSocketToServiceRecord(myUUID);

                    mBluetoothAdapter.cancelDiscovery();
                    try {
                        mBtSocket.connect();

                    }catch(IOException e){
                        Log.e("APPSENSOR", e.getMessage());
                    }
                    publishProgress();
                    mExito = true;

                    mInitTimestamp = new Date().getTime();
                    byte[] buffer = new byte[256];
                    int bytes;

                    while(true){
                        Thread.sleep(1000);

                        InputStream dis = mBtSocket.getInputStream();
                        try {
                            bytes = dis.read(buffer);

                            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

                            byte[] xyzBuffer = new byte[3];
                            int first = bais.read();
                            if (first == 97){
                                Log.i("DATA", "entro");
                                bais.read(xyzBuffer);
                                mListener.onDataObtained(xyzBuffer);
                            }
                        }catch(Exception ex){
                            Log.e("APPSENSOR", ex.getMessage());
                        }

                    }
                }
            }catch(IOException e){
                mExito = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            isBtConnected = true;
            pd.dismiss();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!mExito){
                Toast.makeText(getApplicationContext(), "Conexión Fallida. Vuelva a intentar!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            pd.dismiss();
        }
    }

    public interface OnDeviceDataListener{
        public void onDataObtained(byte[] trama);
    }
}
