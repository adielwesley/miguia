package devimap.com.miguiaprevia;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nossopc on 23/09/16.
 */
public class TagMap implements Serializable {
    private int mapID;
    private String mapFileName;
    private String mapPlaceName;
    private int graph[][];
    private int amountOfTags;
    private ArrayList<Tag> tagList;

    private int[] caminho;
    private String direction;
    private Tag tagAtual;
    private Tag tagAnterior;
    private Tag tagProxima;
    private int TagDestino;
    //private TagDAO tagDAO;

    public TagMap() {}

    public TagMap(int mapID, String mapPlaceName, int amountOfTags, int[][] adjMatrix, ArrayList<Tag> tagList/*, Context context*/) {
        this.mapID = mapID;
        this.mapFileName = mapPlaceName;
        this.mapPlaceName = mapPlaceName;
        this.amountOfTags = amountOfTags;
        this.graph = adjMatrix;
        this.tagList = tagList;

        //tagDAO = new TagDAO(context);
    }

    public int getMapID() {
        return mapID;
    }

    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    public String getMapFileName() {
        return mapFileName;
    }

    public void setMapFileName(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    public String getMapPlaceName() {
        return mapPlaceName;
    }

    public void setMapPlaceName(String mapPlaceName) {
        this.mapPlaceName = mapPlaceName;
    }

    public int[][] getGraph() {
        return graph;
    }

    public void setGraph(int[][] graph) {
        this.graph = graph;
    }

    public int getAmountOfTags() {
        return amountOfTags;
    }

    public void setAmountOfTags(int amountOfTags) {
        this.amountOfTags = amountOfTags;
    }

    public ArrayList<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<Tag> tagList) {
        this.tagList = tagList;
    }

    /*

    void initializeGraph () {
        graph = new int[][]{
                {0,0,0,0,0,0,1},
                {0,0,0,1,0,0,0},
                {0,0,0,1,1,0,0},
                {0,1,1,0,0,0,1},
                {0,0,1,0,0,1,0},
                {0,0,0,0,1,0,0},
                {1,0,0,1,0,0,0}};
    }

    void initializeTags() {

        // inicializa os ids das tags
        for (int i = 0; i< amountOfTags; i++){
            tag[i].setId(i);
        }

        //inicializa as posicoes das tags na matriz imaginária de tags
        initializeTagsPosition(tag);
        initializeTagsKeys();
        defineIntersection();

    }

    private void initializeTagsKeys() {
        tag[0].setKey("6A0B5C4A");
        tag[1].setKey("16683C07");
        tag[2].setKey("47AAF6D3");
        tag[3].setKey("266A2C5E");
        tag[4].setKey("B20E6224");
        tag[5].setKey("337B4495");
        tag[6].setKey("CD6274AA");
    }*/

    public String getTagKey (int i) {
        if (i>=0 && i<amountOfTags) {
            return tagList.get(i).getKey();
        }
        return  null;
    }

    /*private void defineIntersection() {

        //inicializa as tags de intersection
        tag[3].setIntersection(true);
        tag[4].setIntersection(true);

    }

    public void initializeTagsPosition(Tag[] tag){

        tag[0].setPosi_X(3);
        tag[0].setPosi_Y(2);
        tag[1].setPosi_X(0);
        tag[1].setPosi_Y(2);
        tag[2].setPosi_X(1);
        tag[2].setPosi_Y(1);
        tag[3].setPosi_X(1);
        tag[3].setPosi_Y(2);
        tag[4].setPosi_X(1);
        tag[4].setPosi_Y(0);
        tag[5].setPosi_X(0);
        tag[5].setPosi_Y(0);
        tag[6].setPosi_X(2);
        tag[6].setPosi_Y(2);

    }*/

    // construir caminho solicitado
    int[] findCaminho (int origem, int destino) {
        Dijkstra t = new Dijkstra(amountOfTags, graph);
        //Log.i("Debug", "amountOfTags: " + amountOfTags);
        caminho = t.dijkstra(origem,destino);

        TagDestino = destino;

        return caminho;
    }

    int findNextInThePath (int current) {
        for (int i = 0; i < amountOfTags; i++) {
            if (caminho[i] == current && (i+1)<amountOfTags) {
                return caminho[i+1];
            }
        }
        return -1;
    }

    /*public int getTagIdByHexKey(String hexkey) {
        return tagDAO.getTagIdByHexKey(hexkey, this.mapID);
    }

    public int getTagIdByPlaceName(String placename) {
        return tagDAO.getTagIdByPlaceName(placename.toLowerCase(), this.mapID);
    }*/

    public String getDirection(int prev, int current, int next) {

        tagAtual = tagList.get(current);

        if (tagAtual.getId() == TagDestino) {
            direction = "Você chegou ao destino. Para pedir o trajeto para um novo destino, aperte o botão Falar e diga o nome do local desejado.";
            //Log.i("Onde", "Aqui 0");
        }
        //avaliar orientacao do usuario
        else if (prev==current) {
                direction = "Prossiga adiante";
        }
        else if (next != -1) {
            tagAnterior = tagList.get(prev);
            tagProxima = tagList.get(next);

            //Log.i("Onde", "Aqui X");
            if(tagAtual.getIntersection()){

                //indo esquerda pra direita
                if((tagAnterior.getPosi_X() == tagAtual.getPosi_X())&&(tagAnterior.getPosi_Y() < tagAtual.getPosi_Y())){
                    if(tagProxima.getPosi_X() > tagAtual.getPosi_X()){
                        direction = "Vire à direita e prossiga";
                    }else if (tagProxima.getPosi_X() < tagAtual.getPosi_X()){
                        direction = "Vire à esquerda e prossiga";
                    }else{
                        direction = "Prossiga adiante";
                    }
                    //Log.i("Onde", "Aqui 1");
                }

                // indo direita para esquerda
                else if((tagAnterior.getPosi_X() == tagAtual.getPosi_X())&&(tagAnterior.getPosi_Y() > tagAtual.getPosi_Y())){
                    if(tagProxima.getPosi_X() < tagAtual.getPosi_X()){
                        direction = "Vire à direita e prossiga";
                    }else if (tagProxima.getPosi_X() > tagAtual.getPosi_X()){
                        direction = "Vire à esquerda e prossiga";
                    }else{
                        direction = "Prossiga adiante";
                    }
                    //Log.i("Onde", "Aqui 2");
                }

                //indo de cima para baixo
                else if((tagAnterior.getPosi_Y() == tagAtual.getPosi_Y())&&(tagAnterior.getPosi_X() < tagAtual.getPosi_X())){
                    if(tagProxima.getPosi_Y() < tagAtual.getPosi_Y()){
                        direction = "Vire à direita e prossiga";
                    }else if (tagProxima.getPosi_Y() > tagAtual.getPosi_Y()){
                        direction = "Vire à esquerda e prossiga";
                    }else{
                        direction = "Prossiga adiante";
                    }
                    //Log.i("Onde", "Aqui 3");
                }

                //indo de baixo para cima
                else if((tagAnterior.getPosi_Y() == tagAtual.getPosi_Y())&&(tagAnterior.getPosi_X() > tagAtual.getPosi_X())){
                    if(tagProxima.getPosi_Y() < tagAtual.getPosi_Y()){
                        direction = "Vire à esquerda e prossiga";
                    }else if (tagProxima.getPosi_Y() > tagAtual.getPosi_Y()){
                        direction = "Vire à direita e prossiga";
                    }else{
                        direction = "Prossiga adiante";
                    }
                    //Log.i("Onde", "Aqui 4");
                }
                else {
                    direction = "Prossiga adiante";
                    //Log.i("Onde", "Aqui 5");
                }
            } else{
                direction = "Prossiga adiante";
                //Log.i("Onde", "Aqui 6");
            }
        }

        return direction;
    }
}
