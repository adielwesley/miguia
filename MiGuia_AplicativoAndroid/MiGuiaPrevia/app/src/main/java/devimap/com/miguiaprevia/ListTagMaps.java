package devimap.com.miguiaprevia;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

public class ListTagMaps extends AppCompatActivity implements ListView.OnItemClickListener {
    private TagMapDAO tagMapDAO;
    private String address = null;
    private ProgressDialog progress;
    private MiGuiaPreviaApp app;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter myBluetooth = null;
    private boolean isBtConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tag_maps);

        app = ((MiGuiaPreviaApp)getApplicationContext());

        tagMapDAO = new TagMapDAO(this);
        updateMaps();

        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);
        new ConnectBT().execute(); //Call the class to connect

        app = ((MiGuiaPreviaApp)getApplicationContext());

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a748e")));

        ListView mapsListView = (ListView) findViewById(R.id.id_listView_maps);

        SimpleCursorAdapter mapasAdapter = new SimpleCursorAdapter(this,
                R.layout.mapline,
                tagMapDAO.getTagMaps(),
                new String[]{"mapname"},
                new int[]{R.id.id_mapName},
                0);

        mapsListView.setAdapter(mapasAdapter);
        mapsListView.setOnItemClickListener(this);
    }

    protected void onDestroy() {
        tagMapDAO.closeConnection();
        if (app.getBtSocket()!=null) {
            try {
                app.getBtSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void saveMapInDatabase(String mapFileName) {
        TagMap map = this.extractMapFromFile(mapFileName);
        if (map == null) {
            Log.e("Arquivo","Falha ao abrir arquivo " + mapFileName);
        } else {
            Log.d("Arquivo", "Arquivo " + mapFileName + " lido com sucesso!");
            if (!tagMapDAO.searchMap(map.getMapID())) { // Verifica se esse mapa já existe no banco de dados. Se não existir, insere no banco de dados.
                Log.d("DataBase","Cadastrando Mapa " + map.getMapPlaceName() + " no BD!");
                if(!tagMapDAO.addTagMap(map)) { // Se a inserção não deu certo, entra no bloco.
                    Log.e("database","Erro ao inserir mapa " + mapFileName + " no Banco de Dados");
                    finish();
                }
                else{
                    Log.d("DataBase","Mapa " + mapFileName + " Cadastrado no BD com sucesso!");
                }
            }
            else{
                Log.d("DataBase","Mapa " + mapFileName + " ja está no BD!");
            }
        }
    }

    private void updateMaps() {

        String path = "";

        AssetManager mgr = getResources().getAssets();

        try {

            String list[] = mgr.list(path);
            Log.d("FILES", String.valueOf(list.length) + " itens encontrados no diretório \"assets/" + path +"\"");

            for (String file : list
                 ) {
                saveMapInDatabase(file);
            }

        } catch (IOException e) {
            Log.e("Erro de listagem:", "Não pode listar o diretório \"assets/" + path +"\"");
        }

    }

    public TagMap extractMapFromFile(String mapFileName){

        TagMap map;

        try {
            AssetManager assetManager = getResources().getAssets();
            BufferedReader stream = new BufferedReader(new InputStreamReader(assetManager.open(mapFileName)));

            map = new TagMap();

            map.setMapFileName(mapFileName);
            if(!stream.readLine().equals("devimap"))
                return null;
            map.setMapID(Integer.parseInt(stream.readLine()));
            map.setMapPlaceName(stream.readLine());
            Log.d("Debug", map.getMapPlaceName());
            map.setAmountOfTags(Integer.parseInt(stream.readLine()));
            Log.d("Debug", "Quantd. de Tags " + map.getAmountOfTags());

            ArrayList<Tag> tagList = new ArrayList<>();
            for (int i=0;i<map.getAmountOfTags();i++) {
                Tag tag = new Tag();
                tag.setId(Integer.parseInt(stream.readLine()));
                tag.setPosi_X(Integer.parseInt(stream.readLine()));
                tag.setPosi_Y(Integer.parseInt(stream.readLine()));
                if (Integer.parseInt(stream.readLine()) == 1)
                    tag.setIntersection(true);
                else
                    tag.setIntersection(false);
                tag.setPlace(stream.readLine());
                tag.setKey(stream.readLine());
                tagList.add(tag);
            }

            map.setTagList(tagList);

            int[][] graph = new int[map.getAmountOfTags()][map.getAmountOfTags()];

            for(int i=0;i<map.getAmountOfTags();i++) {
                for (int j=0;j<map.getAmountOfTags();j++) {
                    graph[i][j] = Integer.parseInt(stream.readLine());
                }
            }

            map.setGraph(graph);

        }catch(IOException ex) {
            //ex.printStackTrace();
            return null;
        }

        return map;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView mapNameTextView = (TextView)view.findViewById(R.id.id_mapName);
        Log.i("ListView", "Voce clicou no mapa " + mapNameTextView.getText().toString());
        TagMap tagMap = tagMapDAO.getTagMap(mapNameTextView.getText().toString());
        if (tagMap!=null) {
            for (Tag tag:
                    tagMap.getTagList()) {
                String tags = "Tag: " + tag.getId() + ", X: " + tag.getPosi_X() + " Y: "+ tag. getPosi_Y() + ", Place: " + tag.getPlace() + ", Hex: " + tag.getKey() + ", Intersection: " + tag.getIntersection();
                Log.i("Tags", tags);
            }
            Intent intent = new Intent(this, SpeakDetinationAcivity.class);
            intent.putExtra("mapObject", tagMap);
            startActivity(intent);
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ListTagMaps.this, "Conectando...", "Por favor, aguarde!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (app.getBtSocket() == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    app.setBtSocket(dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID));//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    app.getBtSocket().connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                app.msg("A conexão falhou. O Bluetooth é SSP? Tente de novo.");
                app.speechText("A conexão falhou. Tente de novo.");
                finish();
            }
            else
            {
                isBtConnected = true;
                String wellcome = "Bem vindo ao Mi Guia! Escolha um mapa para solicitar o caminho.";
                app.speechText(wellcome);
            }
            progress.dismiss();
        }
    }

}
