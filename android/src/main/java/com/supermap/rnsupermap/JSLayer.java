package com.supermap.rnsupermap;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.supermap.data.Dataset;
import com.supermap.data.Recordset;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerGroup;
import com.supermap.mapping.LayerSetting;
import com.supermap.mapping.LayerSettingImage;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Selection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by will on 2016/7/5.
 */
public class JSLayer extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "JSLayer";
    public static Map<String,Layer> mLayerList=new HashMap<String,Layer>();
    Layer mLayer;

    public JSLayer(ReactApplicationContext context){super(context);}

    public static String registerId(Layer layer){
        if(!mLayerList.isEmpty()) {
            for(Map.Entry entry:mLayerList.entrySet()){
                if(layer.equals(entry.getValue())){
                    return (String)entry.getKey();
                }
            }
        }

        Calendar calendar=Calendar.getInstance();
        String id=Long.toString(calendar.getTimeInMillis());
        mLayerList.put(id,layer);
        return id;
    }

    @Override
    public String getName(){
        return REACT_CLASS;
    }

    public static Layer getLayer(String layerId) {
        Layer layer = mLayerList.get(layerId);
        return layer;
    }

    @ReactMethod
    public void setEditable(String layerId,boolean editable,Promise promise){
        try{
            mLayer = mLayerList.get(layerId);
            mLayer.setEditable(editable);
            promise.resolve(true);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getEditable(String layerId,Promise promise){
        try{
            mLayer = mLayerList.get(layerId);
            boolean isEditable = mLayer.isEditable();
            WritableMap map = Arguments.createMap();
            map.putBoolean("isEditable",isEditable);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getName(String layerId,Promise promise){
        try{
            mLayer = mLayerList.get(layerId);
            String layerName = mLayer.getName();

            WritableMap map = Arguments.createMap();
            map.putString("layerName",layerName);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getDataset(String layerId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            Dataset dataset = layer.getDataset();
            String datasetId = JSDataset.registerId(dataset);

            WritableMap map = Arguments.createMap();
            map.putString("datasetId",datasetId);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setDataset(String layerId,String datasetId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            Dataset dataset = JSDataset.getObjById(datasetId);
            layer.setDataset(dataset);

            promise.resolve(true);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getSelection(String layerId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            Selection selection = layer.getSelection();
            String selectionId = JSSelection.registerId(selection);

            Recordset recordset = selection.toRecordset();
            String recordsetId = JSRecordset.registerId(recordset);

            WritableMap map = Arguments.createMap();
            map.putString("selectionId",selectionId);
            map.putString("recordsetId",recordsetId);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setSelectable(String layerId,boolean b,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            layer.setSelectable(b);

            promise.resolve(true);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void isSelectable(String layerId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            boolean selectable = layer.isSelectable();

            WritableMap map = Arguments.createMap();
            map.putBoolean("selectable",selectable);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getVisible(String layerId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            boolean isVisible = layer.isVisible();

            WritableMap map = Arguments.createMap();
            map.putBoolean("isVisible",isVisible);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setVisible(String layerId,boolean b,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            layer.setVisible(b);

            promise.resolve(true);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void isSnapable(String layerId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            boolean isSnapable = layer.isSnapable();

            WritableMap map = Arguments.createMap();
            map.putBoolean("isSnapable",isSnapable);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setSnapable(String layerId,boolean b,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            layer.setSnapable(b);

            promise.resolve(true);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getAdditionalSetting(String layerId,Promise promise){
        try{
            int typeNum;
            Layer layer = mLayerList.get(layerId);
            System.out.println("========getAdditionalSetting========");
            LayerSetting layerSetting = layer.getAdditionalSetting();
            System.out.println("========getAdditionalSetting=====layerSetting===");
            System.out.println("============layerSetting Type===" + layerSetting);
            String layerSettingId = JSLayerSetting.registerId(layerSetting);
            if (layerSetting instanceof LayerSettingVector){
                typeNum = 0;
            }else if (layerSetting instanceof LayerSettingImage){
                typeNum = 1;
            }else {
                typeNum = 2;
            }
            WritableMap map = Arguments.createMap();
            map.putString("_layerSettingId_",layerSettingId);
            map.putInt("type",typeNum);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setAdditionalSetting(String layerId,String layerSettingId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            LayerSetting layerSetting = JSLayerSetting.getObjFromList(layerSettingId);
            layer.setAdditionalSetting(layerSetting);

            promise.resolve(true);
        }catch(Exception e){
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getParentGroup(String layerId,Promise promise){
        try{
            Layer layer = mLayerList.get(layerId);
            LayerGroup layerGroup = layer.getParentGroup();
            String layerGroupId = JSLayer.registerId(layerGroup);

            WritableMap map = Arguments.createMap();
            map.putString("layerGroupId", layerGroupId);
            promise.resolve(map);
        }catch(Exception e){
            promise.reject(e);
        }
    }

//    @ReactMethod
//    public void ungroup(String layerGroupId,Promise promise){
//        try{
//            LayerGroup layer = mLayerList.get(layerGroupId);
//            LayerGroup layerGroup = layer.getParentGroup();
//            String layerGroupId = JSLayer.registerId(layerGroup);
//
//            WritableMap map = Arguments.createMap();
//            map.putString("layerGroupId", layerGroupId);
//            promise.resolve(map);
//        }catch(Exception e){
//            promise.reject(e);
//        }
//    }
}
